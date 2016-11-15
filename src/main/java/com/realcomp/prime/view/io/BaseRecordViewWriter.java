package com.realcomp.prime.view.io;

import com.realcomp.prime.record.io.IOContext;
import com.realcomp.prime.record.io.RecordWriter;
import com.realcomp.prime.schema.SchemaException;
import java.io.IOException;

/**
 * Wraps a RecordWriter to read instances of an arbitrary class
 *
 */
public abstract class BaseRecordViewWriter<T> implements RecordViewWriter<T>{

    protected RecordWriter writer;

    public BaseRecordViewWriter(RecordWriter writer){
        if (writer == null){
            throw new IllegalArgumentException("writer is null");
        }

        this.writer = writer;
    }

    @Override
    public void close() throws IOException{
        writer.close();
    }

    @Override
    public void open(IOContext context) throws IOException, SchemaException{
        writer.open(context);
    }

    @Override
    public long getCount(){
        return writer.getCount();
    }
}
