package com.realcomp.data.record.io;

import com.realcomp.data.Operations;
import com.realcomp.data.Operation;
import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.conversion.Converter;
import com.realcomp.data.conversion.MissingFieldException;
import com.realcomp.data.conversion.MultiFieldConverter;
import com.realcomp.data.record.Record;
import com.realcomp.data.schema.FileSchema;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.schema.Field;
import com.realcomp.data.validation.Severity;
import com.realcomp.data.validation.ValidationException;
import com.realcomp.data.validation.Validator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Performs operations on values, returning the possibly modified result.
 * 
 * @author krenfro
 */
class ValueSurgeon {
    
    protected static final Logger log = Logger.getLogger(ValueSurgeon.class.getName());

    protected FileSchema schema;
    protected Severity validationExceptionThreshold;
    
    public ValueSurgeon(FileSchema schema){
        this(schema, Severity.getDefault());
    }
    
    public ValueSurgeon(FileSchema schema, Severity validationExceptionThreshold){
        if (schema == null)
            throw new IllegalArgumentException("schema is null");
        if (validationExceptionThreshold == null)
            throw new IllegalArgumentException("validationExceptionThreshold is null");
        this.schema = schema;
        this.validationExceptionThreshold = validationExceptionThreshold;
    }
    
    public ValueSurgeon(ValueSurgeon copy){
        this.schema = copy.schema;
        this.validationExceptionThreshold = copy.validationExceptionThreshold;
    }

    public FileSchema getSchema() {
        return schema;
    }

    public void setSchema(FileSchema schema) {
        if (schema == null)
            throw new IllegalArgumentException("schema is null");
        this.schema = schema;
    }

    public Severity getValidationExceptionThreshold() {
        return validationExceptionThreshold;
    }

    public void setValidationExceptionThreshold(Severity validationExceptionThreshold) {
        if (validationExceptionThreshold == null)
            throw new IllegalArgumentException("validationExceptionThreshold is null");
        
        this.validationExceptionThreshold = validationExceptionThreshold;
    }
    
    
    
    public Object operate(Field field, Record record, Object data) 
            throws ConversionException, ValidationException{
        
        if (field == null)
            throw new IllegalArgumentException("field is null");
        if (record == null)
            throw new IllegalArgumentException("record is null");
        if (data == null)
            throw new IllegalArgumentException("data is null");
        
        Object result = data;
        for (Operation op: Operations.getOperations(schema, field)){
            
            try{
                result = operate(op, result, record);
            }
            catch (ValidationException ex) {
                Severity severity = ((Validator) op).getSeverity();
                
                try{
                    switch(severity){
                        case LOW:
                            log.log(Level.INFO, String.format("%s for [%s] in record [%s]",
                                    new Object[]{ex.getMessage(), field, schema.classify(record).toString(record)}));
                            break;
                        case MEDIUM:
                            log.log(Level.WARNING, String.format("%s for [%s] in record [%s]",
                                    new Object[]{ex.getMessage(), field, schema.classify(record).toString(record)}));
                            break;
                        case HIGH:
                            log.log(Level.SEVERE, String.format("%s for [%s] in record [%s]",
                                    new Object[]{ex.getMessage(), field, schema.classify(record).toString(record)}));
                            break;
                    }
                }
                catch(SchemaException se){
                    throw new ConversionException(se);
                }
                        

                if (severity.ordinal() >= validationExceptionThreshold.ordinal())
                    throw ex;
            }
        }
        
        
        return field.getType().coerce(result); //final type conversion
    }
    

    /**
     * Perform an <i>operation</i> on some data for a <i>Record</i>
     * 
     * @param operation
     * @param data not null
     * @param record May be a partial Record, and useful for a MultiFieldConverter
     * @return the result of the operation. not null.
     * @throws ConversionException
     * @throws ValidationException
     * @throws MissingFieldException 
     */
    protected Object operate(Operation operation, Object data, Record record)
                throws ConversionException, ValidationException, MissingFieldException{

        Object result = data;

        if (operation instanceof Validator){
            ((Validator) operation).validate(data);
        }
        else if (operation instanceof MultiFieldConverter){
            result = ((MultiFieldConverter) operation).convert(data, record);
        }
        else if (operation instanceof Converter){
            result = ((Converter) operation).convert(data);
        }
        else{
            throw new IllegalStateException("Unsupported operaton: " + operation.getClass().getName());
        }

        return result;
    }
}
