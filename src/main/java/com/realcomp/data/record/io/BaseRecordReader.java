package com.realcomp.data.record.io;

import com.realcomp.data.Operation;
import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.schema.BeforeFirstField;
import com.realcomp.data.schema.Field;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.transform.TransformContext;
import com.realcomp.data.transform.Transformer;
import com.realcomp.data.validation.ValidationException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author krenfro
 */
public abstract class BaseRecordReader implements RecordReader{

    private static final Logger logger = Logger.getLogger(BaseRecordReader.class.getName());
    
    protected RecordFactory recordFactory;
    protected Format format;
    
    protected long count;
    protected boolean beforeFirstOperationsRun = false;    
    protected IOContext context;
    
    public BaseRecordReader(){
         format = new Format();
         format.putDefaultValue("charset", Charset.defaultCharset().name());
         format.putDefaultValue("skipLeading", "0");
         format.putDefaultValue("skipTrailing", "0");
    }
    
    public BaseRecordReader(BaseRecordReader copy){
        format = new Format(copy.format);
    }
    

    @Override
    public void open(IOContext context) throws IOException, SchemaException{
        if (context == null)
            throw new IllegalArgumentException("context is null");

        close();        
        if (context.getSchema() != null){
            recordFactory = new RecordFactory(context.getSchema());
            recordFactory.setValidationExceptionThreshold(context.getValidationExeptionThreshold());
        }
        
        this.context = context;
        beforeFirstOperationsRun = false;
        count = 0;
    }
           

    @Override
    public void close(){
        close(true);
    }

    @Override
    public void close(boolean closeIOContext){

        try {
            executeAfterLastOperations();
        }
        catch (ValidationException ex) {
            logger.log(Level.WARNING, null, ex);
        }
        catch (ConversionException ex) {
            logger.log(Level.WARNING, null, ex);
        }

        if (context != null && closeIOContext)
            context.close();
        
        context = null;
    }
    
    
    protected void executeAfterLastOperations() throws ValidationException, ConversionException{

        if (context.getSchema() != null){
            List<Operation> operations = context.getSchema().getAfterLastOperations();
            if (operations != null && !operations.isEmpty()){
                Transformer transformer = new Transformer();
                List<Field> fields = new ArrayList<Field>();
                fields.add(new BeforeFirstField());
                transformer.setFields(fields);
                transformer.setAfter(operations);
                TransformContext xCtx = new TransformContext();
                xCtx.setValidationExceptionThreshold(context.getValidationExeptionThreshold());
                xCtx.setRecordCount(count);
                xCtx.setSchema(context.getSchema());
                transformer.transform(xCtx);
            }
        }
    }

    protected void executeBeforeFirstOperations() throws ValidationException, ConversionException{

        if (context.getSchema() != null){
            List<Operation> operations = context.getSchema().getBeforeFirstOperations();            
            if (operations != null && !operations.isEmpty()){
                List<Field> fields = new ArrayList<Field>();
                fields.add(new BeforeFirstField());
                Transformer transformer = new Transformer();
                transformer.setFields(fields);
                transformer.setBefore(operations);
                TransformContext xCtx = new TransformContext();
                xCtx.setValidationExceptionThreshold(context.getValidationExeptionThreshold());
                xCtx.setRecordCount(count);
                xCtx.setSchema(context.getSchema());
                transformer.transform(xCtx);
            }
        }
    }

    
    /** {@inheritDoc} */
    @Override
    public long getCount(){
        return count;
    }
    
    @Override
    public IOContext getIOContext(){
        return context;
    }

    @Override
    public Map<String, String> getAttributes() {
        return format.filterDefaultValues();
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
