package com.realcomp.data.record.reader;

import com.realcomp.data.Operation;
import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.record.Record;
import com.realcomp.data.record.RecordFactory;
import com.realcomp.data.record.RecordFactoryWorker;
import com.realcomp.data.schema.AfterLastSchemaField;
import com.realcomp.data.schema.BeforeFirstSchemaField;
import com.realcomp.data.schema.FileSchema;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.schema.SchemaField;
import com.realcomp.data.validation.Severity;
import com.realcomp.data.validation.ValidationException;
import com.realcomp.data.view.RecordView;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author krenfro
 */
public abstract class BaseFileReader implements RecordReader{

    private static final Logger logger = Logger.getLogger(BaseFileReader.class.getName());

    protected SkippingBufferedReader reader;
    protected FileSchema schema;
    protected RecordFactory recordFactory;
    protected Severity validationExceptionThreshold = DEFAULT_VALIDATION_THREASHOLD;
    protected int skipLeading = 0;
    protected int skipTrailing = 0;

    @XStreamImplicit
    protected List<String> views;

    @XStreamOmitField
    protected long count;

    @XStreamOmitField
    protected List<Class> viewClasses;
    
    protected String charset = Charset.defaultCharset().name();    

    public BaseFileReader(){
    }
    
    public BaseFileReader(BaseFileReader copy){
        validationExceptionThreshold = copy.validationExceptionThreshold;
        skipLeading = copy.skipLeading;
        skipTrailing = copy.skipTrailing;
    }
    
    @Override
    public Severity getValidationExceptionThreshold() {
        return validationExceptionThreshold;
    }

    @Override
    public void setValidationExceptionThreshold(Severity severity) {
        this.validationExceptionThreshold = severity;
        if (recordFactory != null)
            recordFactory.setValidationExceptionThreshold(severity);
    }

    /**
     *
     * @return number of leading records to skip. default 0
     */
    public int getSkipLeading() {
        return skipLeading;
    }

    /**
     *
     * @param skipLeading number of leading records to skip. >= 0
     */
    public void setSkipLeading(int skipLeading) {
        if (skipLeading < 0)
            throw new IllegalArgumentException(
                    String.format("skipLeading out of range: %s < 0", skipLeading));
        if (reader != null)
            throw new IllegalArgumentException("Cannot setSkipLeading after open()");
        this.skipLeading = skipLeading;
    }

    /**
     *
     * @return number of trailing records to skip. default 0
     */
    public int getSkipTrailing() {
        return skipTrailing;
    }

    /**
     *
     * @param skipTrailing number of trailing records to skip. >= 0
     */
    public void setSkipTrailing(int skipTrailing) {
        if (skipTrailing < 0)
            throw new IllegalArgumentException(
                    String.format("skipTrailing out of range: %s < 0", skipTrailing));
        if (reader != null)
            throw new IllegalArgumentException("Cannot setSkipTrailing after open()");
        this.skipTrailing = skipTrailing;
    }

    @Override
    public void open(InputStream in) throws IOException{
        open(in, Charset.defaultCharset());
    }
    
    @Override
    public void open(InputStream in, Charset charset) throws IOException{
        if (in == null)
            throw new IllegalArgumentException("in is null");
        if (charset == null)
            charset = Charset.defaultCharset();

        close();
        reader = new SkippingBufferedReader(new InputStreamReader(in, charset));
        reader.setSkipLeading(skipLeading);
        reader.setSkipTrailing(skipTrailing);
        this.charset = charset.name();
        count = 0;
    }
     

    @Override
    public void close(){
        IOUtils.closeQuietly(reader);
    }

    protected void executeAfterLastOperations() throws ValidationException, ConversionException{

        if (schema != null){
            List<Operation> operations = schema.getAfterLastOperations();
            if (operations != null && !operations.isEmpty()){
                RecordFactoryWorker worker = new RecordFactoryWorker(validationExceptionThreshold);
                worker.build(new AfterLastSchemaField(), operations, "" + this.getCount(), null);
            }
        }
    }

