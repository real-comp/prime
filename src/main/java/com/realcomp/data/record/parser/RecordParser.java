package com.realcomp.data.record.parser;

import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.record.Record;
import com.realcomp.data.schema.FileSchema;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.validation.Severity;
import com.realcomp.data.validation.ValidationException;
import java.io.IOException;
import java.io.InputStream;


/**
 * Parses records from an input stream.
 *
 * Note:
 * Implementors should ensure that their RecordParser has a default empty constructor.
 *
 * @author krenfro
 */
public interface RecordParser {

    public static final Severity DEFAULT_VALIDATION_THREASHOLD = Severity.HIGH;

   /**
     * Read and return the next Record, or null if there are no more.
     * @return the next Record, or null if no more records.
     * @throws IOException
     */
    Record next() throws IOException, ValidationException, ConversionException, SchemaException;

    /**
     * Close open resources. Should be invoked when you are done with the RecordParser.
     */
    void close();

    /**
     * Open an InputStream for parsing. May be invoked multiple times with new input as needed.
     * close() is automatically invoked before each open();
     *
     *
     * @param in InputStream to parse. Not null
     */
    void open(InputStream in);


    /**
     * Set the schema that the RecordParser should use to create Records.
     * 
     * @param schema
     */
    void setSchema(FileSchema schema);

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


}
