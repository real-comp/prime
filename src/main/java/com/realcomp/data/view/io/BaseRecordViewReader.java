package com.realcomp.data.view.io;

import com.realcomp.data.record.io.IOContext;
import com.realcomp.data.record.io.RecordReader;
import com.realcomp.data.schema.SchemaException;
import java.io.IOException;

/**
 * Wraps a RecordReader to read instances of an arbitrary class
 *
 * @author krenfro
 */
public abstract class BaseRecordViewReader<T> implements RecordViewReader<T>{

    protected RecordReader reader;

    public BaseRecordViewReader(RecordReader reader){
        if (reader == null){
            throw new IllegalArgumentException("reader is null");
        }

        this.reader = reader;
    }

    @Override
    public void close(){
        reader.close();
    }

    @Override
    public void open(IOContext context) throws IOException, SchemaException{
        reader.open(context);
    }

    @Override
    public long getCount(){
        return reader.getCount();
    }
}
