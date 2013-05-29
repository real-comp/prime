package com.realcomp.data.view.io;

import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.record.io.IOContext;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.validation.ValidationException;
import java.io.IOException;

/**
 * Wraps a RecordReader to read instances of an arbitrary class.
 *
 * @author krenfro
 */
public interface RecordViewReader<T>{

    T read() throws IOException, ValidationException, ConversionException, SchemaException;

    /**
     * Close open resources. Should be invoked when you are done with the RecordReader.
     */
    void close();

    /**
     * Open an InputStream for reading. May be invoked multiple times with new input as needed.
     * close() is automatically invoked before each open();
     *
     * @param context
     * @throws IOException
     * @throws SchemaException
     */
    void open(IOContext context) throws IOException, SchemaException;

    /**
     *
     * @return number of records read; not including skipped records.
     */
    long getCount();
}
