package com.realcomp.prime.record;

import com.realcomp.prime.Operation;
import com.realcomp.prime.schema.*;
import com.realcomp.prime.validation.field.ForeignKey;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Joins Records using a RelationalSchema and one or more Schemas
 *
 *
 */
public class RelationalRecordJoiner{

    private static final Logger logger = Logger.getLogger(RelationalRecordJoiner.class.getName());

    protected RelationalSchema relationalSchema;
    protected LinkedHashMap<Record, Schema> remainingRecords;
    protected List<Record> skippedRecords;

    public RelationalRecordJoiner(){
        remainingRecords = new LinkedHashMap<>();
        skippedRecords = new ArrayList<>();
    }

    public void setRelationalSchema(RelationalSchema relationalSchema){
        this.relationalSchema = relationalSchema;
    }

    public void addRecord(Record record, Schema schema){
        remainingRecords.put(record, schema);
    }

    /**
     * @return 
     * @throws IOException
     */
    public List<Record> join() throws IOException{

        skippedRecords.clear();
        List<Record> result = new ArrayList<>();
        int recordCount = remainingRecords.size();

        if (recordCount > 0){

            if (relationalSchema == null && recordCount == 1){
                result.add(remainingRecords.keySet().iterator().next());
                remainingRecords.clear();
            }
            else if (relationalSchema == null){
                logger.log(Level.WARNING,
                           "No relational schema defined, but {0} occurences were encountered.",
                           new Object[]{recordCount});

                result.addAll(remainingRecords.keySet());
                remainingRecords.clear();
            }
            else{
                //do a full relational join
                Record record = new Record();

                for (Table child : relationalSchema.getTables()){
                    join(child, remainingRecords, record);
                }

                // The join process will remove entries from the remainingRecords collection that it was able to join.
                if (!remainingRecords.isEmpty()){

                    for (Map.Entry<Record, Schema> entry : remainingRecords.entrySet()){
                        Schema schema = entry.getValue();
                        if (schema != null){
                            logger.log(Level.WARNING,
                                       "Record for schema [{0}] not joined: {1}",
                                       new Object[]{schema.getName(), schema.toString(entry.getKey())});
                        }
                        else{
                            logger.log(Level.WARNING, "Record not joined, and no schema defined: {0}", entry.getKey().toString());
                        }
                    }
                }

                result.add(record);
            }
        }

        skippedRecords.addAll(remainingRecords.keySet());
        remainingRecords.clear();
        return result;
    }

    public List<Record> getSkippedRecords(){
        return skippedRecords;
    }

    /**
     * Adds an entry to current where the key is the table name, and the value is a List of children. It then recurses
     * for each child, until there are no more maps available.
     *
     * @param table
     * @param records
     * @param current
     * @throws java.io.IOException
     */
    protected void join(Table table, Map<Record, Schema> records, Record current) throws IOException{

        List<Record> children = getChildren(table, records, current);
        if (!children.isEmpty()){

            for (Record child : children){
                records.remove(child);
            }
            current.put(table.getName(), children);

            for (Record child : children){
                if (table.hasChildren()){
                    for (Table childTable : table.getChildren()){
                        join(childTable, records, child); //i recurse
                    }
                }
            }
        }
    }

    /**
     * Uses the FileSchemas for both maps to determine if the child map is a valid child of the parent map. If there are
     * no foreign keys defined for the child, then the child is considered valid.
     *
     * @param parent
     * @param child
     * @return true if foreign keys defined in child match the keys in parent; else false.
     * @throws java.io.IOException
     */
    protected boolean isChild(Record parent, Record child) throws IOException{

        Schema childSchema = remainingRecords.get(child);

        for (FieldList fields : childSchema.getFieldLists()){
            if (isChild(fields.getForeignKeys(), parent, child)){
                return true;
            }
        }

        return false;
    }

    protected boolean isChild(List<Field> foreignKeys, Record parent, Record child){

        boolean isChild = true;

        for (Field field : foreignKeys){
            Object parentValue = parent.get(field.getName());
            Object childValue = child.get(getForeignKeyName(field));

            if (!childValue.equals(parentValue)){
                isChild = false;
                break;
            }
        }

        return isChild;
    }

    /**
     * The foreign key name is the name of the field in the parent record that shares a value with the specified field
     * in the child record.
     *
     * By default, the parent field name and child field name will be the same, but this can be overridden in the
     * ForeignKey validator in the Schema.
     *
     * @param field
     * @return
     */
    protected String getForeignKeyName(Field field){

        //by default, the fk name is the same as the field.
        String foreignKeyName = field.getName();

        for (Operation op : field.getOperations()){
            if (op instanceof ForeignKey){
                if (((ForeignKey) op).getName() != null){
                    foreignKeyName = ((ForeignKey) op).getName();
                }
            }
        }

        return foreignKeyName;
    }

    /**
     * @param remainingRecords
     * @param table
     * @return filtered list of remainingRecords that belong to the specified table.
     */
    protected List<Record> getChildren(Table table, Map<Record, Schema> records, Record current) throws IOException{

        List<Record> children = new ArrayList<>();
        List<Record> candidates = filterByTable(table, records);  //probably dumb to do this.

        for (Record candidate : candidates){
            if (isChild(current, candidate)){
                children.add(candidate);
            }
        }

        return children;
    }

    /**
     * @param remainingRecords
     * @param table
     * @return filtered list of remainingRecords that belong to the specified table.
     */
    protected List<Record> filterByTable(Table table, Map<Record, Schema> records){

        List<Record> retVal = new ArrayList<>();
        for (Entry<Record, Schema> entry : records.entrySet()){
            Schema schema = entry.getValue();
            if (schema.getName() == null){
                StringBuilder fields = new StringBuilder();
                for (Field f: schema.getDefaultFieldList()){
                    if (fields.length() > 0){
                        fields.append(", ");
                    }
                    fields.append(f.getName());
                }
                throw new IllegalStateException(
                        "Encountered a Schema with no name!  "
                        + "All Schemas must be named for relational joining. "
                        + "The problematic Schema has the following Fields: [" + fields.toString() + "]");
            }
            else if (schema.getName().equals(table.getName())){
                retVal.add(entry.getKey());
            }
        }
        return retVal;
    }
}
