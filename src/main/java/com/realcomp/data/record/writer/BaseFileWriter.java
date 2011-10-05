package com.realcomp.data.record.writer;

import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.record.Record;
import com.realcomp.data.record.io.ValueResolver;
import com.realcomp.data.schema.AfterLastSchemaField;
import com.realcomp.data.schema.BeforeFirstSchemaField;
import com.realcomp.data.schema.FileSchema;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.schema.SchemaField;
import com.realcomp.data.validation.Severity;
import com.realcomp.data.validation.ValidationException;
import com.realcomp.data.view.RecordView;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author krenfro
 */
public abstract class BaseFileWriter implements RecordWriter{

    protected static final Logger log = Logger.getLogger(BaseFileWriter.class.getName());

    protected OutputStream out;
    protected String charset = Charset.defaultCharset().name();
    protected FileSchema schema;
    protected Severity validationExceptionThreshold = DEFAULT_VALIDATION_THREASHOLD;
    protected long count;
    protected boolean beforeFirstOperationsRun = false;    
    protected ValueResolver valueResolver;
    
    public BaseFileWriter(){
    }
    
    public BaseFileWriter(BaseFileWriter copy){
        
        try{
            validationExceptionThreshold = copy.validationExceptionThreshold;
            schema = new FileSchema(copy.schema);  
            if (copy.valueResolver != null)
                valueResolver = new ValueResolver(copy.valueResolver);
        }
        catch(SchemaException ex){
            throw new IllegalStateException(ex); //should never happen
        }
        
    }
    
    @Override
    public Severity getValidationExceptionThreshold() {
        return validationExceptionThreshold;
    }

    @Override
    public void setValidationExceptionThreshold(Severity severity) {
        this.validationExceptionThreshold = severity;
        if (valueResolver != null)
            valueResolver.setValidationExceptionThreshold(severity);
    }
    
    @Override
    public void open(OutputStream out) throws IOException{
        open(out, Charset.forName(charset));
    }
    
    @Override
    public void open(OutputStream out, Charset charset) throws IOException{
        if (out == null)
            throw new IllegalArgumentException("out is null");
        this.out = out;
        this.charset = charset == null ? Charset.defaultCharset().name() : charset.name();
        count = 0;        
    }


    @Override
    public void close(){

        try {
            executeAfterLastOperations();
        }
        catch (ValidationException ex) {
            log.log(Level.WARNING, null, ex);
        }
        catch (ConversionException ex) {
            log.log(Level.WARNING, null, ex);
        }

        IOUtils.closeQuietly(out);
        beforeFirstOperationsRun = false;
    }

    protected void executeAfterLastOperations() throws ValidationException, ConversionException{
        
        if (schema != null){            
            assert(valueResolver != null);            
            valueResolver.resolve(new AfterLastSchemaField(), new Record(), "" + this.getCount());            
        }
    }

    protected void executeBeforeFirstOperations() throws ValidationException, ConversionException{

        if (schema != null){
            assert(valueResolver != null);            
            valueResolver.resolve(new BeforeFirstSchemaField(), new Record(), "" + this.getCount());
        }
    }
    
    @Override
    public void setSchema(FileSchema schema) throws SchemaException{
        if (schema == null)
            throw new IllegalArgumentException("schema is null");
        this.schema = schema;
        if (valueResolver == null)
            valueResolver = new ValueResolver(schema, validationExceptionThreshold);
        else
            valueResolver.setSchema(schema);
    }

    @Override
    public FileSchema getSchema(){
        return schema;
    }

    @Override
    public void write(RecordView recordView)
        throws IOException, ValidationException, ConversionException, SchemaException{

        write(recordView.getRecord());
    }
    
    @Override
    public void write(Record record)
            throws IOException, ValidationException, ConversionException, SchemaException{

        if (schema == null)
            throw new IllegalStateException("schema not specified");
        if (record == null)
            throw new IllegalArgumentException("record is null");

        if (!beforeFirstOperationsRun){
            executeBeforeFirstOperations();
            beforeFirstOperationsRun = true;
        }

        write(record, schema.classify(record));
        count++;
    }

    
    protected void write(Record record, List<SchemaField> fields)
            throws ValidationException, ConversionException, IOException{

        for (SchemaField field: fields)
            write(record, field);
    }


    protected abstract void write(Record record, SchemaField field)
       throws ValidationException, ConversionException, IOException;

  
    /**
     * @param data
     * @return first two fields from the record
     */
    protected String getRecordIdentifier(Record record) throws SchemaException{
        if (record == null || record.isEmpty())
            return "";
        String id = "";
        List<SchemaField> fields = schema.classify(record);
        if (fields.size() > 0)
            id = id.concat(record.get(fields.get(0).getName()).toString());
        
        if (fields.size() > 1){
            id = id.concat(":");
            id = id.concat(record.get(fields.get(0).getName()).toString());
        }

        return id;
    }

    /** {@inheritDoc} */
    @Override
    public long getCount(){
        return count;
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
}
