package com.realcomp.data.view;

import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.validation.ValidationException;
import java.io.IOException;

/**
 * Wraps a RecordReader to read View specific classes instead of Records
 * from an input source.
 *
 * @author krenfro
 */
public interface ViewReader{

    /**
     * @return the next View, or null if there are no more.
     */
    Object read() throws IOException, ValidationException, ConversionException, SchemaException;

    /**
     * Close the reader.
     */
    void close();


    /**
     * @param clazz not null
     * @return true if the specified class is supported by this ViewReader
     */
    boolean supports(Class clazz);
}
