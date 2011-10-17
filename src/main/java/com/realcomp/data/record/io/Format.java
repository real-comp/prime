package com.realcomp.data.record.io;


import com.realcomp.data.schema.xml.FormatConverter;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author krenfro
 */
@XStreamConverter(FormatConverter.class)
public class Format {
    
    private String type;    
    
    private Map<String,String> attributes;
    
    public Format(){
        attributes = new HashMap<String,String>();
    }
    
    public Format(String type){
        if (type == null)
            throw new IllegalArgumentException("type is null");
        attributes = new HashMap<String,String>();
        this.type = type;
    }
    
    public Format(Format copy){
        this();
        type = copy.type;        
        if (copy.attributes != null)
            attributes.putAll(copy.attributes);
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes.clear();
        if (attributes != null)            
            this.attributes.putAll(attributes);
    }
    
    public String getAttribute(String name){
        return attributes.get(name);
    }
    
    public String setAttribute(String name, String value){
        return attributes.put(name, value);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    @Override
    public String toString(){
        
        StringBuilder s = new StringBuilder();
        s.append("Format (type=");
        s.append(type);
        s.append(") attributes:");
        s.append(attributes.toString());
        return s.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Format other = (Format) obj;
        if ((this.type == null) ? (other.type != null) : !this.type.equals(other.type))
            return false;
        if (this.attributes != other.attributes && (this.attributes == null || !this.attributes.equals(other.attributes)))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 67 * hash + (this.attributes != null ? this.attributes.hashCode() : 0);
        return hash;
    }
    
}
