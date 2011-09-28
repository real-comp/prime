package com.realcomp.data.record.io;

import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.record.Aliases;
import com.realcomp.data.record.Record;
import com.realcomp.data.schema.FileSchema;
import com.realcomp.data.schema.SchemaField;
import com.realcomp.data.validation.Severity;
import com.realcomp.data.validation.ValidationException;
import java.util.logging.Logger;

/**
 * 
 * @author krenfro
 */
public class ValueResolver {
    
    protected static final Logger log = Logger.getLogger(ValueResolver.class.getName());

    private ValueSurgeon surgeon;
    
    public ValueResolver(FileSchema schema){
        surgeon = new ValueSurgeon(schema);
    }
    
    public ValueResolver(FileSchema schema, Severity validationExceptionThreshold){
        surgeon = new ValueSurgeon(schema, validationExceptionThreshold);
    }
    
    public ValueResolver(ValueResolver copy){
        surgeon = new ValueSurgeon(copy.surgeon);
    }

    public FileSchema getSchema() {
        return surgeon.getSchema();
    }

    public void setSchema(FileSchema schema) {
        surgeon.setSchema(schema);
    }

    public Severity getValidationExceptionThreshold() {
        return surgeon.getValidationExceptionThreshold();
    }

    public void setValidationExceptionThreshold(Severity validationExceptionThreshold) {
        surgeon.setValidationExceptionThreshold(validationExceptionThreshold);
    }
    
    /**
     * Resolve the specified field from the Record, performing all specified Operations.
     * 
     * @param field
     * @param record
     * @return the resolved value, or null if not found
     * @throws ConversionException
     * @throws ValidationException 
     */
    public Object resolve(SchemaField field, Record record)
            throws ConversionException, ValidationException{
            
        //schema field name is not composite, so resolving first is ok
        Object value = record.get(field.getName());
        
        if (value == null){
            //try aliases
            for (String alias: Aliases.getAliases(field)){
                value = record.get(alias);
                if (value != null){
                    break;
                }
            }
        }
        
        return resolve(field, record, value);
    }
    
    /**
     * Modify the value with the operations defined in the FileSchema for the specified SchemaField.
     * @param field
     * @param record
     * @param value
     * @return the resolved value, or null if not found
     * @throws ConversionException
     * @throws ValidationException 
     */
    public Object resolve(SchemaField field, Record record, Object value)
            throws ConversionException, ValidationException{
            
        return value == null ? null : surgeon.operate(field, record, value);
    }
    
    
    
}
