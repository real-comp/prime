package com.realcomp.data.record.io.fixed;

import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.record.Record;
import com.realcomp.data.record.io.BaseRecordReader;
import com.realcomp.data.record.io.IOContext;
import com.realcomp.data.record.io.SkippingBufferedReader;
import com.realcomp.data.schema.Field;
import com.realcomp.data.schema.FieldList;
import com.realcomp.data.schema.Schema;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.validation.Severity;
import com.realcomp.data.validation.ValidationException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;

/**
 * There is an implicit Resize converter on all fields that runs after all 'after' operations.
 *
 * @author krenfro
 */
public class FixedFileReader extends BaseRecordReader{

    private static final Logger logger = Logger.getLogger(FixedFileReader.class.getName());
    
    protected SkippingBufferedReader reader;

    public FixedFileReader(){
        super();
        format.putDefault("header", "false");
        format.putDefault("type", "fixed");
        format.putDefault("length", "");
    }

    public FixedFileReader(FixedFileReader copy){
        super(copy);
        format.putDefault("header", "false");
        format.putDefault("type", "fixed");
        format.putDefault("length", "");
    }

    @Override
    public void open(IOContext context) throws IOException, SchemaException{
        super.open(context);
        if (context.getIn() == null){
            throw new IllegalArgumentException("Invalid IOContext. No InputStream specified");
        }

        ensureFieldLengthsSpecified(context.getSchema());
        reader = new SkippingBufferedReader(new InputStreamReader(context.getIn(), getCharset()));
        reader.setSkipLeading(getSkipLeading());
        reader.setSkipTrailing(getSkipTrailing());

        if (isHeader() && reader.getSkipLeading() == 0){
            reader.setSkipLeading(1);
        }

        if (getLength() > 0){
            reader.setLength(getLength());
        }
    }

    @Override
    public void close(){
        IOUtils.closeQuietly(reader);
        super.close();
    }

    @Override
    public void close(boolean closeIOContext){
        IOUtils.closeQuietly(reader);
        super.close(closeIOContext);
    }

    @Override
    public Record read()
            throws IOException, ValidationException, ConversionException, SchemaException{

        if (context.getSchema() == null){
            throw new IllegalStateException("schema not specified");
        }

        if (!beforeFirstOperationsRun){
            executeBeforeFirstOperations();
            beforeFirstOperationsRun = true;
        }

        Record record = null;
        String data = reader.readLine();
        if (data != null){
            FieldList fields = classify(context.getSchema(), data);
            String[] parsed = parse(data, fields);
            record = loadRecord(fields, parsed);
        }

        if (record != null){
            count++;
        }
        else{
            executeAfterLastOperations();
        }

        return record;
    }

    /**
     * Classify some data and return the FieldList that should be used to parse the data. If only one FieldList is
     * defined, then it is returned. If multiple FieldLists are defined, then the first FieldList who's regex classifier
     * matches the data is returned
     *
     * @param data not null
     * @return the FieldList that should be used to parse the data. never null
     * @throws SchemaException if no defined layout supports the data.
     */
    protected FieldList classify(Schema schema, String data) throws SchemaException{

        if (data == null){
            throw new IllegalArgumentException("data is null");
        }

        FieldList match = schema.getDefaultFieldList();

        if (schema.getFieldLists().size() > 1){
            for (FieldList fieldList : schema.getFieldLists()){
                if (fieldList.supports(data)){
                    match = fieldList;
                }
            }
        }

        if (match == null){
            throw new SchemaException("The schema [" + schema.getName() + "] does not support the specified data.");
        }

        return match;
    }

    protected Record loadRecord(FieldList fields, String[] data)
            throws ValidationException, ConversionException{

        if (fields == null){
            throw new IllegalArgumentException("fields is null");
        }
        if (data == null){
            throw new IllegalArgumentException("data is null");
        }

        if (fields.size() != data.length){
            throw new ValidationException(
                    "number of fields in schema does not match data.",
                    fields.size() + " != " + data.length,
                    Severity.HIGH);
        }

        return recordFactory.build(fields, data);
    }

    protected String[] parse(String record, FieldList fields)
            throws ValidationException, SchemaException{

        if (fields == null){
            throw new IllegalArgumentException("fields is null");
        }
        if (fields.isEmpty()){
            throw new IllegalArgumentException("fields is empty");
        }

        int expectedLength = getExpectedLength(fields);
        int actualLength = record.length();

        if (expectedLength != actualLength){
            throw new ValidationException(
                    String.format("Record length != expected length (%s != %s)", actualLength, expectedLength),
                    record,
                    Severity.HIGH);
        }

        String[] result = new String[fields.size()];
        int index = 0;
        int start = 0;
        int stop;

        for (Field field : fields){
            stop = start + field.getLength();
            result[index] = record.substring(start, stop);
            index++;
            start = stop;
        }

        return result;
    }

    protected void ensureFieldLengthsSpecified(Schema schema) throws SchemaException{
        for (FieldList fields : schema.getFieldLists()){
            ensureFieldLengthsSpecified(fields);
        }
    }

    protected void ensureFieldLengthsSpecified(FieldList fields) throws SchemaException{
        for (Field field : fields){
            if (field.getLength() <= 0){
                throw new SchemaException("field length not specified for: " + field);
            }
        }
    }

    protected int getExpectedLength(List<Field> fields){

        assert (fields != null);
        if (getLength() > 0){
            return getLength();
        }
        else{
            int retVal = 0;
            for (Field field : fields){
                retVal += field.getLength();
            }
            return retVal;
        }
    }

    public boolean isHeader(){
        return Boolean.parseBoolean(format.get("header"));
    }

    public int getLength(){
        String length = format.get("length");
        return length == null || length.isEmpty() ? 0 : Integer.parseInt(length);
    }

    @Override
    protected void validateAttributes(){
        super.validateAttributes();
        isHeader();
    }
}
