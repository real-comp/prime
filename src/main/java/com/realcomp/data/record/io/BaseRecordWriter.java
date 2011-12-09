package com.realcomp.data.record.io;

import com.realcomp.data.Operation;
import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.record.Record;
import com.realcomp.data.schema.FileSchema;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.schema.Field;
import com.realcomp.data.schema.FieldList;
import com.realcomp.data.transform.TransformContext;
import com.realcomp.data.transform.ValueSurgeon;
import com.realcomp.data.validation.Severity;
import com.realcomp.data.validation.ValidationException;
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
public abstract class BaseRecordWriter implements RecordWriter{

    protected static final Logger log = Logger.getLogger(BaseRecordWriter.class.getName());

    protected OutputStream out;
    protected String charset = Charset.defaultCharset().name();
    protected FileSchema schema;
    protected long count;
    protected boolean beforeFirstOperationsRun = false;   
    protected TransformContext context;
    protected ValueSurgeon surgeon;
    protected int skipLeading = 0;
    protected int skipTrailing = 0;
    
    public BaseRecordWriter(){
        context = new TransformContext();
        surgeon = new ValueSurgeon();
    }
    
    
    @Override
    public Severity getValidationExceptionThreshold() {
        return context.getValidationExceptionThreshold();
    }

    @Override
    public void setValidationExceptionThreshold(Severity severity) {
        
        context.setValidationExceptionThreshold(severity);
    }
    
    @Override
    public void open(OutputStream out) throws IOException{
        if (out == null)
            throw new IllegalArgumentException("out is null");
        
        close();
        this.out = out;
        beforeFirstOperationsRun = true;
        count = 0;        
    }

    
    @Override
    public void close(){

        close(true);
    }

    @Override
    public void close(boolean closeAll){

        try {
            executeAfterLastOperations();
        }
        catch (ValidationException ex) {
            log.log(Level.WARNING, null, ex);
        }
        catch (ConversionException ex) {
            log.log(Level.WARNING, null, ex);
        }

        if (closeAll)
            IOUtils.closeQuietly(out);
    }

    protected void executeAfterLastOperations() throws ValidationException, ConversionException{
        
        if (schema != null){
            List<Operation> operations = schema.getAfterLastOperations();
            if (operations != null && !operations.isEmpty()){
                context.setRecordCount(this.getCount());
                surgeon.operate(operations, context);
            }
        }

            
    }

    protected void executeBeforeFirstOperations() throws ValidationException, ConversionException{

         if (schema != null){
            List<Operation> operations = schema.getBeforeFirstOperations();            
            if (operations != null && !operations.isEmpty()){
                context.setRecordCount(this.getCount());
                surgeon.operate(operations, context);
            }
        }
    }
    
    @Override
    public void setSchema(FileSchema schema) throws SchemaException{
        if (schema == null)
            throw new IllegalArgumentException("schema is null");
        this.schema = schema;
        context.setSchema(schema);
    }

    @Override
    public FileSchema getSchema(){
        return schema;
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

    
    protected void write(Record record, FieldList fields)
            throws ValidationException, ConversionException, IOException{

        for (Field field: fields)
            write(record, field);
    }


    protected abstract void write(Record record, Field field)
       throws ValidationException, ConversionException, IOException;

  
    /**
     * @param data
     * @return first two fields from the record
     */
    protected String getRecordIdentifier(Record record) throws SchemaException{
        if (record == null || record.isEmpty())
            return "";
        String id = "";
        List<Field> fields = schema.classify(record);
        if (fields.size() > 0)
            id = id.concat(record.get(fields.get(0).getName()).toString());
        
        if (fields.size() > 1){
            id = id.concat(":");
            id = id.concat(record.get(fields.get(0).getName()).toString());
        }

        return id;
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
        if (out != null)
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
        if (out != null)
            throw new IllegalArgumentException("Cannot setSkipTrailing after open()");
        this.skipTrailing = skipTrailing;
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
