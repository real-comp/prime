package com.realcomp.data.record.io;

import com.realcomp.data.Operation;
import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.schema.Schema;
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
 * @author krenfro
 */
public abstract class BaseRecordReaderWriter implements AutoCloseable{

    private static final Logger logger = Logger.getLogger(BaseRecordReaderWriter.class.getName());

    protected RecordFactory recordFactory;
    protected Format format;
    protected long count;
    protected boolean beforeFirstOperationsRun = false;
    protected IOContext context;
    protected Schema schema;

    public BaseRecordReaderWriter(){
        format = new Format();
        format.putDefault("charset", Charset.defaultCharset().name());
        format.putDefault("skipLeading", "0");
        format.putDefault("skipTrailing", "0");
    }

    public BaseRecordReaderWriter(BaseRecordReaderWriter copy){
        this();
    }

    public void open(IOContext context) throws IOException, SchemaException{

        if (context == null){
            throw new IllegalArgumentException("context is null");
        }

        close();
        this.context = context;
        this.schema = context.getSchema();
        format.putAll(context.getAttributes());
        validateAttributes();
        beforeFirstOperationsRun = false;
        count = 0;
        if (schema != null){
            recordFactory = new RecordFactory(schema);
            recordFactory.setValidationExceptionThreshold(context.getValidationExeptionThreshold());
        }
    }

    public void close() throws IOException{
        close(true);
    }

    public void close(boolean closeIOContext) throws IOException{

        try{
            executeAfterLastOperations();
        }
        catch (ValidationException | ConversionException ex){
            logger.log(Level.WARNING, null, ex);
        }

        if (context != null && closeIOContext){
            context.close();
        }

        context = null;
        format.clear();
    }

    protected void executeAfterLastOperations() throws ValidationException, ConversionException{
        if (context != null && schema != null){
            List<Operation> operations = schema.getAfterLastOperations();
            if (operations != null && !operations.isEmpty()){
                TransformContext ctx = new TransformContext();
                ctx.setValidationExceptionThreshold(context.getValidationExeptionThreshold());
                ctx.setRecordCount(count);
                ctx.setSchema(schema);
                ValueSurgeon surgeon = new ValueSurgeon();
                surgeon.operate(operations, ctx);
            }
        }
    }

    protected void executeBeforeFirstOperations() throws ValidationException, ConversionException{
        if (context != null && schema != null){
            List<Operation> operations = schema.getBeforeFirstOperations();
            if (operations != null && !operations.isEmpty()){
                TransformContext ctx = new TransformContext();
                ctx.setValidationExceptionThreshold(context.getValidationExeptionThreshold());
                ctx.setRecordCount(count);
                ctx.setSchema(schema);
                ValueSurgeon surgeon = new ValueSurgeon();
                surgeon.operate(operations, ctx);
            }
        }
    }

    public long getCount(){
        return count;
    }

    public IOContext getIOContext(){
        return context;
    }

    public Charset getCharset(){
        return Charset.forName(format.get("charset"));
    }

    public int getSkipLeading(){
        return Integer.parseInt(format.get("skipLeading"));
    }

    public int getSkipTrailing(){
        return Integer.parseInt(format.get("skipTrailing"));
    }

    /**
     * Validates that all attributes contain valid values.
     *
     * @throws IllegalArgumentException
     */
    protected void validateAttributes(){

        for (String attribute : format.keySet()){
            if (!format.getDefaults().containsKey(attribute)){
                logger.log(
                        Level.WARNING,
                        "Unsupported attribute [{0}] = [{1}]",
                        new Object[]{attribute, format.get(attribute)});
            }
        }

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

    public Map<String, String> getDefaults(){
        return format.getDefaults();
    }


}
