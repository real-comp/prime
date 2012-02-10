package com.realcomp.data.record.io;

import com.realcomp.data.schema.Schema;
import com.realcomp.data.validation.Severity;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;

/**
 * Unmodifiable context for I/O operations.
 * 
 * @author krenfro
 */
public class IOContext implements Serializable{

    protected Schema schema;
    protected Map<String,String> attributes;
    protected transient InputStream in;
    protected transient OutputStream out;
    protected Severity validationExeptionThreshold = Severity.HIGH;
    
    private IOContext(Builder builder){
        schema = builder.schema;
        attributes = builder.attributes;
        in = builder.in;
        out = builder.out;
        validationExeptionThreshold = builder.validationExceptionThreshold;
    }
    
    public static class Builder{
        private Schema schema;
        private InputStream in;
        private OutputStream out;
        private Map<String,String> attributes;
        private Severity validationExceptionThreshold = Severity.HIGH;
        
        public Builder(){
            attributes = new HashMap<String,String>();
        }
        
        public Builder(IOContext context){
            attributes = new HashMap<String,String>();
            attributes.putAll(context.attributes);
            if (context.schema != null)
                schema = new Schema(context.schema);
            in = context.in;
            out = context.out;
            validationExceptionThreshold = context.validationExeptionThreshold;
        }
        
        public Builder schema(Schema schema){
            if (schema != null)
                this.schema = new Schema(schema);
            return this;
        }
        
        public Builder in(InputStream in){
            this.in = in;
            return this;
        }
        
        public Builder out(OutputStream out){
            this.out = out;
            return this;
        }
        
        public Builder attributes(Map<String,String> attributes){            
            if (attributes != null)
                this.attributes.putAll(attributes);
            return this;
        }
        
        public Builder attribute(String name, String value){
            attributes.put(name, value);
            return this;
        }
        
        public Builder validationExceptionThreshold(Severity severity){
            if (severity == null)
                throw new IllegalArgumentException("severity is null");
            validationExceptionThreshold = severity;
            return this;
        }
        
        public IOContext build(){
            return new IOContext(this);
        }
    }

    public void close(){
        IOUtils.closeQuietly(in);
        IOUtils.closeQuietly(out);
    }

    /**
     * Attributes of this IOContext override (hide) attributes specified in the Schema's format attributes.
     * 
     * @return copy of all attributes, including any format attributes of the schema
     */
    public Map<String, String> getAttributes() {        
        Map<String,String> copy = new HashMap<String,String>();
        if (schema != null)
            copy.putAll(schema.getFormat());
        copy.putAll(attributes);
        return copy;
    }
    
    public String getAttribute(String name){
        String value = null;
        if (schema != null && schema.getFormat() != null)
            value = schema.getFormat().get(name);
        if (value == null)
            value = attributes.get(name);
        return value;
    }
    
    public InputStream getIn() {
        return in;
    }

    public OutputStream getOut() {
        return out;
    }

    public Schema getSchema() {
        return schema;
    }

    public Severity getValidationExeptionThreshold() {
        return validationExeptionThreshold;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final IOContext other = (IOContext) obj;
        if (this.validationExeptionThreshold != other.validationExeptionThreshold)
            return false;
        if (this.schema != other.schema && (this.schema == null || !this.schema.equals(other.schema)))
            return false;
        if (this.attributes != other.attributes && (this.attributes == null || !this.attributes.equals(other.attributes)))
            return false;
        if (this.in != other.in && (this.in == null || !this.in.equals(other.in)))
            return false;
        if (this.out != other.out && (this.out == null || !this.out.equals(other.out)))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + (this.validationExeptionThreshold != null ? this.validationExeptionThreshold.hashCode() : 0);
        hash = 59 * hash + (this.schema != null ? this.schema.hashCode() : 0);
        hash = 59 * hash + (this.attributes != null ? this.attributes.hashCode() : 0);
        hash = 59 * hash + (this.in != null ? this.in.hashCode() : 0);
        hash = 59 * hash + (this.out != null ? this.out.hashCode() : 0);
        return hash;
    }    
}
