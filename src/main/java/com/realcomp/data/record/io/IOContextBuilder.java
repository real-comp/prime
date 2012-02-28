package com.realcomp.data.record.io;

import com.realcomp.data.schema.Schema;
import com.realcomp.data.validation.Severity;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author krenfro
 */
public class IOContextBuilder {
    
    protected Schema schema;
    protected InputStream in;
    protected OutputStream out;
    protected Map<String, String> attributes;
    protected Severity validationExceptionThreshold = Severity.HIGH;

    public IOContextBuilder() {
        attributes = new HashMap<String, String>();
    }

    public IOContextBuilder(IOContext context) {
        attributes = new HashMap<String, String>();
        attributes.putAll(context.attributes);
        if (context.schema != null)
            schema = new Schema(context.schema);
        in = context.in;
        out = context.out;
        validationExceptionThreshold = context.validationExeptionThreshold;
    }

    public IOContextBuilder schema(Schema schema) {
        if (schema != null)
            this.schema = new Schema(schema);
        return this;
    }

    public IOContextBuilder in(InputStream in) {
        this.in = in;
        return this;
    }

    public IOContextBuilder out(OutputStream out) {
        this.out = out;
        return this;
    }

    public IOContextBuilder attributes(Map<String, String> attributes) {
        if (attributes != null)
            this.attributes.putAll(attributes);
        return this;
    }

    public IOContextBuilder attribute(String name, String value) {
        attributes.put(name, value);
        return this;
    }

    public IOContextBuilder validationExceptionThreshold(Severity severity) {
        if (severity == null)
            throw new IllegalArgumentException("severity is null");
        validationExceptionThreshold = severity;
        return this;
    }

    public IOContext build() {
        return new IOContext(this);
    }

}
