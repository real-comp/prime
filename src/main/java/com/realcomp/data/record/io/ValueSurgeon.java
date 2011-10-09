package com.realcomp.data.record.io;

import com.realcomp.data.Operations;
import com.realcomp.data.Operation;
import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.record.Record;
import com.realcomp.data.schema.FileSchema;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.schema.Field;
import com.realcomp.data.transform.Transformer;
import com.realcomp.data.validation.Severity;
import com.realcomp.data.validation.ValidationException;
import com.realcomp.data.validation.Validator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A Schema aware Transformer that provides better error messages on validation problems.
 * Performs operations on values, returning the possibly modified result.
 * 
 * 
 * @author krenfro
 */
public class ValueSurgeon extends Transformer{
    
    rework me
    
    protected static final Logger log = Logger.getLogger(ValueSurgeon.class.getName());

    protected FileSchema schema;
    
    public ValueSurgeon(FileSchema schema){
        this(schema, Severity.getDefault());
    }
    
    public ValueSurgeon(FileSchema schema, Severity validationExceptionThreshold){
        super();
        if (schema == null)
            throw new IllegalArgumentException("schema is null");
        if (validationExceptionThreshold == null)
            throw new IllegalArgumentException("validationExceptionThreshold is null");
        
        this.schema = schema;
        this.validationExceptionThreshold = validationExceptionThreshold;
    }
    
    public ValueSurgeon(ValueSurgeon copy){
        super(copy);
        this.schema = copy.schema;
    }

    public FileSchema getSchema() {
        return schema;
    }

    public void setSchema(FileSchema schema) {
        if (schema == null)
            throw new IllegalArgumentException("schema is null");
        this.schema = schema;
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
    

    
    @Override
    protected void handleValidationException(Operation op, Field field, Record record, ValidationException ex) 
            throws ValidationException{
        
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
            throw new ValidationException(se);
        }

        if (severity.ordinal() >= validationExceptionThreshold.ordinal())
            throw ex;
    }
    
}
