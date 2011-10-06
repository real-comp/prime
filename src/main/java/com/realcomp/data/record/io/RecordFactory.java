package com.realcomp.data.record.io;

import com.realcomp.data.record.Aliases;
import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.record.Record;
import com.realcomp.data.schema.FileSchema;
import com.realcomp.data.schema.Field;
import com.realcomp.data.schema.FieldList;
import com.realcomp.data.validation.Severity;
import com.realcomp.data.validation.ValidationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Creates Records from a String[] using a FileSchema.
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
    
    protected Map<String,List<String>> aliases;

    protected FileSchema schema;
    
    private ValueSurgeon surgeon;

    public RecordFactory(FileSchema schema) throws ParsePlanException{

        if (schema == null)
            throw new IllegalArgumentException("schema is null");

        this.schema = schema;
        buildParsePlan();
        aliases = Aliases.getAliases(schema);
        surgeon = new ValueSurgeon(schema);
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
        int index = 0;
        
        for (Field field: getParsePlan(fieldList)){
            index = fieldList.indexOf(field);
            Object value = null;
            try{
                value = surgeon.operate(field, record, data[index]);
            }
            catch (ConversionException ex){
                throw new ConversionException(
                        String.format("%s in field [%s] of record [%s]",
                                new Object[]{ex.getMessage(), field, fieldList.toString(record)}));
            }

            record.put(field.getName(), value);
            addAliases(record, field.getName(), value);
        }


        return record;
    }
    
    /**
     * If aliases are defined for the field, add an entry to the specified record, 
     * with the alias name as the key, and the provided value as the value.
     * 
     * @param record
     * @param fieldName
     * @param value 
     */
    protected void addAliases(Record record, String fieldName, Object value){
        if (aliases.containsKey(fieldName)){
            for (String alias: aliases.get(fieldName)){
                record.put(alias, value);
            }
        }
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

        ParsePlan plan = parsePlan;
        if (parsePlanCache != null)
            plan = parsePlanCache.get(fieldList);
        return plan;
    }

    
    public Severity getValidationExceptionThreshold() {
        return surgeon.getValidationExceptionThreshold();
    }

    
    public void setValidationExceptionThreshold(Severity validationExceptionThreshold) {
        surgeon.setValidationExceptionThreshold(validationExceptionThreshold);        
    }
}
