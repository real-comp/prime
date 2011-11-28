package com.realcomp.data.view.io;

import com.realcomp.data.record.io.RecordWriter;
import com.realcomp.data.schema.FileSchema;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.validation.Severity;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Wraps a RecordWriter to read instances of an arbitrary class
 * 
 * @author krenfro
 */
public abstract class BaseRecordViewWriter<T> implements RecordViewWriter{
    
    protected RecordWriter writer;
    
    public BaseRecordViewWriter(RecordWriter writer){
        if (writer == null)
            throw new IllegalArgumentException("writer is null");
        
        this.writer = writer;
    }
    
    @Override
    public void close(){
        writer.close();
    }

    @Override
    public void open(OutputStream out) throws IOException{
        writer.open(out);
    }
    
    @Override
    public void setSchema(FileSchema schema) throws SchemaException{
        writer.setSchema(schema);
    }    
    
    @Override
    public FileSchema getSchema() {
        return writer.getSchema();
    }    

    @Override
    public Severity getValidationExceptionThreshold(){
        return writer.getValidationExceptionThreshold();
    }
    
    @Override
    public void setValidationExceptionThreshold(Severity severity){
        writer.setValidationExceptionThreshold(severity);
    }

    @Override
    public long getCount(){
        return writer.getCount();
    }
}