    protected void executeBeforeFirstOperations() throws ValidationException, ConversionException{

        if (schema != null){
            List<Operation> operations = schema.getBeforeFirstOperations();
            if (operations != null && !operations.isEmpty()){
                RecordFactoryWorker worker = new RecordFactoryWorker(validationExceptionThreshold);
                worker.build(new BeforeFirstSchemaField(), operations, "", null);
            }
        }
    }

    @Override
    public void setSchema(FileSchema schema) throws SchemaException{

        if (schema == null)
            throw new IllegalArgumentException("schema is null");

        this.schema = schema;
        recordFactory = new RecordFactory(schema);
    }

    @Override
    public FileSchema getSchema() {
        return schema;
    }

    protected Record loadRecord(List<SchemaField> fields, String[] data)
            throws ValidationException, ConversionException{

        if (fields == null)
            throw new IllegalArgumentException("fields is null");
        if (data == null)
            throw new IllegalArgumentException("data is null");

        if (fields.size() != data.length)
            throw new ValidationException(
                    "number of fields in schema does not match data.",
                    fields.size() + " != " + data.length,
                    Severity.HIGH);

        return recordFactory.build(fields, data);
    }



    /** {@inheritDoc} */
    @Override
    public long getCount(){
        return count;
    }

    /** {@inheritDoc} */
    @Override
    public RecordView read(Class clazz)
            throws IOException, ValidationException, ConversionException, SchemaException{

        if (clazz == null)
            throw new IllegalArgumentException("clazz is null");

        RecordView retVal = null;

        Record r = read();
        if (r != null){
            try {
                Class view = getViewClassFor(clazz);
                retVal = (RecordView) view.newInstance();
                retVal.setRecord(r);
            }
            catch (InstantiationException ex) {
                throw new ConversionException(ex);
            }
            catch (IllegalAccessException ex) {
                throw new ConversionException(ex);
            }
        }

        return retVal;
    }

    /** {@inheritDoc} */
    @Override
    public List<String> getViews() {
        return views == null ? Collections.EMPTY_LIST : views;
    }

    /** {@inheritDoc} */
    @Override
    public void setViews(List<String> views) {
        this.views = views;
        try {
            if (views != null){
                viewClasses = new ArrayList<Class>();
                for (String view: views)
                    viewClasses.add(Class.forName(view));
            }
        }
        catch (ClassNotFoundException ex) {
            throw new IllegalArgumentException(ex);
        }
    }


    /** {@inheritDoc} */
    @Override
    public boolean supports(Class clazz){

        if (clazz == null)
            throw new IllegalArgumentException("clazz is null");

        if (viewClasses != null){
            for (Class view: viewClasses){
                if (view.isAssignableFrom(clazz))
                    return true;
            }
        }

        return false;
    }

    protected Class getViewClassFor(Class clazz){

        if (viewClasses != null){
            for (Class view: viewClasses){
                if (view.isAssignableFrom(clazz))
                    return view;
            }
        }
        throw new IllegalArgumentException(
                "RecordView class " + clazz.toString() + " is not supported.");
    }

    @Override
    public String getCharset() {
        return charset;
    }

    @Override
    public void setCharset(String charset) {
        if (charset == null)
            throw new IllegalArgumentException("charset is null");
        this.charset = charset;
    }

    
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final BaseFileReader other = (BaseFileReader) obj;
        if (this.schema != other.schema && (this.schema == null || !this.schema.equals(other.schema)))
            return false;
        if (this.validationExceptionThreshold != other.validationExceptionThreshold)
            return false;
        if (this.skipLeading != other.skipLeading)
            return false;
        if (this.skipTrailing != other.skipTrailing)
            return false;
        if (this.views != other.views && (this.views == null || !this.views.equals(other.views)))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + (this.schema != null ? this.schema.hashCode() : 0);
        hash = 17 * hash + (this.validationExceptionThreshold != null
                ? this.validationExceptionThreshold.hashCode() : 0);
        hash = 17 * hash + this.skipLeading;
        hash = 17 * hash + this.skipTrailing;
        hash = 17 * hash + (this.views != null ? this.views.hashCode() : 0);
        return hash;
    }

}
