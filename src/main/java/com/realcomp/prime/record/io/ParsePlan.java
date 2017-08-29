package com.realcomp.prime.record.io;

import com.realcomp.prime.MultiFieldOperation;
import com.realcomp.prime.Operation;
import com.realcomp.prime.schema.Field;
import com.realcomp.prime.schema.FieldList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The parse plan is the order that the Fields should to be parsed from a record. Some converters are
 * MultiFieldConverters and require other fields to be available in the Record before they can succeed.
 * MultiFieldConverters should be parsed after any non-MultiFieldConverters
 * Fields without validations should be parsed before other fields.
 * Key fields should be parsed before other fields.
 *
 */
public class ParsePlan extends ArrayList<Field>{

    public ParsePlan(FieldList fields) throws ParsePlanException{
        super();
        if (fields != null){
            createParsePlan(fields);
        }
    }

    protected List<Field> getMultiFieldOperations(List<Field> fields){
        List<Field> found = new ArrayList<>();
        for (Field field: fields){
            if (hasMultiFieldOperation(field)){
                found.add(field);
            }
        }
        return fields;
    }

    protected final void createParsePlan(FieldList fields) throws ParsePlanException{



        // The FieldParsePlanComparator will put fields in the correct order to be parsed.
        List<Field> ordered = new ArrayList<>();
        ordered.addAll(fields);
        Collections.sort(ordered, new FieldParsePlanComparator());

        /*Nex walk the list and add all non-MultiFieldOperations to the list of fields. These Field do not depend on
         * anything other than themselves, so they can be parsed first, in any order.
         */
        List<Field> multiFieldOps = getMultiFieldOperations(ordered);

        /* multiFieldOps now contains all the Fields that contain at least one MultiFieldOperation. Try an inelegant
         * brute-force of ordering by making sure all the fields that the Field depends on have already been parsed. If
         * there are circular dependencies in the Fields, this algorithm will fall down and should be revised.
         */
        int attempt = 0;
        while (!multiFieldOps.isEmpty()){

            for (Field field : multiFieldOps){
                if (getParsedFieldNames().containsAll(getDependentFieldNames(field))){
                    add(field);
                }
            }

            multiFieldOps.removeAll(this);
            attempt++;

            if (!multiFieldOps.isEmpty() && attempt > 3){
                StringBuilder message = new StringBuilder();
                message.append("Unable to determine parse plan for the following fields: [");
                boolean needDelimiter = false;
                for (Field field : multiFieldOps){
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
     * @return
     * @param field list of field names that the Field needs to be available in the Record for its operations to succeed.
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
