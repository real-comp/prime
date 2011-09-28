package com.realcomp.data.record.writer;

import com.realcomp.data.DataType;
import com.realcomp.data.Operation;
import com.realcomp.data.record.io.Alias;
import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.conversion.Converter;
import com.realcomp.data.conversion.MultiFieldConverter;
import com.realcomp.data.record.AfterLastRecord;
import com.realcomp.data.record.BeforeFirstRecord;
import com.realcomp.data.record.Record;
import com.realcomp.data.record.io.Aliases;
import com.realcomp.data.record.io.Operations;
import com.realcomp.data.record.io.ValueResolver;
import com.realcomp.data.schema.FileSchema;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.schema.SchemaField;
import com.realcomp.data.validation.Severity;
import com.realcomp.data.validation.ValidationException;
import com.realcomp.data.validation.Validator;
import com.realcomp.data.view.RecordView;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
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
        valueResolver = new ValueResolver(validationExceptionThreshold);
    }
    
    public BaseFileWriter(BaseFileWriter copy){
        validationExceptionThreshold = copy.validationExceptionThreshold;
        try{
            if (copy.schema != null)
                schema = new FileSchema(copy.schema);
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
            List<Operation> afterLast = schema.getAfterLastOperations();
            if (afterLast != null){
                for (Operation op: afterLast){
                    operate("(after last record)", op, "" + this.getCount(), new AfterLastRecord());
                }
            }
        }
    }

    protected void executeBeforeFirstOperations() throws ValidationException, ConversionException{

        if (schema != null){
            List<Operation> beforeFirst = schema.getBeforeFirstOperations();
            if (beforeFirst != null){
                for (Operation op: beforeFirst){
                    operate("(before first record)", op, "", new BeforeFirstRecord());
                }
            }
        }
    }
    
    @Override
    public void setSchema(FileSchema schema) throws SchemaException{
        this.schema = schema;
    }

    @Override
    public FileSchema getSchema(){
        return schema;
    }

    @Override
    public void write(RecordView recordView)
        throws IOException, ValidationException, ConversionException{

        write(recordView.getRecord());
    }
    
    @Override
    public void write(Record record)
            throws IOException, ValidationException, ConversionException{

        if (schema == null)
            throw new IllegalStateException("schema not specified");
        if (record == null)
            throw new IllegalArgumentException("record is null");

        if (!beforeFirstOperationsRun){
            executeBeforeFirstOperations();
            beforeFirstOperationsRun = true;
        }

        write(record, schema.getFields());
        count++;
    }

    
    protected void write(Record record, List<SchemaField> fields)
            throws ValidationException, ConversionException, IOException{

        for (SchemaField field: fields)
            write(record, field);
    }


    protected abstract void write(Record record, SchemaField field)
       throws ValidationException, ConversionException, IOException;

    
    protected Object resolve(Record record, SchemaField schemaField)
            throws ValidationException, ConversionException{

        Object value = record.get(schemaField.getName());
        
        if (value == null){
            //try aliases
            for (String alias: Aliases.getAliases(schemaField)){
                value = record.get(alias);
                if (value != null){
                    break;
                }
            }
        }
        
        try{
            value = operate(schemaField.getName(), Operations.getOperations(schema, schemaField), value, record);
        }
        catch(ValidationException ex){
            String message = String.format(
                    "{0} for [{1}] in record [%s]",
                    new Object[]{ex.getMessage(), schemaField.getName(), schema.toString(record)});
            throw new ValidationException(message, ex);
        }
        catch(ConversionException ex){
            String message = String.format(
                    "{0} for [{1}] in record [%s]",
                    new Object[]{ex.getMessage(), schemaField.getName(), schema.toString(record)});
            throw new ConversionException(message, ex);
        }

        return value;
    }

    protected Object operate(SchemaField field, Object value, Record record)
            throws ConversionException, ValidationException{
        
        value = operate(field.getName(), schema.getBeforeOperations(), value, record);
        value = operate(field.getName(), field.getOperations(), value, record);
        value = operate(field.getName(), schema.getAfterOperations(), value, record);
        return value;
    }
    
    protected Object operate(String fieldName, List<Operation> operations, Object value, Record record)
            throws ConversionException, ValidationException{
        
        if (operations == null)
            return value;
        for (Operation op: operations)
            value = operate(fieldName, op, value, record);
        return value;
    }
    

    protected Object operate(String fieldName, Operation op, Object value, Record record)
                throws ConversionException, ValidationException{

        if (op instanceof Validator){
            try {
                ((Validator) op).validate(value);
            }
            catch (ValidationException ex) {
                Severity severity = ((Validator) op).getSeverity();

                if (severity.ordinal() >= validationExceptionThreshold.ordinal())
                    throw ex;
                
                switch(severity){
                    case LOW:
                        log.log(Level.INFO, String.format("%s for [%s] in record [%s]",
                                new Object[]{ex.getMessage(), fieldName, schema.toString(record)}));
                        break;
                    case MEDIUM:
                        log.log(Level.WARNING, String.format("%s for [%s] in record [%s]",
                                new Object[]{ex.getMessage(), fieldName, schema.toString(record)}));
                        break;
                    case HIGH:
                        log.log(Level.SEVERE, String.format("%s for [%s] in record [%s]",
                                new Object[]{ex.getMessage(), fieldName, schema.toString(record)}));
                        break;
                }
            }
        }
        else if (op instanceof Converter){
            value = ((Converter) op).convert(value);
        }
        else if (op instanceof MultiFieldConverter){
            value = ((MultiFieldConverter) op).convert(value, record);
        }
        else{
            throw new IllegalStateException("Unhandled operator: " + op.getClass().getName());
        }

        return value;
    }

    /**
     * @param data
     * @return first two fields from the record
     */
    protected String getRecordIdentifier(Record record){
        if (record == null || record.isEmpty())
            return "";
        String id = "";
        List<SchemaField> fields = schema.getFields();
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
