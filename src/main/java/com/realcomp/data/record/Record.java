package com.realcomp.data.record;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
public class Record implements Map<String,Object>, Serializable {

    public static final long serialVersionUID = 2L;
    
    Map<String, Object> data;

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


    @XmlTransient
    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }
    
    
    /**
     * @return all leaf node keys contained in this Record. The keys may be <i>composite</i> and <i>indexed</i>.
     */
    @Override
    public Set<String> keySet(){        
        Set<String> keys = new HashSet<String>();
        Iterator<Map.Entry<String,Object>> itr  = entrySet().iterator();
        while (itr.hasNext()){
            keys.add(itr.next().getKey());
        }
        return keys;
    }
    
    /**
     * 
     * @return all leaf node values contained in this Record.
     */
    @Override
    public Collection<Object> values() {        
        List<Object> values = new ArrayList<Object>();
        Iterator<Map.Entry<String,Object>> itr = entrySet().iterator();
        while (itr.hasNext()){
            values.add(itr.next().getValue());
        }
        
        return values;
    }
    
    /**
     * @return all leaf node values in this Record.
     */
    @Override
    public Set<Entry<String, Object>> entrySet() {                
        return RecordEntries.getEntries(data);
    }
    
    @Override
    public Object put(String key, Object value) {
        return value == null ? data.remove(key) : data.put(key, value);
    }
    
    @Override
    public void clear(){
        data.clear();
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
    
    /**
     * @param key evaluated as a String
     * @return The value referenced by the <i>key</i>. Type one of supported DataTypes. Null if value does not exist.
     * @throws RecordKeyException if the key does not refer to a single value
     */
    @Override
    public Object get(Object key){
        List<Object> list = RecordValueResolver.resolve(data, new RecordKey(key.toString()));
        if (list != null && list.size() > 1){
            throw new RecordKeyException(
                    String.format("Ambiguous key [%s] references [%s] values.  "
                                + "Use getFirst(), getAll() or a more specific key", key, list.size()));
        }
        
        return list == null || list.isEmpty() ? null : list.get(0);
        
    }
    
    /**
     * 
     * @param key
     * @return the <i>first</i> value referenced by the <i>key</i>, or null if it does not exist
     */
    public Object getFirst(String key){
        List<Object> list = RecordValueResolver.resolve(data, new RecordKey(key));
        return list == null || list.isEmpty() ? null : list.get(0);
    }
    
    /**
     * Resolve all values for the specified key
     * @param key
     * @return 
     */
    public List<Object> getAll(String key){
        return RecordValueResolver.resolve(data, new RecordKey(key));
    }
  
    @Override
    public int size() {
        return keySet().size();
    }
    
    @Override
    public boolean containsKey(Object key) {
        return keySet().contains(key.toString());
    }

    @Override
    public boolean containsValue(Object value) {
        return values().contains(value);
    }

    @Override
    public Object remove(Object key) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> m) {        
        for (Map.Entry entry: m.entrySet()){
            put((String) entry.getKey(), entry.getValue());
        }
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
