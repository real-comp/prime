package com.realcomp.data.record;

import com.realcomp.data.DataType;
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
 * A Map with support for composite keys.
 * 
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
        return key == null 
                ? null 
                : RecordValueResolver.resolve(data, new RecordKey(key.toString()));        
    }
    
    /**
     * Resolve all values for the specified key
     * @param key
     * @return list of values, never null.
     */
    public List<Object> getAll(String key){
        return key == null
                ? new ArrayList<Object>() 
                : RecordMultiValueResolver.resolve(data, new RecordKey(key.toString()));
    }
  
    @Override
    public int size() {
        return keySet().size();
    }
    
    @Override
    public boolean containsKey(Object key) {
        return key == null ? false : keySet().contains(key.toString());
    }

    @Override
    public boolean containsValue(Object value) {
        return values().contains(value);
    }

    @Override
    public Object remove(Object key) {
        
        Object previous = null;
        if (key != null){
            RecordKey child = new RecordKey(key.toString());
            if (child.hasParent()){
                RecordKey parent = child.getParent();
                Object existing = RecordValueResolver.resolve(data, parent); 
                if (DataType.getDataType(existing) == DataType.MAP){                    
                    if (child.isIndexed()){
                        List<Object> list = (List<Object>) ((Map<String,Object>) existing).get(child.getName());
                        previous = list.remove(child.getIndex());
                    }
                    else{
                        previous = ((Map<String,Object>) existing).remove(child.getName());
                    }
                }
            }
            else{
                previous = data.remove(child.getName());
            }
        }
        
        return previous;
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> m) {        
        for (Entry entry: m.entrySet()){
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
