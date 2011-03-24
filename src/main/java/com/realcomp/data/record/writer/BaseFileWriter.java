package com.realcomp.data.record.writer;

import com.realcomp.data.DataType;
import com.realcomp.data.Field;
import com.realcomp.data.Operation;
import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.conversion.Converter;
import com.realcomp.data.record.Record;
import com.realcomp.data.schema.FileSchema;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.schema.SchemaField;
import com.realcomp.data.validation.Severity;
import com.realcomp.data.validation.ValidationException;
import com.realcomp.data.validation.Validator;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import java.io.IOException;
import java.io.OutputStream;
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
    protected FileSchema schema;
    protected Severity validationExceptionThreshold = DEFAULT_VALIDATION_THREASHOLD;
    protected long count;
    protected boolean beforeFirstOperationsRun = false;
    
    @Override
    public Severity getValidationExceptionThreshold() {
        return validationExceptionThreshold;
    }

    @Override
    public void setValidationExceptionThreshold(Severity severity) {
        this.validationExceptionThreshold = severity;
    }
    
    @Override
    public void open(OutputStream out){
        if (out == null)
            throw new IllegalArgumentException("out is null");
        this.out = out;
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
                    operate(op, "" + this.getCount(), "AFTER LAST RECORD");
                }
            }
        }
    }

    protected void executeBeforeFirstOperations() throws ValidationException, ConversionException{

        if (schema != null){
            List<Operation> afterLast = schema.getBeforeFirstOperations();
            if (afterLast != null){
                for (Operation op: afterLast){
                    operate(op, "", "BEFORE FIRST RECORD");
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


    protected String toString(Record record, SchemaField schemaField)
            throws ValidationException, ConversionException{

        Field field = record.get(schemaField.getName());
        String data = "";
        if (field != null && field.getType() != DataType.NULL)
            data = field.toString();
        
        String recordId = "";
        try{
            data = operate(schema.getBeforeOperations(), data, recordId);
            data = operate(schemaField.getOperations(), data, recordId);
            data = operate(schema.getAfterOperations(), data, recordId);
        }
        catch(ValidationException ex){
            throw new ValidationException(schemaField.getName() + " " + ex.getMessage(), ex);
        }
        catch(ConversionException ex){
            throw new ConversionException(schemaField.getName() + " " + ex.getMessage(), ex);
        }

        return data;
    }

    protected String operate(List<Operation> operations, String data, String recordIdentifier) 
            throws ConversionException, ValidationException{
        
        if (operations == null)
            return data;
        for (Operation op: operations)
            data = operate(op, data, recordIdentifier);
        return data;
    }

    protected String operate(Operation op, String data, String recordIdentifier)
                throws ConversionException, ValidationException{

        if (op instanceof Validator){
            try {
                ((Validator) op).validate(data);
            }
            catch (ValidationException ex) {
                Severity severity = ((Validator) op).getSeverity();
                switch(severity){
                    case LOW:
                        log.log(Level.INFO, "{0} in record [{1}]",
                                new Object[]{ex.getMessage(), recordIdentifier});
                        break;
                    case MEDIUM:
                        log.log(Level.WARNING, "{0} in record [{1}]",
                                new Object[]{ex.getMessage(), recordIdentifier});
                        break;
                    case HIGH:
                        log.log(Level.SEVERE, "{0} in record [{1}]",
                                new Object[]{ex.getMessage(), recordIdentifier});
                        break;
                }

                if (severity.ordinal() >= validationExceptionThreshold.ordinal())
                    throw ex;
            }
        }
        else if (op instanceof Converter){
            data = ((Converter) op).convert(data);
        }
        else{
            throw new IllegalStateException("Unhandled operator: " + op.getClass().getName());
        }

        return data;
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

}
