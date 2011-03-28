package com.realcomp.data.schema;

import com.realcomp.data.Operation;
import com.realcomp.data.validation.field.Key;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author krenfro
 */
public class KeyFieldIntrospector {

    /**
     *
     * @param fields
     * @return List of <i>Key</i> SchemaFields, or null if none specified.
     */
    public static List<SchemaField> getKeyFields(List<SchemaField> fields){

        List<SchemaField> keys = null;
        for (SchemaField field: fields){
            if (isKey(field)){
                if (keys == null)
                    keys = new ArrayList<SchemaField>();

                keys.add(field);
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
}
