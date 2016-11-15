package com.realcomp.prime.view.io;

import com.realcomp.prime.record.io.IOContext;
import com.realcomp.prime.record.io.RecordReader;
import com.realcomp.prime.schema.SchemaException;
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
    public void close() throws IOException{
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
