package com.realcomp.data.view.io;

import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.record.io.IOContext;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.validation.ValidationException;

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
