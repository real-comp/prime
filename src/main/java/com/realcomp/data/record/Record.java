package com.realcomp.data.record;

import com.realcomp.data.DataType;
import com.realcomp.data.schema.FileSchema;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


/**
 * <p>
 * A record may have more than one 'key' field.  These fields
 * are used to construct a record 'key' that may be useful.
 * By default, the 'key' is the value of the first field in the map.
 * Typically, a field is marked with a key validator in the schema.
 * </p>
 *
 * @author krenfro
 */
public class Record implements Serializable{


    protected Map<String,Object> data;

    public Record(){
        data = new HashMap<String,Object>();
    }

    public Record(Record copy){
        data = new HashMap<String,Object>();
        data.putAll(copy.data);
    }

 
    public boolean containsKey(String key){
        return data.containsKey(key);
    }

    public Set<String> keySet(){
        return data.keySet();
    }

    public Collection<Object> values(){
        return data.values();
    }

    public boolean isEmpty(){
        return data.isEmpty();
    }
    
    public Set<Entry<String,Object>> entrySet(){
        return data.entrySet();
    }


    public Object put(String key, Object value){

        if (value == null){
            return put(key, (String) null);
        }
        else{
            DataType type = DataType.get(value);
            switch(type){
                case STRING:
                    return put(key, (String) value);
                case INTEGER:
                    return put(key, (Integer) value);
                case FLOAT:
                    return put(key, (Float) value);
                case LONG:
                    return put(key, (Long) value);
                case DOUBLE:
                    return put(key, (Double) value);
                case BOOLEAN:
                    return put(key, (Boolean) value);
                case LIST:
                    return put(key, (List) value);
                case MAP:
                    return put(key, (Map) value);
            }
        }

        return get(key);
    }

    public Object put(String key, String value){
        return value == null ? data.remove(key) : data.put(key, value);
    }

    public Object put(String key, Integer value){
        return value == null ? data.remove(key) : data.put(key, value);
    }

    public Object put(String key, Float value){
        return value == null ? data.remove(key) : data.put(key, value);
    }

    public Object put(String key, Long value){
        return value == null ? data.remove(key) : data.put(key, value);
    }

    public Object put(String key, Double value){
        return value == null ? data.remove(key) : data.put(key, value);
    }

    public Object put(String key, Boolean value){
        return value == null ? data.remove(key) : data.put(key, value);
    }

    public Object put(String key, List value){
        return value == null ? data.remove(key) : data.put(key, value);
    }

    public Object put(String key, Map value){
        return value == null ? data.remove(key) : data.put(key, value);
    }

    public Object get(String key){
        return data.get(key);
    }

    /**
     * @see FileSchema#toString(Record)
     * @return the first two fields of this Record delimited by a pipe "|"
     */
    @Override
    public String toString(){
        
        StringBuilder s = new StringBuilder();
        for (Object f: data.values()){
            if (s.length() > 0)
                s.append("|");
            s.append(f.toString());
        }
        
        return s.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Record other = (Record) obj;
        if (this.data != other.data && (this.data == null || !this.data.equals(other.data)))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + (this.data != null ? this.data.hashCode() : 0);
        return hash;
    }


}
