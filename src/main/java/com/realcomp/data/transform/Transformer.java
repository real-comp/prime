package com.realcomp.data.transform;

import com.realcomp.data.MultiFieldOperation;
import com.realcomp.data.Operation;
import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.conversion.Converter;
import com.realcomp.data.conversion.MissingFieldException;
import com.realcomp.data.conversion.MultiFieldConverter;
import com.realcomp.data.record.Record;
import com.realcomp.data.schema.FieldList;
import com.realcomp.data.schema.Field;
import com.realcomp.data.validation.Severity;
import com.realcomp.data.validation.ValidationException;
import com.realcomp.data.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author krenfro
 */
public class Transformer {
    
    private static final Logger logger = Logger.getLogger(Transformer.class.getName());
    
    private List<Operation> before;
    private List<Operation> after;
    private FieldList fields;
    protected Severity validationExceptionThreshold = Severity.getDefault();
    
    public Transformer(){
        fields = new FieldList();
    }
    
    public Transformer(Transformer copy){
        super();
        if (copy.before != null)
            setBefore(copy.before);
        if (copy.after != null)
            setAfter(copy.after);
        setFields(copy.fields);
        validationExceptionThreshold = copy.validationExceptionThreshold;
    }
    
    public void transform(Record record) throws ConversionException, ValidationException{
        
        
        for (Field field: fields){
            Object value = record.get(field.getName());
            if (value == null)
                value = "";
            
            for (Operation op: getOperations(field)){                
                try{
                    value = operate(op, value, record);
                }
                catch (ValidationException ex) {
                    handleValidationException(op, field, record, ex);
                }                
            }
            record.put(field.getName(), value);
        }        
    }
    
    protected List<Operation> getOperations(Field field){
        
        List<Operation> operations = new ArrayList<Operation>();
        if (before != null)
            operations.addAll(before);
        operations.addAll(field.getOperations());
        if (after != null)
            operations.addAll(after);
        return operations;
    }
    
    protected void handleValidationException(Operation op, Field field, Record record, ValidationException ex) 
            throws ValidationException{
        
        Severity severity = ((Validator) op).getSeverity();

        switch(severity){
            case LOW:
                logger.log(Level.INFO, String.format("%s for [%s]",
                        new Object[]{ex.getMessage(), field, fields.toString(record)}));
                break;
            case MEDIUM:
                logger.log(Level.WARNING, String.format("%s for [%s] in record [%s]",
                        new Object[]{ex.getMessage(), field, fields.toString(record)}));
                break;
            case HIGH:
                logger.log(Level.SEVERE, String.format("%s for [%s] in record [%s]",
                        new Object[]{ex.getMessage(), field, fields.toString(record)}));
                break;
        }

        if (severity.ordinal() >= validationExceptionThreshold.ordinal())
            throw ex;
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
    

    public FieldList getFields() {
        return fields;
    }

    public void setFields(FieldList fields) {
        if (fields == null)
            throw new IllegalArgumentException("fields is null");
        this.fields = new FieldList(fields);
    }

    
    /**
     * @return all Operations to perform on all Fields after all Field specific operations are
     * finished, or null if none specified.
     *
     */
    public List<Operation> getAfter() {
        return after;
    }

    /**
     * Set all Operations to perform on all Fields after all Field specific operations are finished.
     * @param after null will clear existing list
     */
    public void setAfter(List<Operation> after) {

        if (after == null){
            this.after = null;
        }
        else{
            if (this.after != null)
                this.after.clear();
            for (Operation op: after)
                addAfter(op);
        }
    }

    /**
     * Add an Operation to the after operations group, to be run after all Field specific
     * Operations are performed.
     * @param op not null
     */
    public void addAfter(Operation op){
        if (op == null)
            throw new IllegalArgumentException("op is null");
        if (after == null)
            after = new ArrayList<Operation>();

        this.after.add(op);
    }
    
    
    /**
     *
     * @return all Operations to perform on all Fields before any Field specific Operations are
     * performed, or null if none specified.
     */
    public List<Operation> getBefore() {
        return before;
    }

    /**
     * Set all Operations to perform on all Fields before any Field specific Operations are
     * performed.
     * @param before null will clear list
     */
    public void setBefore(List<Operation> before) {
        if (before == null){
            this.before = null;
        }
        else{
            if (this.before != null)
                this.before.clear();
            for (Operation op: before)
                addBefore(op);
        }
    }

    /**
     * Add an Operation to the before operations group, to be run before all Field specific
     * Operations are performed.
     * @param op not null, not a MultiFieldOperation
     */
    public void addBefore(Operation op){
        if (op == null)
            throw new IllegalArgumentException("op is null");
        if (op instanceof MultiFieldOperation)
            throw new IllegalArgumentException(
                    "You cannot specify a MultiFieldOperation as a 'before' operation");

        if (before == null)
            before = new ArrayList<Operation>();
        this.before.add(op);
    }

    public Severity getValidationExceptionThreshold() {
        return validationExceptionThreshold;
    }

    public void setValidationExceptionThreshold(Severity validationExceptionThreshold) {
        this.validationExceptionThreshold = validationExceptionThreshold;
    }
    
    

    
}
