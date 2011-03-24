package com.realcomp.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author krenfro
 */
public class MapField extends Field<Map<String,Field>> implements Map<String,Field>, Serializable{

    private static final long serialVersionUID = 1L;

    protected static final DataType type = DataType.MAP;
    protected Map<String,Field> wrapped;
    protected String name;

    protected MapField(){
        wrapped = new HashMap<String,Field>();
    }

    public MapField(Map<String,Field> value){
        if (value == null)
            throw new IllegalArgumentException("value is null");
        this.wrapped = value;
    }

    public MapField(String name, Map<String,Field> value){
        this(value);
        this.name = name;
    }

    @Override
    public String getName(){
        return name;
    }

    @Override
    public void setName(String name){
        this.name = name;
    }
 
    @Override
    public int size() {
        return wrapped.size();
    }

    @Override
    public boolean isEmpty() {
        return wrapped.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return wrapped.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return wrapped.containsValue(value);
    }

    @Override
    public Field get(Object key) {
        return wrapped.get(key);
    }

    @Override
    public Field get(String key) {
        return wrapped.get(key);
    }

    @Override
    public Field put(String key, Field value) {
        return wrapped.put(key, value);
    }

    @Override
    public Field remove(Object key) {
        return wrapped.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends Field> m) {
        wrapped.putAll(m);
    }

    @Override
    public void clear() {
        wrapped.clear();
    }

    @Override
    public Set<String> keySet() {
        return wrapped.keySet();
    }

    @Override
    public Collection<Field> values() {
        return wrapped.values();
    }

    @Override
    public Set<Entry<String, Field>> entrySet() {
        return wrapped.entrySet();
    }

    @Override
    public DataType getType() {
        return type;
    }

    @Override
    public Map<String, Field> getValue() {
        return wrapped;
    }

    @Override
    public void setValue(Map<String, Field> value) {
        if (value == null)
            throw new IllegalArgumentException("value is null");
        wrapped = value;
    }

    @Override
    public String toString(){
        return wrapped.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final MapField other = (MapField) obj;
        if (this.wrapped != other.wrapped && (this.wrapped == null || !this.wrapped.equals(other.wrapped)))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.wrapped != null ? this.wrapped.hashCode() : 0);
        return hash;
    }
}
