package com.realcomp.data.record;

import com.realcomp.data.MultiFieldOperation;
import com.realcomp.data.Operation;
import com.realcomp.data.schema.SchemaField;
import java.util.ArrayList;
import java.util.List;

/**
 * The parse plan is the order that the fields should to be parsed from
 * a record.  Some converters are MultiFieldConverters and require
 * other fields to be available in the Record before they can succeed.
 * 
 * @author krenfro
 */
public class ParsePlan {

    protected List<SchemaField> fields;
    
    public ParsePlan(List<SchemaField> in) throws ParsePlanException{

        this.fields = new ArrayList<SchemaField>();
        createParsePlan(in);
    }
    
    protected final void createParsePlan(List<SchemaField> in) throws ParsePlanException{

        /* first walk the list and add all non-MultiFieldOperations to the list of
         * fields.  These SchemaField do not depend on anything other than themselves,
         * so they can be parsed first, in any order.
         */
        List<SchemaField> skipped = new ArrayList<SchemaField>();
        for (SchemaField field: in){
            if (hasMultiFieldOperation(field))
                skipped.add(field);
            else
                fields.add(field);
        }

        /* skipped now contains all the SchemaFields that contain at least one
         * MultiFieldOperation.  Try an inelegant brute-force of ordering by
         * making sure all the fields that the SchemaField depends on have
         * already been parsed.  If there are circular dependencies
         * in the SchemaFields, this algorithm will fall down and should be revised.
         */
        int attempt = 0;
        while (!skipped.isEmpty()){

            for (SchemaField field: skipped){
                if (fields.containsAll(getDependentFieldNames(field)))
                    fields.add(field);
            }

            skipped.removeAll(fields);
            attempt++;

            if (!skipped.isEmpty() && attempt > 3){
                StringBuilder message = new StringBuilder();
                message.append("Unable to determine parse plan for the following fields: [");
                boolean needDelimiter = false;
                for (SchemaField field: skipped){
                    if (needDelimiter)
                        message.append(",");
                    needDelimiter = true;
                    message.append(field.getName());
                }
                message.append("]");
                throw new ParsePlanException(message.toString());
            }
        }
    }

    protected final List<String> getDependentFieldNames(SchemaField field){

        List<String> fieldNames = new ArrayList<String>();
        for (Operation op: field.getOperations()){
            if (op instanceof MultiFieldOperation){
                fieldNames.addAll(((MultiFieldOperation) op).getFields());
            }
        }

        return fieldNames;
    }

    /**
     * Determines if one of the Operations in a SchemaField is a MultiFieldOperation.
     * 
     * @param field
     * @return true if the SchemaField contains a MultiFieldOperation.
     */
    protected final boolean hasMultiFieldOperation(SchemaField field){
        if (field.getOperations() != null){
            for (Operation op: field.getOperations()){
                if (op instanceof MultiFieldOperation)
                    return true;
            }
        }
        return false;
    }

    public List<SchemaField> getFields() {
        return fields;
    }

    public void setFields(List<SchemaField> fields) {
        this.fields = fields;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final ParsePlan other = (ParsePlan) obj;
        if (this.fields != other.fields && (this.fields == null || !this.fields.equals(other.fields)))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 73 * hash + (this.fields != null ? this.fields.hashCode() : 0);
        return hash;
    }
}
