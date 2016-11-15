package com.realcomp.prime.record.io;

import com.realcomp.prime.Operations;
import com.realcomp.prime.conversion.ConversionException;
import com.realcomp.prime.record.Record;
import com.realcomp.prime.schema.Field;
import com.realcomp.prime.schema.FieldList;
import com.realcomp.prime.schema.Schema;
import com.realcomp.prime.transform.TransformContext;
import com.realcomp.prime.transform.ValueSurgeon;
import com.realcomp.prime.validation.Severity;
import com.realcomp.prime.validation.ValidationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Creates Records from a String[] using a Schema. The creation of this object is expensive, so you are encouraged to
 * reuse instances as much as possible.
 *
 * @author krenfro
 */
public class RecordFactory{

    private static final Logger logger = Logger.getLogger(RecordFactory.class.getName());

    /**
     * Cache of the parse plan for each unique FieldList. When classifiers are used, the FieldList for a record may
     * differ. The computation of parsing plans is expensive, so this cache is used to store the plans.
     */
    protected Map<FieldList, ParsePlan> parsePlanCache;

    /**
     * Most schemas do not make use of classifiers, so this is an optimization to limit the use of the parsePlanCache.
     */
    protected ParsePlan parsePlan;
    protected Schema schema;
    protected ValueSurgeon surgeon;
    protected TransformContext context;

    public RecordFactory(Schema schema) throws ParsePlanException{

        if (schema == null){
            throw new IllegalArgumentException("schema is null");
        }

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
     * @param fieldList
     * @param data
     * @return
     * @throws ValidationException
     * @throws ConversionException
     */
    public Record build(FieldList fieldList, String[] data) throws ValidationException, ConversionException{

        return build(getParsePlan(fieldList), fieldList, data);
    }
    
     /**
     * Builds a Record from the provided String[] and ParsePlan.
     *
     * @param parsePlan
     * @param fieldList
     * @param data
     * @return
     * @throws ValidationException
     * @throws ConversionException
     */
    public Record build(ParsePlan parsePlan, FieldList fieldList, String[] data) throws ValidationException, ConversionException{
        Objects.requireNonNull(parsePlan);
        
        if (fieldList.size() != data.length){
            throw new ValidationException(
                    "The number of fields in schema does not match prime.",
                    fieldList.size() + " != " + data.length,
                    Severity.HIGH);
        }
        
        Record record = new Record();
        context.setRecord(record);
        int index;

        for (Field field : parsePlan){
            index = fieldList.indexOf(field);
            context.setKey(field.getName());
            record.put(field.getName(), data[index]); //seed record with initial value
            Object value = surgeon.operate(Operations.getOperations(schema, field), context);
            if (value != null){
                try{
                    record.put(field.getName(), field.getType().coerce(value)); //set final value
                }
                catch (ConversionException ex){
                    throw new ConversionException(ex.getMessage() + " for field [" + field.getName() + "]", ex);
                }
            }
        }

        return record;
    }
    
    

    protected final void buildParsePlan() throws ParsePlanException{

        parsePlan = new ParsePlan(schema.getDefaultFieldList());

        if (schema.getFieldLists().size() > 1){
            parsePlanCache = new HashMap<>();
            for (FieldList fieldList : schema.getFieldLists()){
                parsePlanCache.put(fieldList, new ParsePlan(fieldList));
            }
        }
    }

    /**
     * Returns the pre-computed parse plan for the specified FieldList
     *
     * @param fieldList
     * @return fieldList ParsePlan for the specified FieldList
     */
    protected ParsePlan getParsePlan(FieldList fieldList){
        return parsePlanCache == null ? parsePlan : parsePlanCache.get(fieldList);
    }

    public Severity getValidationExceptionThreshold(){
        return context.getValidationExceptionThreshold();
    }

    public void setValidationExceptionThreshold(Severity validationExceptionThreshold){
        context.setValidationExceptionThreshold(validationExceptionThreshold);
    }

}
