package com.realcomp.data.record;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * 
 * 
 * 
 * <p>
 * A record may have more than one 'key' field.  These fields
 * are used to construct a record 'key' that may be useful.
 * By default, the 'key' is the value of the first field in the map.
 * Typically, a field is marked with a key validator in the schema.
 * </p>
 *
 * @author krenfro
 */
@XmlRootElement
public class Record implements Serializable {

    public static final long serialVersionUID = 2L;
    
    protected Map<String, Object> data;

    public Record() {
        data = new HashMap<String, Object>();
    }

    public Record(Record copy) {
        data = new HashMap<String, Object>();
        data.putAll(copy.data);
    }

    public Record(Map<String, Object> data) {
        if (data == null)
            throw new IllegalArgumentException("data is null");
        this.data = new HashMap<String, Object>();
        this.data.putAll(data);
    }

    public boolean containsKey(String key) {
        return data.containsKey(key);
    }

    public Set<String> keySet() {
        return data.keySet();        
    }

    public Collection<Object> values() {
        return data.values();
    }

    @XmlTransient
    public boolean isEmpty() {
        return data.isEmpty();
    }

    public Set<Entry<String, Object>> entrySet() {
        return data.entrySet();
    }
    
    public Object put(String key, Object value) {
        return value == null ? data.remove(key) : data.put(key, value);
    }

    public Object put(String key, String value) {
        return value == null ? data.remove(key) : data.put(key, value);
    }

    public Object put(String key, Integer value) {
        return value == null ? data.remove(key) : data.put(key, value);
    }

    public Object put(String key, Float value) {
        return value == null ? data.remove(key) : data.put(key, value);
    }

    public Object put(String key, Long value) {
        return value == null ? data.remove(key) : data.put(key, value);
    }

    public Object put(String key, Double value) {
        return value == null ? data.remove(key) : data.put(key, value);
    }

    public Object put(String key, Boolean value) {
        return value == null ? data.remove(key) : data.put(key, value);
    }

    public Object put(String key, List value) {
        return value == null ? data.remove(key) : data.put(key, value);
    }

    public Object put(String key, Map value) {
        return value == null ? data.remove(key) : data.put(key, value);
    }

    public Object get(String key) {
        return data.get(key);
    }
    
    public Object remove(String key){
        return data.remove(key);
    }
    
    /**
     * 
     * @param key
     * @return 
     */
    public List<Object> resolve(String key){
        return RecordValueResolver.resolve(data, key);
    }
    
    public Object resolveFirst(String key){
        List<Object> list = resolve(key);
        if (list != null && !list.isEmpty())
            return list.get(0);
        else
            return null;        
    }
    
    public Map<String,Object> asMap(){
        return data;
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
