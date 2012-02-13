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
 * 
 * @author krenfro
 */
public class IOContext implements Serializable{
    private static final long serialVersionUID = 1L;

    protected Schema schema;
    protected Map<String,String> attributes;
    protected transient InputStream in;
    protected transient OutputStream out;
    protected Severity validationExeptionThreshold = Severity.HIGH;
    
    protected IOContext(IOContextBuilder builder){
        schema = builder.schema;
        attributes = builder.attributes;
        in = builder.in;
        out = builder.out;
        validationExeptionThreshold = builder.validationExceptionThreshold;
    }

    /**
     * Quietly close the input and output streams
     */
    public void close(){
        IOUtils.closeQuietly(in);
        IOUtils.closeQuietly(out);
    }

    /**
     * All the attributes of this IOContext, including any available attributes in the schema.
     * 
     * 
     * @return copy of all attributes, including any attributes of the schema
     */
    public Map<String, String> getAttributes() {        
        Map<String,String> copy = new HashMap<String,String>();
        if (schema != null)
            copy.putAll(schema.getFormat());
        copy.putAll(attributes);
        return copy;
    }
    
    /**
     * Get a named attribute, first from the attributes of this IOContext, or if not available, the attribute 
     * from the schema.
     * 
     * Attributes of this IOContext override (hide) attributes specified in the Schema's format attributes.
     * @param name
     * @return 
     */
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

    /**
     * @return copy of the schema
     */
    public Schema getSchema() {
        return schema == null ? null : new Schema(schema);
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
