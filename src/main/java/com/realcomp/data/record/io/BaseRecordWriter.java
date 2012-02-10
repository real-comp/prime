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
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author krenfro
 */
public abstract class BaseRecordWriter implements RecordWriter {

    private static final Logger logger = Logger.getLogger(BaseRecordWriter.class.getName());
    protected IOContext context;
    protected Format format;
    protected long count;
    protected boolean beforeFirstOperationsRun = false;

    public BaseRecordWriter() {
        format = new Format();
        format.putDefaultValue("charset", Charset.defaultCharset().name());
        format.putDefaultValue("skipLeading", "0");
        format.putDefaultValue("skipTrailing", "0");

    }

    @Override
    public void open(IOContext context) throws IOException, SchemaException {
        if (context == null)
            throw new IllegalArgumentException("context is null");

        close();
        this.context = context;
        beforeFirstOperationsRun = true;
        count = 0;
    }

    @Override
    public void close() {
        close(true);
    }

    @Override
    public void close(boolean closeIOContext) {

        try {
            executeAfterLastOperations();
        } catch (ValidationException ex) {
            logger.log(Level.WARNING, null, ex);
        } catch (ConversionException ex) {
            logger.log(Level.WARNING, null, ex);
        }

        if (context != null && closeIOContext)
            context.close();

        context = null;
    }

    protected void executeAfterLastOperations() throws ValidationException, ConversionException {

        if (context.getSchema() != null) {
            List<Operation> operations = context.getSchema().getAfterLastOperations();
            if (operations != null && !operations.isEmpty()) {
                TransformContext ctx = new TransformContext();
                ctx.setValidationExceptionThreshold(context.getValidationExeptionThreshold());
                ctx.setRecordCount(count);
                ctx.setSchema(context.getSchema());
                ValueSurgeon surgeon = new ValueSurgeon();
                surgeon.operate(operations, ctx);
            }
        }
    }

    protected void executeBeforeFirstOperations() throws ValidationException, ConversionException {

        if (context.getSchema() != null) {
            List<Operation> operations = context.getSchema().getBeforeFirstOperations();
            if (operations != null && !operations.isEmpty()) {
                TransformContext ctx = new TransformContext();
                ctx.setValidationExceptionThreshold(context.getValidationExeptionThreshold());
                ctx.setRecordCount(count);
                ctx.setSchema(context.getSchema());
                ValueSurgeon surgeon = new ValueSurgeon();
                surgeon.operate(operations, ctx);
            }
        }
    }

    @Override
    public void write(Record record)
            throws IOException, ValidationException, ConversionException, SchemaException {

        if (context.getSchema() == null)
            throw new IllegalStateException("schema not specified");
        if (record == null)
            throw new IllegalArgumentException("record is null");

        if (!beforeFirstOperationsRun) {
            executeBeforeFirstOperations();
            beforeFirstOperationsRun = true;
        }

        write(record, context.getSchema().classify(record));
        count++;
    }

    protected void write(Record record, FieldList fields)
            throws ValidationException, ConversionException, IOException {

        for (Field field : fields) {
            write(record, field);
        }
    }

    protected abstract void write(Record record, Field field)
            throws ValidationException, ConversionException, IOException;

    /**
     * @param record 
     * @return first two fields from the record
     * @throws SchemaException  
     */
    protected String getRecordIdentifier(Record record) throws SchemaException {
        if (record == null || record.isEmpty())
            return "";
        String id = "";
        List<Field> fields = context.getSchema().classify(record);
        if (fields.size() > 0)
            id = id.concat(record.get(fields.get(0).getName()).toString());

        if (fields.size() > 1) {
            id = id.concat(":");
            id = id.concat(record.get(fields.get(0).getName()).toString());
        }

        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getCount() {
        return count;
    }

    @Override
    public IOContext getIOContext() {
        return context;
    }

    

    protected Charset getCharset(){
        return Charset.forName(format.get("charset"));
    }
    
    protected int getSkipLeading(){
        return Integer.parseInt(format.get("skipLeading"));
    }
    
    protected int getSkipTrailing(){
        return Integer.parseInt(format.get("skipTrailing"));
    }
    
    @Override
    public Map<String, String> getAttributes() {
        return format.filterDefaultValues();
    }
    
    @Override
    public void setAttributes(Map<String, String> attributes) {        
        if (context != null)
            throw new IllegalArgumentException("Cannot set attributes after open()");
        
        format.clear();
        format.putAll(attributes);
        validateAttributes();
    }    
    
    /**
     * Validates that all attributes contain valid values.
     * @throws IllegalArgumentException
     */
    protected void validateAttributes(){        
        getCharset();
        
        int skipTrailing = getSkipTrailing();
        if (skipTrailing < 0){
            throw new IllegalArgumentException(
                    String.format("skipTrailing out of range: %s < 0", skipTrailing));
        }
        
        int skipLeading = getSkipLeading();
        if (skipLeading < 0){
            throw new IllegalArgumentException(
                    String.format("skipLeading out of range: %s < 0", skipLeading));
        }
    }
}
