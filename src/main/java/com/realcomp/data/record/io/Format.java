package com.realcomp.data.record.io;


import com.realcomp.data.schema.xml.FormatConverter;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A map of format attributes, with some convenience methods for reading values with defaults.
 * @author krenfro
 */
@XStreamConverter(FormatConverter.class)
public class Format implements Map<String,String> {
    
    private Map<String,String> attributes;
    private Map<String,String> defaultValues;
    
    public Format(){
        attributes = new HashMap<String,String>();
        defaultValues = new HashMap<String,String>();
    }
    
    public Format(Format copy){
        attributes = new HashMap<String,String>();
        if (copy.attributes != null)
            attributes.putAll(copy.attributes);
        defaultValues = new HashMap<String,String>();
    }
    
    public Format(Map<String,String> map){
        attributes = new HashMap<String,String>();
        attributes.putAll(map);
        defaultValues = new HashMap<String,String>();
    }
    
    public Map<String,String> filterDefaultValues(){
        Map<String,String> filtered = new HashMap<String,String>();
        filtered.putAll(attributes);
        
        for (Entry<String,String> entry: attributes.entrySet()){
            String key = entry.getKey();
            if (key != null && key.equals(defaultValues.get(key))){
                filtered.remove(key);
            }
        }
        return filtered;
    }

    
    @Override
    public String get(Object name){
        String value = get(name);
        if (value == null){
            value = defaultValues.get(name);
        }
        return value;
    }
    
    public String putDefaultValue(String name, String value){
        return defaultValues.put(name, value);
    }
    
    /**
     * 
     * @param name
     * @param value
     * @return
     */
    @Override
    public String put(String name, String value){
        return attributes.put(name, value);
    }
    

    @Override
    public String remove(Object name){
        return attributes.remove(name);
    }
        
    @Override
    public int size() {
        return attributes.size();
    }

    @Override
    public boolean isEmpty() {
        return attributes.isEmpty();
    }

    @Override
    public boolean containsKey(Object o) {
        return attributes.containsKey(o);
    }

    @Override
    public boolean containsValue(Object o) {
        return attributes.containsValue(o);
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> map) {
        attributes.putAll(map);
    }

    @Override
    public void clear() {
        attributes.clear();
    }

    @Override
    public Set<String> keySet() {
        return attributes.keySet();
    }

    @Override
    public Collection<String> values() {
        return attributes.values();
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        return attributes.entrySet();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Format other = (Format) obj;
        if (this.attributes != other.attributes && (this.attributes == null || !this.attributes.equals(other.attributes)))
            return false;
        if (this.defaultValues != other.defaultValues && (this.defaultValues == null || !this.defaultValues.equals(other.defaultValues)))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.attributes != null ? this.attributes.hashCode() : 0);
        hash = 59 * hash + (this.defaultValues != null ? this.defaultValues.hashCode() : 0);
        return hash;
    }
    
}
