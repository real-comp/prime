package com.realcomp.data.record.io;

import com.realcomp.data.Operation;
import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.schema.BeforeFirstField;
import com.realcomp.data.schema.Field;
import com.realcomp.data.schema.FileSchema;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.transform.TransformContext;
import com.realcomp.data.transform.Transformer;
import com.realcomp.data.transform.ValueSurgeon;
import com.realcomp.data.validation.Severity;
import com.realcomp.data.validation.ValidationException;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author krenfro
 */
public abstract class BaseRecordReader implements RecordReader{

    private static final Logger logger = Logger.getLogger(BaseRecordReader.class.getName());

    protected InputStream in;
    protected FileSchema schema;
    protected RecordFactory recordFactory;
    protected Severity validationExceptionThreshold = DEFAULT_VALIDATION_THREASHOLD;
    protected int skipLeading = 0;
    protected int skipTrailing = 0;

    @XStreamOmitField
    protected long count;
    
    protected String charset = Charset.defaultCharset().name();   
    
    protected boolean beforeFirstOperationsRun = false;  
    protected TransformContext context;
    protected ValueSurgeon surgeon;

    public BaseRecordReader(){
        context = new TransformContext();
        surgeon = new ValueSurgeon();
    }
    
    public BaseRecordReader(BaseRecordReader copy){
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
        if (in != null)
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
        if (in != null)
            throw new IllegalArgumentException("Cannot setSkipTrailing after open()");
        this.skipTrailing = skipTrailing;
    }

    @Override
    public void open(InputStream in) throws IOException{
        
        if (in == null)
            throw new IllegalArgumentException("in is null");

        close();       
        this.in = in;
        beforeFirstOperationsRun = false;
        count = 0;
    }
           

    @Override
    public void close(){
        IOUtils.closeQuietly(in);
    }

    protected void executeAfterLastOperations() throws ValidationException, ConversionException{

        if (schema != null){
            List<Operation> operations = schema.getAfterLastOperations();
            if (operations != null && !operations.isEmpty()){
                Transformer transformer = new Transformer();
                List<Field> fields = new ArrayList<Field>();
                fields.add(new BeforeFirstField());
                transformer.setFields(fields);
                transformer.setAfter(operations);
                TransformContext context = new TransformContext();
                context.setValidationExceptionThreshold(validationExceptionThreshold);
                context.setRecordCount(this.getCount());
                context.setSchema(schema);
                transformer.transform(context);
            }
        }
    }

    protected void executeBeforeFirstOperations() throws ValidationException, ConversionException{

        if (schema != null){
            List<Operation> operations = schema.getBeforeFirstOperations();            
            if (operations != null && !operations.isEmpty()){
                List<Field> fields = new ArrayList<Field>();
                fields.add(new BeforeFirstField());
                Transformer transformer = new Transformer();
                transformer.setFields(fields);
                transformer.setBefore(operations);
                TransformContext context = new TransformContext();
                context.setValidationExceptionThreshold(validationExceptionThreshold);
                context.setRecordCount(this.getCount());
                context.setSchema(schema);
                transformer.transform(context);
            }
        }
    }

    @Override
    public void setSchema(FileSchema schema) throws SchemaException{

        if (schema == null)
            throw new IllegalArgumentException("schema is null");

        this.schema = schema;
        recordFactory = new RecordFactory(schema);
        recordFactory.setValidationExceptionThreshold(validationExceptionThreshold);
    }

    @Override
    public FileSchema getSchema() {
        return schema;
    }



    /** {@inheritDoc} */
    @Override
    public long getCount(){
        return count;
    }

    
    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        if (charset == null)
            throw new IllegalArgumentException("charset is null");
        this.charset = charset;
    }

}
