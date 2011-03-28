package com.realcomp.data.record;

import com.realcomp.data.Field;
import com.realcomp.data.MapField;
import com.realcomp.data.schema.SchemaField;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Simply a MapField with support for the setting of 'id' field names.
 * A record may have more than one 'id' field.  These fields
 * are used to construct a record 'id' that may be useful.
 * By default, the 'id' is the value of the first field in the map.
 * Typically, a field is marked as id="true" in the schema.
 *
 * @author krenfro
 */
public class Record extends MapField implements Serializable{

    private static final long serialVersionUID = 1L;
    protected List<SchemaField> keyFields;

    public Record(){
    }

    /**
     *
     * @param keyFields List of <i>Key</i> SchemaFields or nulls
     */
    public Record(List<SchemaField> keyFields){
        this.keyFields = keyFields;
    }

    /**
     *
     * @return the value of the <i>key</i> fields for this Record, or null if unknown.
     */
    public List<String> getKey(){

        List<String> key = null;
        if (keyFields != null && !wrapped.isEmpty()){
            key = new ArrayList<String>();
            for (SchemaField field: keyFields)
                key.add(wrapped.get(field.getName()).getValue().toString());
        }

        return key;
    }

    
    /**
     * @return the key fields of this Record delimited by a colon, or
     * the value of the first two fields if no key fields defined.
     */
    @Override
    public String toString(){
        List<String> key = getKey();
        StringBuilder s = new StringBuilder();
        boolean needDelimiter = false;

        if (key == null){
            //no key defined, use value of first 2 fields
            key = new ArrayList<String>();
            for (Field f: wrapped.values()){
                if (key.size() >= 2)
                    break;

                key.add(f.getValue().toString());
            }
        }
        
        for (String k: key){
            if (needDelimiter)
                s.append(":");
            s.append(k);
        }
        
        return s.toString();
    }
}
