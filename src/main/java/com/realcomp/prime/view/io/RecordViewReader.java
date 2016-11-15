package com.realcomp.prime.view.io;

import com.realcomp.prime.conversion.ConversionException;
import com.realcomp.prime.record.io.IOContext;
import com.realcomp.prime.schema.SchemaException;
import com.realcomp.prime.validation.ValidationException;
import java.io.IOException;

/**
 * Wraps a RecordReader to read instances of an arbitrary class.
 *
 * @author krenfro
 */
public interface RecordViewReader<T> extends AutoCloseable{

    T read() throws IOException, ValidationException, ConversionException, SchemaException;

    /**
     * Close open resources. Should be invoked when you are done with the RecordReader.
     */
    void close() throws IOException;

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
