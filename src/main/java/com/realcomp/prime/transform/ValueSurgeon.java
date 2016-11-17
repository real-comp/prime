package com.realcomp.prime.transform;

import com.realcomp.prime.Operation;
import com.realcomp.prime.conversion.ConversionException;
import com.realcomp.prime.conversion.Converter;
import com.realcomp.prime.conversion.MissingFieldException;
import com.realcomp.prime.conversion.MultiFieldConverter;
import com.realcomp.prime.conversion.RecordConverter;
import com.realcomp.prime.record.Record;
import com.realcomp.prime.record.RecordValueResolver;
import com.realcomp.prime.validation.ValidationException;
import com.realcomp.prime.validation.Validator;
import com.realcomp.prime.validation.file.RecordCountValidator;
import java.util.List;

/**
 * Performs <i>Operations</i> on the value(s) of a field in a Record.
 *
 */
public class ValueSurgeon{

    /**
     * @param context
     * @param operations the operations to perform
     * @return the result of the operation.
     * @throws ConversionException
     * @throws ValidationException
     */
    public Object operate(List<Operation> operations, TransformContext context)
            throws ConversionException, ValidationException{

        if (operations == null){
            throw new IllegalArgumentException("operations is null");
        }
        if (context == null){
            throw new IllegalArgumentException("context is null");
        }

        Record record = context.getRecord();
        String key = context.getKey();

        // The key specified may resolve to multiple values in the record
        Object value = RecordValueResolver.resolve(record, key);

        //the value may be null in the record, but there may still be output if there are
        // converters like concat or constant which can output values with null input.
        for (Operation op : operations){
            value = operate(op, value, context);
        }

        return value;
    }

    /**
     * Perform the requested operation on the data, optionally using the provided Record
     * for multi-field operations.
     *
     * @param operation to be performed
     * @param data      to be operated on
     * @param context   the context of the transformation
     * @return the result of the operation
     * @throws ConversionException
     * @throws ValidationException
     * @throws MissingFieldException
     */
    protected Object operate(Operation operation, Object data, TransformContext context)
            throws ConversionException, ValidationException, MissingFieldException{

        Object result = data;

        if (operation instanceof Validator){
            try{
                if (operation instanceof RecordCountValidator){
                    ((Validator) operation).validate(context.getRecordCount());
                }
                else{
                    ((Validator) operation).validate(data);
                }
            }
            catch (ValidationException ve){
                context.handleValidationException((Validator) operation, ve);
            }
        }
        else if (operation instanceof MultiFieldConverter){
            result = ((MultiFieldConverter) operation).convert(data, context.getRecord());
        }
        else if (operation instanceof RecordConverter){
            ((RecordConverter) operation).convert(context.getRecord());            
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
}
