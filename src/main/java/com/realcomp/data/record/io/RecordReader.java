package com.realcomp.data.record.io;

import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.record.Record;
import com.realcomp.data.schema.FileSchema;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.validation.Severity;
import com.realcomp.data.validation.ValidationException;
import java.io.IOException;
import java.io.InputStream;


/**
 * Reads and parses records from an input stream.
 *
 * Note:
 * Implementors should ensure that their RecordReader has a default empty constructor.
 *
 * @author krenfro
 */
public interface RecordReader {
    

    /**
     * If a Validator logs a warning at or above this threshold, then the warning is thrown as
     * a ValidationException. By default, Validators log at Severity.MEDIUM, so all
     * validation failures are simply logged.  This mechanism, along with adjustable
     * Validator severity, allow for configurations where some validations are more
     * important than others.
     */
    public static final Severity DEFAULT_VALIDATION_THREASHOLD = Severity.HIGH;


   /**
    * Read and return the next Record, or null if there are no more.
    * @return the next Record, or null if no more records.
    * @throws IOException
    * @throws ValidationException
    * @throws ConversionException
    * @throws SchemaException
    */
    Record read() throws IOException, ValidationException, ConversionException, SchemaException;

    /**
     * Close open resources. Should be invoked when you are done with the RecordReader.
     */
    void close();

    /**
     * Open an IOContext for reading. May be invoked multiple times with new input as needed.
     * close() is automatically invoked before each open();
     *
     * @param context Not null
     * @throws IOException
     * @throws SchemaException
     */
    void open(IOContext context) throws IOException, SchemaException;
    
    /**
     * @return the IOContext being operated on; or null if not yet open
     */
    IOContext getIOContext();
    
    /**
     *
     * @return number of records read; not including skipped records.
     */
    long getCount();
}
