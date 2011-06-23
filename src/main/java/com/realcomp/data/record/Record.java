package com.realcomp.data.record;

import com.realcomp.data.DataType;
import java.io.Serializable;
import java.util.ArrayList;
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


    protected List<String> keys;
    protected Map<String,Object> data;

    public Record(){
        data = new HashMap<String,Object>();
    }

    public Record(Record copy){
        data = new HashMap<String,Object>();
        data.putAll(copy.data);
        if (copy.keys != null){
            keys = new ArrayList<String>();
            keys.addAll(copy.keys);
        }
    }

    /**
     *
     * @param keys the names of the fields that constitute the <i>key</i> for this Record.
     */
    public Record(Collection<String> keys){
        this();
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
        if (keys != null && !data.isEmpty()){
            key = new ArrayList<String>();
            for (String fieldname: keys){
                Object value = data.get(fieldname);
                key.add(value == null ? "NULL" : value.toString());
            }
        }

        return key;
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
            for (Object f: data.values()){
                if (key.size() >= 2)
                    break;

                key.add(f.toString());
            }
        }

        for (String k: key){
            if (needDelimiter)
                s.append(":");
            s.append(k);
            needDelimiter = true;
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
        if (this.keys != other.keys && (this.keys == null || !this.keys.equals(other.keys)))
            return false;
        if (this.data != other.data && (this.data == null || !this.data.equals(other.data)))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + (this.keys != null ? this.keys.hashCode() : 0);
        hash = 23 * hash + (this.data != null ? this.data.hashCode() : 0);
        return hash;
    }


}
