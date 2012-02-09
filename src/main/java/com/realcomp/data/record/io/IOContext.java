package com.realcomp.data.record.io;

import com.realcomp.data.schema.FileSchema;
import com.realcomp.data.validation.Severity;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 *
 * @author krenfro
 */
public class IOContext {

    protected Severity validationExeptionThreshold = Severity.HIGH;
    protected FileSchema schema;
    protected InputStream in;
    protected OutputStream out;
    protected Charset charset = Charset.defaultCharset();

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    
    public InputStream getIn() {
        return in;
    }

    public void setIn(InputStream in) {
        this.in = in;
    }

    public OutputStream getOut() {
        return out;
    }

    public void setOut(OutputStream out) {
        this.out = out;
    }

    public FileSchema getSchema() {
        return schema;
    }

    public void setSchema(FileSchema schema) {
        this.schema = schema;
    }

    public Severity getValidationExeptionThreshold() {
        return validationExeptionThreshold;
    }

    public void setValidationExeptionThreshold(Severity validationExeptionThreshold) {
        if (validationExeptionThreshold == null)
            throw new IllegalArgumentException("validationExceptionThreshold is null");
        this.validationExeptionThreshold = validationExeptionThreshold;
    }
    
}
