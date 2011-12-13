package com.realcomp.data.view.io;

import com.realcomp.data.record.io.RecordReader;
import com.realcomp.data.schema.FileSchema;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.validation.Severity;
import java.io.IOException;
import java.io.InputStream;

/**
 * Wraps a RecordReader to read instances of an arbitrary class
 * 
 * @author krenfro
 */
public abstract class BaseRecordViewReader<T> implements RecordViewReader<T>{
    
    protected RecordReader reader;
    
    public BaseRecordViewReader(RecordReader reader){
        if (reader == null)
            throw new IllegalArgumentException("reader is null");
        
        this.reader = reader;
    }
       
    @Override
    public void close(){
        reader.close();
    }

    @Override
    public void open(InputStream in) throws IOException{
        reader.open(in);
    }
    
    
    @Override
    public FileSchema getSchema(){
        return reader.getSchema();
    }
    
    
    @Override
    public void setSchema(FileSchema schema) throws SchemaException{
        reader.setSchema(schema);
    }

    @Override
    public Severity getValidationExceptionThreshold(){
        return reader.getValidationExceptionThreshold();
    }

    
    @Override
    public void setValidationExceptionThreshold(Severity severity){
        reader.setValidationExceptionThreshold(severity);
    }

    @Override
    public long getCount(){
        return reader.getCount();
    }
    
}
