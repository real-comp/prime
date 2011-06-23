package com.realcomp.data.record;

import com.realcomp.data.Operation;
import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.conversion.MissingFieldException;
import com.realcomp.data.schema.Classifier;
import com.realcomp.data.schema.FileSchema;
import com.realcomp.data.schema.KeyFieldIntrospector;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.schema.SchemaField;
import com.realcomp.data.validation.Severity;
import com.realcomp.data.validation.ValidationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Creates Records from a String[] using a FileSchema.
 *
 * @author krenfro
 */
public class RecordFactory {

    /**
     * Stores the list of key fieldnames for a particular list of SchemaFields.
     * When classifiers are used, the keys for a record may change.  The
     * computation of key fields is expensive, so its cached.
     */
    protected Map<List<SchemaField>, List<String>> keyFieldCache;

    /**
     * The list of key fields for schemas that do not make use of classifiers.
     * Most schemas will not make use of classifiers, and will therefore
     * have the same key fields for every record.
     * This is an optimization to limit the use of keyFieldCache
     */
    protected List<String> keyFields;

    /**
     * Cache of the parse plan for each unique list of SchemaFields.
     * When classifiers are used, the SchemaFields for a record may
     * differ. The computation of parsing plans is expensive, so
     * this cache is used to store the plans.
     */
    protected Map<List<SchemaField>, ParsePlan> parsePlanCache;

    /**
     * Most schemas do not make use of classifiers, so this is an optimization
     * to limit the use of the parsePlanCache.
     */
    protected ParsePlan parsePlan;

    protected FileSchema schema;
    protected RecordFactoryWorker worker;

    protected Severity validationExceptionThreshold = Severity.HIGH;

    public RecordFactory(FileSchema schema) throws ParsePlanException{

        if (schema == null)
            throw new IllegalArgumentException("schema is null");

        this.schema = schema;
        buildKeyFieldCache();
        buildParsePlan();
        worker = new RecordFactoryWorker(validationExceptionThreshold);
    }
    
    public Record build(List<SchemaField> fields, String[] data)
            throws ValidationException, ConversionException{

        if (fields.size() != data.length)
            throw new ValidationException(
                    "number of fields in schema does not match data.",
                    fields.size() + " != " + data.length,
                    Severity.HIGH);


        Record record = new Record(getKeyFields(fields));

        int index = 0;
        List<SchemaField> schemaFields = getParsePlan(fields).getFields();
        for (SchemaField schemaField: schemaFields){
            index = fields.indexOf(schemaField);
            Object value = null;
            try{
                value = worker.build(
                        schemaField, getOperations(schemaField), data[index], record);
            }
            catch (ConversionException e){
                throw new ConversionException(
                        e.getMessage() + " in field [" + schemaField + "]");
            }

            record.put(schemaField.getName(), value);
        }


        return record;
    }


    protected List<Operation> getOperations(SchemaField field)
            throws MissingFieldException{

        List<Operation> operations = new ArrayList<Operation>();
        List<Operation> temp = schema.getBeforeOperations();
        if (temp != null)
            operations.addAll(temp);

        temp = field.getOperations();
        if (temp != null)
            operations.addAll(temp);

        temp = schema.getAfterOperations();
        if (temp != null)
            operations.addAll(temp);
        return operations;
    }



    protected final void buildKeyFieldCache(){

        keyFields = KeyFieldIntrospector.getKeyFieldnames(schema.getFields());

        //there are classifiers, so I need to use the slower keyFieldCache
        if (!schema.getClassifiers().isEmpty()){
            keyFieldCache = new HashMap<List<SchemaField>, List<String>>();
            keyFieldCache.put(schema.getFields(), keyFields);
            for (Classifier c: schema.getClassifiers())
                keyFieldCache.put(c.getFields(), KeyFieldIntrospector.getKeyFieldnames(c.getFields()));
        }
    }

    protected final void buildParsePlan() throws ParsePlanException{

        parsePlan = new ParsePlan(schema.getFields());

        if (!schema.getClassifiers().isEmpty()){
            parsePlanCache = new HashMap<List<SchemaField>, ParsePlan>();
            parsePlanCache.put(schema.getFields(), parsePlan);
            for (Classifier c: schema.getClassifiers())
                parsePlanCache.put(c.getFields(), new ParsePlan(c.getFields()));
        }
    }

    /**
     * A <i>key</i> field is identified as having a <i>Key</i> validator operation.
     *
     * @param fields
     * @return the <i>key</i> fields from the list of fields.
     */
    protected List<String> getKeyFields(List<SchemaField> fields){

        List<String> keys = keyFields;
        if (keyFieldCache != null)
            keys = keyFieldCache.get(fields);


        return keys;
    }


    protected ParsePlan getParsePlan(List<SchemaField> fields){

        ParsePlan plan = parsePlan;
        if (parsePlanCache != null)
            plan = parsePlanCache.get(fields);
        return plan;
    }

    public Severity getValidationExceptionThreshold() {
        return validationExceptionThreshold;
    }

    public void setValidationExceptionThreshold(Severity validationExceptionThreshold) {
        if (validationExceptionThreshold == null)
            throw new IllegalArgumentException("validationExceptionThreshold == null");
        this.validationExceptionThreshold = validationExceptionThreshold;
    }
}
