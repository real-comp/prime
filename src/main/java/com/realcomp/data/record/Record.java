package com.realcomp.data.record;

import com.realcomp.data.Field;
import com.realcomp.data.MapField;
import com.realcomp.data.schema.SchemaField;
import com.realcomp.data.validation.record.RecordValidator;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * A collection of zero or more Fields.
 * 
 * Simply a MapField with support for the setting of 'key' field names.
 * A record may have more than one 'key' field.  These fields
 * are used to construct a record 'key' that may be useful.
 * By default, the 'key' is the value of the first field in the map.
 * Typically, a field is marked with a key validator in the schema.
 *
 * @author krenfro
 */
public class Record extends MapField implements Serializable{

    private static final long serialVersionUID = 1L;
    protected List<String> keys;

    public Record(){
    }

    /**
     *
     * @param keys the names of the fields that constitute the <i>key</i> for this Record.
     */
    public Record(Collection<String> keys){
        if (keys != null){
            this.keys = new ArrayList<String>();
            this.keys.addAll(keys);
        }
    }

    /**
     *
     * @return the value of the <i>key</i> fields for this Record, or null if unknown.
     */
    public List<String> getKey(){

        List<String> key = null;
        if (keys != null && !wrapped.isEmpty()){
            key = new ArrayList<String>();
            for (String fieldname: keys)
                key.add(wrapped.get(fieldname).getValue().toString());
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
