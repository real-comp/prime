package com.realcomp.data.view.io;

import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.schema.FileSchema;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.validation.Severity;
import com.realcomp.data.validation.ValidationException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Wraps a RecordReader to read instances of an arbitrary class.
 * 
 * @author krenfro
 */
public interface RecordViewReader<T> {
    
    
    T read() throws IOException, ValidationException, ConversionException, SchemaException;
    
    
    /**
     * Close open resources. Should be invoked when you are done with the RecordReader.
     */
    void close();

    /**
     * Open an InputStream for reading. May be invoked multiple times with new input as needed.
     * close() is automatically invoked before each open();
     *
     * @param in InputStream to parse. Not null
     * @throws IOException
     */
    void open(InputStream in) throws IOException;
    
    /**
     * Set the schema that the RecordReader should use to create Records.
     * 
     * @param schema
     * @throws SchemaException
     */
    void setSchema(FileSchema schema) throws SchemaException;

    /**
     *
     * @return the current Schema, or null if none set
     */
    FileSchema getSchema();


    /**
     * @return the Severity level that will cause ValidationExceptions to be thrown instead of
     *  logged.
     */
    Severity getValidationExceptionThreshold();

    /**
     * 
     * @param severity the severity level that will cause ValidationExceptions to be thrown
     * instead of logged. not null
     */
    void setValidationExceptionThreshold(Severity severity);

    /**
     *
     * @return number of records read; not including skipped records.
     */
    long getCount();
    
}
