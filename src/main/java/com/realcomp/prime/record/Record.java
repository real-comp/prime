package com.realcomp.prime.record;

import com.realcomp.prime.DataType;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.*;

/**
 * A Map with support for composite keys.
 *
 *
 */
@XmlRootElement
public class Record implements Map<String, Object>, Serializable{

    public static final long serialVersionUID = 2L;
    private Map<String, Object> data;

    public Record(){
        data = new HashMap<>();
    }

    public Record(Record copy){
        data = new HashMap<>();
        data.putAll(copy.data);
    }

    /**
     * Wraps a map as a Record.
     *
     * No checks are done to make sure that this map conforms to the Prime data model.
     *
     * @param map
     */
    public Record(Map<String, Object> map){
        if (map == null){
            throw new IllegalArgumentException("prime is null");
        }
        data = new HashMap<>();
        for (Entry<String, Object> entry : map.entrySet()){
            put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Return this map as a map that does not resolve composite keys. Changes made to the map will be reflected in this
     * Record.
     *
     */
    public Map<String, Object> asSimpleMap(){
        return data;
    }

    public boolean containsKey(String key){
        return data.containsKey(key);
    }

    @XmlTransient
    @Override
    public boolean isEmpty(){
        return data.isEmpty();
    }

    /**
     * @return all leaf node keys contained in this Record. The keys may be <i>composite</i> and <i>indexed</i>.
     */
    @Override
    public Set<String> keySet(){
        Set<String> keys = new HashSet<>();
        Iterator<Map.Entry<String, Object>> itr = entrySet().iterator();
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
    public Collection<Object> values(){
        List<Object> values = new ArrayList<>();
        Iterator<Map.Entry<String, Object>> itr = entrySet().iterator();
        while (itr.hasNext()){
            values.add(itr.next().getValue());
        }

        return values;
    }

    /**
     * @return all leaf node values in this Record.
     */
    @Override
    public Set<Entry<String, Object>> entrySet(){
        return RecordEntries.getEntries(data);
    }

    @Override
    public Object put(String key, Object value){
        if (key == null){
            throw new RecordKeyException("Record keys cannot be null.");
        }

        return RecordValueAssembler.assemble(data, key, value);
    }

    @Override
    public void clear(){
        data.clear();
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
                : RecordValueResolver.resolve(data, key.toString());
    }

    public Object get(String key, Object defaultValue){
        Object value = get(key);
        return value == null ? defaultValue : value;
    }

    public String getString(String key){
        return (String) get(key);
    }

    public String getString(String key, String defaultValue){
        String value = getString(key);
        return value == null ? defaultValue : value;
    }

    public Boolean getBoolean(String key){
        Object value = get(key);
        Boolean result = null;
        if (value != null){
            if (value instanceof Boolean){
                result = (Boolean) value;
            }
            else{
                result = Boolean.parseBoolean(value.toString());
            }
        }
        return result;
    }

    public Boolean getBoolean(String key, Boolean defaultValue){
        Boolean value = getBoolean(key);
        return value == null ? defaultValue : value;
    }

    public Integer getInteger(String key){
        return (Integer) get(key);
    }

    public Integer getInteger(String key, Integer defaultValue){
        Integer value = getInteger(key);
        return value == null ? defaultValue : value;
    }

    public Long getLong(String key){
        return (Long) get(key);
    }

    public Long getLong(String key, Long defaultValue){
        Long value = getLong(key);
        return value == null ? defaultValue : null;
    }

    public Float getFloat(String key){
        return (Float) get(key);
    }

    public Float getFloat(String key, Float defaultValue){
        Float value = getFloat(key);
        return value == null ? defaultValue : value;
    }

    public Double getDouble(String key){
        return (Double) get(key);
    }

    public Double getDouble(String key, Double defaultValue){
        Double value = getDouble(key);
        return value == null ? defaultValue : value;
    }

    public Map<String, Object> getMap(String key){
        return (Map<String, Object>) get(key);
    }

    public Map<String, Object> getMap(String key, Map<String, Object> defaultValue){
        Map<String, Object> value = getMap(key);
        return value == null ? defaultValue : value;
    }

    public List getList(String key){
        return (List) get(key);
    }

    public List getList(String key, List defaultValue){
        List value = getList(key);
        return value == null ? defaultValue : value;
    }

    /**
     * Resolve all values for the specified key
     *
     * @param key
     * @return list of values, never null.
     */
    public List<Object> getAll(String key){
        return key == null
                ? new ArrayList<>()
                : RecordMultiValueResolver.resolve(data, new RecordKey(key.toString()));
    }

    @Override
    public int size(){
        return keySet().size();
    }

    @Override
    public boolean containsKey(Object key){
        return key != null && keySet().contains(key.toString());
    }

    @Override
    public boolean containsValue(Object value){
        return values().contains(value);
    }

    @Override
    public Object remove(Object key){

        Object previous = null;
        if (key != null){
            RecordKey child = key instanceof RecordKey ? (RecordKey) key : new RecordKey(key.toString());
            if (child.hasParent()){
                RecordKey parent = child.getParent();
                Object existing = RecordValueResolver.resolve(data, parent.toString());
                if (DataType.getDataType(existing) == DataType.MAP){
                    if (child.isIndexed()){
                        List<Object> list = (List<Object>) ((Map<String, Object>) existing).get(child.getName());
                        previous = list.remove(child.getIndex());
                    }
                    else{
                        previous = ((Map<String, Object>) existing).remove(child.getName());
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
    public void putAll(Map<? extends String, ? extends Object> m){
        for (Entry entry : m.entrySet()){
            put((String) entry.getKey(), entry.getValue());
        }
    }

    @Override
    public boolean equals(Object obj){
        if (obj == null){
            return false;
        }
        if (getClass() != obj.getClass()){
            return false;
        }
        final Record other = (Record) obj;
        return this.data == other.data || (this.data != null && this.data.equals(other.data));
    }

    @Override
    public int hashCode(){
        int hash = 3;
        hash = 23 * hash + (this.data != null ? this.data.hashCode() : 0);
        return hash;
    }
}
