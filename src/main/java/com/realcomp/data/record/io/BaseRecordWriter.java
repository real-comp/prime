package com.realcomp.data.record.io;

import com.realcomp.data.Operation;
import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.record.Record;
import com.realcomp.data.schema.Field;
import com.realcomp.data.schema.FieldList;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.transform.TransformContext;
import com.realcomp.data.transform.ValueSurgeon;
import com.realcomp.data.validation.ValidationException;
import java.io.IOException;
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

    protected IOContext ioContext;
    protected long count;
    protected boolean beforeFirstOperationsRun = false;   
    protected TransformContext transformContext;
    protected ValueSurgeon surgeon;
    protected int skipLeading = 0;
    protected int skipTrailing = 0;
    
    public BaseRecordWriter(){
        transformContext = new TransformContext();
        surgeon = new ValueSurgeon();
    }
    
    @Override
    public void open(IOContext context) throws IOException, SchemaException{
        if (context == null)
            throw new IllegalArgumentException("context is null");
        
        close();
        this.ioContext = context;
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

        if (ioContext != null && closeAll)
            IOUtils.closeQuietly(ioContext.getOut());
    }

    protected void executeAfterLastOperations() throws ValidationException, ConversionException{
        
        if (ioContext.getSchema() != null){
            List<Operation> operations = ioContext.getSchema().getAfterLastOperations();
            if (operations != null && !operations.isEmpty()){
                transformContext.setRecordCount(this.getCount());
                surgeon.operate(operations, transformContext);
            }
        }

            
    }

    protected void executeBeforeFirstOperations() throws ValidationException, ConversionException{

         if (ioContext.getSchema() != null){
            List<Operation> operations = ioContext.getSchema().getBeforeFirstOperations();            
            if (operations != null && !operations.isEmpty()){
                transformContext.setRecordCount(this.getCount());
                surgeon.operate(operations, transformContext);
            }
        }
    }
    
    
    @Override
    public void write(Record record)
            throws IOException, ValidationException, ConversionException, SchemaException{

        if (ioContext.getSchema() == null)
            throw new IllegalStateException("schema not specified");
        if (record == null)
            throw new IllegalArgumentException("record is null");

        if (!beforeFirstOperationsRun){
            executeBeforeFirstOperations();
            beforeFirstOperationsRun = true;
        }

        write(record, ioContext.getSchema().classify(record));
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
        List<Field> fields = ioContext.getSchema().classify(record);
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
        if (ioContext != null)
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
        if (ioContext != null)
            throw new IllegalArgumentException("Cannot setSkipTrailing after open()");
        this.skipTrailing = skipTrailing;
    }


    /** {@inheritDoc} */
    @Override
    public long getCount(){
        return count;
    }
    
    
    @Override
    public IOContext getIOContext(){
        return ioContext;
    }
}
