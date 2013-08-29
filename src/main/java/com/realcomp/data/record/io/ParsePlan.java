package com.realcomp.data.record.io;

import com.realcomp.data.MultiFieldOperation;
import com.realcomp.data.Operation;
import com.realcomp.data.schema.Field;
import com.realcomp.data.schema.FieldList;
import java.util.ArrayList;
import java.util.List;

/**
 * The parse plan is the order that the Fields should to be parsed from a record. Some converters are
 * MultiFieldConverters and require other fields to be available in the Record before they can succeed.
 * MultiFieldConverters should be parsed after any non-MultiFieldConverters
 *
 * @author krenfro
 */
public class ParsePlan extends ArrayList<Field>{

    public ParsePlan(FieldList fields) throws ParsePlanException{
        super();
        createParsePlan(fields);
    }

    protected final void createParsePlan(FieldList fields) throws ParsePlanException{

        /* first walk the list and add all non-MultiFieldOperations to the list of fields. These Field do not depend on
         * anything other than themselves, so they can be parsed first, in any order.
         */
        List<Field> skipped = new ArrayList<Field>();
        for (Field field : fields){
            if (hasMultiFieldOperation(field)){
                skipped.add(field);
            }
            else{
                add(field);
            }
        }

        /* skipped now contains all the SchemaFields that contain at least one MultiFieldOperation. Try an inelegant
         * brute-force of ordering by making sure all the fields that the Field depends on have already been parsed. If
         * there are circular dependencies in the SchemaFields, this algorithm will fall down and should be revised.
         */
        int attempt = 0;
        while (!skipped.isEmpty()){

            for (Field field : skipped){
                if (getParsedFieldNames().containsAll(getDependentFieldNames(field))){
                    add(field);
                }
            }

            skipped.removeAll(this);
            attempt++;

            if (!skipped.isEmpty() && attempt > 3){
                StringBuilder message = new StringBuilder();
                message.append("Unable to determine parse plan for the following fields: [");
                boolean needDelimiter = false;
                for (Field field : skipped){
                    if (needDelimiter){
                        message.append(",");
                    }
                    needDelimiter = true;
                    message.append(field.getName());
                }
                message.append("]");
                throw new ParsePlanException(message.toString());
            }
        }
    }

    /**
     *
     * @return list of all field names that have been successfully parsed
     */
    protected final List<String> getParsedFieldNames(){
        List<String> fieldNames = new ArrayList<>();
        for (Field f : this){
            fieldNames.add(f.getName());
        }
        return fieldNames;
    }

    /**
     * @return list of field names that the Field needs to be available in the Record for its operations to succeed.
     */
    protected final List<String> getDependentFieldNames(Field field){

        List<String> fieldNames = new ArrayList<>();
        for (Operation op : field.getOperations()){
            if (op instanceof MultiFieldOperation){
                fieldNames.addAll(((MultiFieldOperation) op).getFields());
            }
        }

        return fieldNames;
    }

    /**
     * Determines if one of the Operations in a Field is a MultiFieldOperation.
     *
     * @param field
     * @return true if the Field contains a MultiFieldOperation.
     */
    protected final boolean hasMultiFieldOperation(Field field){
        if (field.getOperations() != null){
            for (Operation op : field.getOperations()){
                if (op instanceof MultiFieldOperation){
                    return true;
                }
            }
        }
        return false;
    }
}
