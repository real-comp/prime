package com.realcomp.data.record;

import com.realcomp.data.FieldFactory;
import com.realcomp.data.Operation;
import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.conversion.Converter;
import com.realcomp.data.conversion.MissingFieldException;
import com.realcomp.data.conversion.MultiFieldConverter;
import com.realcomp.data.schema.SchemaField;
import com.realcomp.data.validation.Severity;
import com.realcomp.data.validation.ValidationException;
import com.realcomp.data.validation.Validator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author krenfro
 */
public class RecordFactoryWorker {

    protected static final Logger log = Logger.getLogger(RecordFactoryWorker.class.getName());

    protected Severity validationExceptionThreshold;

    /**
     * If a Validator logs a warning above this threshold, then the warning is thrown as
     * a ValidationException. By default, Validators log at Severity.MEDIUM, so all
     * validation failures are simply logged.  This mechanism, along with adjustable
     * Validator severity, allow for configurations where some validations are more
     * important than others.
     * @param validationExceptionThreshold
     */
    public RecordFactoryWorker(Severity validationExceptionThreshold){
        this.validationExceptionThreshold = validationExceptionThreshold;
    }

    public Severity getValidationExceptionThreshold() {
        return validationExceptionThreshold;
    }

    public void setValidationExceptionThreshold(Severity validationExceptionThreshold) {
        if (validationExceptionThreshold == null)
            throw new IllegalArgumentException("validationExceptionThreshold is null");
        this.validationExceptionThreshold = validationExceptionThreshold;
    }
    

    public Object build(SchemaField field, List<Operation> operations, String data, Record record)
            throws ConversionException, ValidationException, MissingFieldException{

        return FieldFactory.create(field.getType(), operate(field.getName(), operations, data, record));
    }


    protected String operate(String fieldName, List<Operation> operations, String data, Record record)
            throws ConversionException, ValidationException, MissingFieldException{

        if (operations == null || operations.isEmpty())
            return data;
        for (Operation op: operations)
            data = operate(fieldName, op, data, record);

        return data;
    }

    protected String operate(String fieldName, Operation op, String data, Record record)
                throws ConversionException, ValidationException, MissingFieldException{

        String result = data;


        if (op instanceof Validator){
            try {
                ((Validator) op).validate(data);
            }
            catch (ValidationException ex) {
                Severity severity = ((Validator) op).getSeverity();
                switch(severity){
                    case LOW:
                        log.log(Level.INFO, "{0} for [{1}] in record [{2}]",
                                new Object[]{ex.getMessage(), fieldName, record.toString()});
                        break;
                    case MEDIUM:
                        log.log(Level.WARNING, "{0} for [{1}] in record [{2}]",
                                new Object[]{ex.getMessage(), fieldName, record.toString()});
                        break;
                    case HIGH:
                        log.log(Level.SEVERE, "{0} for [{1}]  in record [{2}]",
                                new Object[]{ex.getMessage(), fieldName, record.toString()});
                        break;
                }

                if (severity.ordinal() >= validationExceptionThreshold.ordinal())
                    throw ex;
            }
        }
        else if (op instanceof Converter){
            result = ((Converter) op).convert(data);
        }
        else if (op instanceof MultiFieldConverter){
            result = ((MultiFieldConverter) op).convert(data, record);
        }
        else{
            throw new IllegalStateException("Unhandled operator: " + op.getClass().getName());
        }

        return result;
    }
}
