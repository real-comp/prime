package com.realcomp.data.transform;

import com.realcomp.data.Operation;
import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.conversion.Converter;
import com.realcomp.data.conversion.MissingFieldException;
import com.realcomp.data.conversion.MultiFieldConverter;
import com.realcomp.data.record.Record;
import com.realcomp.data.record.RecordKey;
import com.realcomp.data.validation.ValidationException;
import com.realcomp.data.validation.Validator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Performs <i>Operations</i> on the value of a field in a Record.
 * 
 * @author krenfro
 */
public class ValueSurgeon {
   
    private static final Logger logger = Logger.getLogger(ValueSurgeon.class.getName());
            
    private Object defaultValue;
    
    /**
     * @param record holds a value to be operated on
     * @param key the key of the value in the record to operate on
     * @param operations the operations to perform
     * @return the result of the operation. may be null
     * @throws ConversionException
     * @throws ValidationException 
     */
    public Object operate(Record record, RecordKey key, List<Operation> operations) 
            throws ConversionException, ValidationException{
        
        if (record == null)
            throw new IllegalArgumentException("record is null");
        if (key == null)
            throw new IllegalArgumentException("key is null");
        if (operations == null)
            throw new IllegalArgumentException("operations is null");
        
        Object value = record.get(key.getKey());
        if (value == null && defaultValue != null){
            logger.log(
                    Level.FINE, 
                    "Using default value of [{0}] for [{1}] in Record [{2}]", 
                        new Object[]{defaultValue, key, record});
            value = defaultValue;
        }
            
        if (value != null){
            for (Operation op: operations){
                value = operate(op, value, record);
            }
        }
        
        return value;
    }

    /**
     * Perform the requested operation on the data, optionally using the provided Record
     * for multi-field operations.
     * 
     * @param operation to be performed
     * @param data to be operated on
     * @param record need for multi-field operations
     * @return the result of the operation
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
            throw new IllegalStateException(
                    "Unsupported operaton: " + operation.getClass().getName());
        }

        return result;
    }

    /**
     * 
     * @return the value to be used when the specified key does not exist in the record. Default null.
     */
    public Object getDefaultValue() {
        return defaultValue;
    }

    /**
     * Set the value to be used when there is no value in the Record for the specified key.
     * Default = null
     * @param defaultValue 
     */
    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

       
}
