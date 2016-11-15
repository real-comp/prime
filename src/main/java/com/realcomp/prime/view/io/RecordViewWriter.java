package com.realcomp.prime.view.io;

import com.realcomp.prime.conversion.ConversionException;
import com.realcomp.prime.record.io.IOContext;
import com.realcomp.prime.schema.SchemaException;
import com.realcomp.prime.validation.ValidationException;

import java.io.IOException;

/**
 *
 * @author krenfro
 */
public interface RecordViewWriter<T> extends AutoCloseable{

    void write(T t)
            throws IOException, ValidationException, ConversionException, SchemaException;

    /**
     * Close open resources. Should be invoked when you are done with the RecordReader.
     */
    void close() throws IOException;

    /**
     * Open an OutputStream for writing. May be invoked multiple times with new output as needed.
     * close() is automatically invoked before each open();
     *
     * @param context IOContext to write to. Not null
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
