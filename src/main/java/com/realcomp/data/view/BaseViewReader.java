package com.realcomp.data.view;

import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.record.reader.RecordReader;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.validation.ValidationException;
import java.io.IOException;

/**
 *
 * @author krenfro
 */
public abstract class BaseViewReader implements ViewReader{

    protected RecordReader reader;

    /**
     * Create a new ViewReader, wrapping the specified RecordReader.
     * 
     * @param reader
     */
    public BaseViewReader(RecordReader reader){
        if (reader == null)
            throw new IllegalArgumentException("reader is null");
        this.reader = reader;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract Object read() throws IOException, ValidationException, ConversionException, SchemaException;

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract boolean supports(Class clazz);

    /**
     * {@inheritDoc}
     */
    @Override
    public void close(){
        reader.close();
    }
}
