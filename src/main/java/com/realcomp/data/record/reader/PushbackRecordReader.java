package com.realcomp.data.record.reader;

import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.record.Record;
import com.realcomp.data.schema.FileSchema;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.validation.Severity;
import com.realcomp.data.validation.ValidationException;
import com.realcomp.data.view.RecordView;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Wraps a RecordReader to provide a push-back feature to un-read Records.
 * 
 * @author krenfro
 */
public class PushbackRecordReader implements RecordReader{
    
    private RecordReader reader;
    private Queue<Record> queue;
    
    public PushbackRecordReader(RecordReader reader){
        queue = new LinkedList<Record>();
        this.reader = reader;
    }

    public void pushBack(Record record){
        queue.add(record);
    }    
    
    @Override
    public long getCount() {
        return reader.getCount() - queue.size();
    }
    
    
    @Override
    public Record read() throws IOException, ValidationException, ConversionException, SchemaException {
        
        return queue.isEmpty() ? reader.read() : queue.remove();
    }

    @Override
    public RecordView read(Class clazz) throws IOException, ValidationException, ConversionException, SchemaException {
        return reader.read(clazz);
    }

    @Override
    public void close() {
        reader.close();
    }

    @Override
    public void open(InputStream in) throws IOException {
        reader.open(in);
    }

    @Override
    public void open(InputStream in, Charset charset) throws IOException {
        reader.open(in, charset);
    }

    @Override
    public void setSchema(FileSchema schema) throws SchemaException {
        reader.setSchema(schema);
    }

    @Override
    public FileSchema getSchema() {
        return reader.getSchema();
    }

    @Override
    public Severity getValidationExceptionThreshold() {
        return reader.getValidationExceptionThreshold();
    }

    @Override
    public void setValidationExceptionThreshold(Severity severity) {
        reader.setValidationExceptionThreshold(severity);
    }

    @Override
    public boolean supports(Class clazz) {
        return reader.supports(clazz);
    }

    @Override
    public void setViews(List<String> viewClassNames) {
        reader.setViews(viewClassNames);
    }

    @Override
    public List<String> getViews() {
        return reader.getViews();
    }
    
}
