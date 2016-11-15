package com.realcomp.prime.record.io;

import com.realcomp.prime.conversion.ConversionException;
import com.realcomp.prime.record.Record;
import com.realcomp.prime.schema.SchemaException;
import com.realcomp.prime.validation.Severity;
import com.realcomp.prime.validation.ValidationException;

import java.io.IOException;

/**
 * Writes records to an output stream. Any Classifier(s) in a schema are ignored.
 *
 * Note: Implementors should ensure that their RecordWriter has a default empty constructor.
 *
 * @author krenfro
 */
public interface RecordWriter extends AutoCloseable{

    /**
     * If a Validator logs a warning at or above this threshold, then the warning is thrown as a ValidationException. By
     * default, Validators log at Severity.MEDIUM, so all validation failures are simply logged. This mechanism, along
     * with adjustable Validator severity, allow for configurations where some validations are more important than
     * others.
     */
    public static final Severity DEFAULT_VALIDATION_THREASHOLD = Severity.HIGH;

    /**
     * write a Record
     *
     * @param record the Record to write; not null
     * @throws IOException
     * @throws ValidationException
     * @throws ConversionException
     * @throws SchemaException
     */
    void write(Record record)
            throws IOException, ValidationException, ConversionException, SchemaException;

    /**
     * Close open resources including the resources of the IOContext. Should be invoked when you are done with the
     * RecordReader.
     */
    @Override
    void close() throws IOException;

    /**
     * Close open resources, optionally closing the resources of the IOContext.
     *
     * @param closeIOContext if true, the ioContext.close() method will be invoked
     */
    void close(boolean closeIOContext) throws IOException;

    /**
     * Open an IOContext for writing. May be invoked multiple times with new output as needed. close() is automatically
     * invoked before each open();
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

    /**
     * @return the IOContext being operated on; or null if not yet open
     */
    IOContext getIOContext();
}
