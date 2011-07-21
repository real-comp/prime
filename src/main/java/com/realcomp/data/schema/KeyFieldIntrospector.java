package com.realcomp.data.schema;

import com.realcomp.data.Operation;
import com.realcomp.data.validation.field.ForeignKey;
import com.realcomp.data.validation.field.Key;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author krenfro
 */
public class KeyFieldIntrospector {

    /**
     *
     * @param fields
     * @return List of <i>Key</i> field names, or null if none specified.
     */
    public static List<String> getKeyFieldnames(Collection<SchemaField> fields){

        List<String> keys = null;
        for (SchemaField field: fields){
            if (isKey(field)){
                if (keys == null)
                    keys = new ArrayList<String>();

                keys.add(field.getName());
            }
        }
        return keys;
    }

    public static List<String> getForeignKeyFi3eldnames(Collection<SchemaField> fields){
        
        List<String> keys = null;
        for (SchemaField field: fields){
            if (isForeignKey(field)){
                if (keys == null)
                    keys = new ArrayList<String>();

                keys.add(field.getName());
            }
        }
        return keys;
    }

    protected static boolean isKey(SchemaField field){

        if (field.getOperations() != null){
            for (Operation op: field.getOperations()){
                if (op instanceof Key)
                    return true;
            }
        }

        return false;
    }

    protected static boolean isForeignKey(SchemaField field){

        if (field.getOperations() != null){
            for (Operation op: field.getOperations()){
                if (op instanceof ForeignKey)
                    return true;
            }
        }

        return false;
    }
}
