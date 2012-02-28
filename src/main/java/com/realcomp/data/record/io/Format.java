package com.realcomp.data.record.io;


import com.realcomp.data.schema.xml.AttributesConverter;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Collection of formatting attributes with support for default values.
 * 
 * 
 * @author krenfro
 */
@XStreamConverter(AttributesConverter.class)
public class Format implements Map<String,String> {
    
    protected Map<String,String> attributes;
    protected Map<String,String> defaults;
    
    public Format(){
        attributes = new HashMap<String,String>();
        defaults = new HashMap<String,String>();
    }
    
    public Format(Format copy){
        attributes = new HashMap<String,String>();
        if (copy.attributes != null)
            attributes.putAll(copy.attributes);
        defaults = new HashMap<String,String>();
    }
    
    public Format(Map<String,String> map){
        attributes = new HashMap<String,String>();
        attributes.putAll(map);
        defaults = new HashMap<String,String>();
    }
    
    public Map<String,String> getDefaults(){
        return defaults;
    }
    

    /**
     * 
     * @param name
     * @return the value for the named attribute, or the default value (if available); else null
     */
    @Override
    public String get(Object name){
        String value = attributes.get(name);
        if (value == null){
            value = defaults.get(name);
        }
        return value;
    }
    
    /**
     * Store a default value for a named default attribute.
     * @param name
     * @param value
     * @return previous value for the default.
     */
    public String putDefault(String name, String value){
        return defaults.put(name, value);
    }
    
    /**
     * If the value is already set as a default, then this is a no-op.
     * @param name
     * @param value
     * @return
     */
    @Override
    public String put(String name, String value){
        String retVal = null;
        String d = defaults.get(name);
        if (d == null || !d.equals(value)){
            retVal = attributes.put(name, value);
        }
        
        return retVal;
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
        if (map != null){
            for (Entry<? extends String,? extends String> entry: map.entrySet()){
                put(entry.getKey(), entry.getValue());
            }
        }
    }
    
    public void putDefaults(Map<? extends String, ? extends String> defaults){
        this.defaults.putAll(defaults);
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
        if (this.defaults != other.defaults && (this.defaults == null || !this.defaults.equals(other.defaults)))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.attributes != null ? this.attributes.hashCode() : 0);
        hash = 59 * hash + (this.defaults != null ? this.defaults.hashCode() : 0);
        return hash;
    }
    
}
