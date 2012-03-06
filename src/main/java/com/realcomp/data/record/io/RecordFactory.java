package com.realcomp.data.record.io;

import com.realcomp.data.Operation;
import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.record.Record;
import com.realcomp.data.schema.Field;
import com.realcomp.data.schema.Schema;
import com.realcomp.data.schema.FieldList;
import com.realcomp.data.transform.TransformContext;
import com.realcomp.data.transform.ValueSurgeon;
import com.realcomp.data.validation.Severity;
import com.realcomp.data.validation.ValidationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Creates Records from a String[] using a Schema.
 * The creation of this object is expensive, so you are encouraged to reuse instances as much
 * as possible.
 *
 * @author krenfro
 */
public class RecordFactory {

    /**
     * Cache of the parse plan for each unique FieldList.
     * When classifiers are used, the FieldList for a record may
     * differ. The computation of parsing plans is expensive, so
     * this cache is used to store the plans.
     */
    protected Map<FieldList, ParsePlan> parsePlanCache;

    /**
     * Most schemas do not make use of classifiers, so this is an optimization
     * to limit the use of the parsePlanCache.
     */
    protected ParsePlan parsePlan;
    
    protected Schema schema;    
    protected ValueSurgeon surgeon;
    protected TransformContext context;
    
    public RecordFactory(Schema schema) throws ParsePlanException{

        if (schema == null)
            throw new IllegalArgumentException("schema is null");

        this.schema = schema;
        buildParsePlan();        
        surgeon = new ValueSurgeon();
        context = new TransformContext();
        context.setSchema(schema);
    }
    
    /**
     * Build a Record from the provided String[] using the schema's default FieldList
     * 
     * @param data
     * @return
     * @throws ValidationException
     * @throws ConversionException 
     */
    public Record build(String[] data) throws ValidationException, ConversionException{
        return build(schema.getDefaultFieldList(), data);
    }
    
    /**
     * Builds a Record from the provided String[] and FieldList.
     * 
     * @param fieldLists
     * @param data
     * @return
     * @throws ValidationException
     * @throws ConversionException 
     */
    public Record build(FieldList fieldList, String[] data)
            throws ValidationException, ConversionException{

        if (fieldList.size() != data.length)
            throw new ValidationException(
                    "The number of fields in schema does not match data.",
                    fieldList.size() + " != " + data.length,
                    Severity.HIGH);

        Record record = new Record();        
        context.setRecord(record);
        int index = 0;
        
        for (Field field: getParsePlan(fieldList)){
            index = fieldList.indexOf(field);
            context.setKey(field.getName());
            record.put(field.getName(), data[index]); //seed record with initial value
            Object value = surgeon.operate(getOperations(field), context);
            if (value != null){
                try{
                    record.put(field.getName(), field.getType().coerce(value)); //set final value            
                }
                catch(ConversionException ex){
                    throw new ConversionException(ex.getMessage() + " for field [" + field.getName() + "]", ex);
                }
            }
        }
        
        return record;
    }
    
    protected final void buildParsePlan() throws ParsePlanException{
        
        parsePlan = new ParsePlan(schema.getDefaultFieldList());
        
        if (schema.getFieldLists().size() > 1){
            parsePlanCache = new HashMap<FieldList, ParsePlan>();
            for (FieldList fieldList: schema.getFieldLists()){
                parsePlanCache.put(fieldList, new ParsePlan(fieldList));
            }
        }
    }
    
    
    /**
     * Returns the pre-computed parse plan for the specified FieldList
     * @param schemaFieldList
     * @return fieldList ParsePlan for the specified FieldList
     */
    protected ParsePlan getParsePlan(FieldList fieldList){

        return parsePlanCache == null ? parsePlan : parsePlanCache.get(fieldList);
    }

    
    public Severity getValidationExceptionThreshold() {        
        return context.getValidationExceptionThreshold();
    }

    
    public void setValidationExceptionThreshold(Severity validationExceptionThreshold) {
        context.setValidationExceptionThreshold(validationExceptionThreshold);        
    }
    
    
    protected List<Operation> getOperations(Field field){
        
        List<Operation> operations = new ArrayList<Operation>();
        if (schema.getBeforeOperations() != null)
            operations.addAll(schema.getBeforeOperations());
        operations.addAll(field.getOperations());
        if (schema.getAfterOperations() != null)
            operations.addAll(schema.getAfterOperations());
        return operations;
    }
    
}
