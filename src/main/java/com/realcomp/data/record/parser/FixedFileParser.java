package com.realcomp.data.record.parser;

import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.record.Record;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.schema.SchemaField;
import com.realcomp.data.validation.Severity;
import com.realcomp.data.validation.ValidationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author krenfro
 */
public class FixedFileParser extends BaseFileParser{


    protected BufferedReader reader;
    protected boolean leadingRecordsSkipped = false;


    @Override
    public void open(InputStream in){
        close();
        super.open(in);
        reader = new BufferedReader(new InputStreamReader(in));
        leadingRecordsSkipped = false;
    }

    @Override
    public void close(){
        if (reader != null)
            IOUtils.closeQuietly(reader);
        super.close();
    }
    
    @Override
    public Record next() throws IOException, ValidationException, ConversionException, SchemaException{

        if (schema == null)
            throw new IllegalStateException("schema not specified");

         if (!leadingRecordsSkipped){
            executeBeforeFirstOperations();
            for (int x = 0; x < super.getSkipLeading(); x++)
                reader.readLine();
            leadingRecordsSkipped = true;
        }

        Record record = null;
        String data = reader.readLine();
        if (data != null){
            List<SchemaField> fields = schema.classify(data);            
            String[] parsed = parse(data, schema.classify(data));
            record = loadRecord(fields, parsed);
        }

        if (record != null)
            count++;
        else
            executeAfterLastOperations();
        
        return record;
    }

    protected String[] parse(String record, List<SchemaField> fields) throws ValidationException, SchemaException{

        if (fields == null)
            throw new IllegalArgumentException("fields is null");
        if (fields.isEmpty())
            throw new IllegalArgumentException("fields is empty");

        ensureFieldLengthsSpecified(fields);
        int expectedLength = getExpectedLength(fields);
        int length = record.length();

        if (expectedLength != length)
            throw new ValidationException(
                String.format("Record length != expected length (%s != %s)", length, expectedLength),
                record,
                Severity.HIGH);

        String[] result = new String[fields.size()];
        int index = 0;
        int start = 0;
        int stop = -1;
        
        for (SchemaField field: fields){
            stop = start + field.getLength();
            result[index] = record.substring(start, stop);
            index++;
            start = stop;
        }

        return result;
    }

    protected void ensureFieldLengthsSpecified(List<SchemaField> fields) throws SchemaException{
        for (SchemaField field: fields)
            if (field.getLength() <= 0)
                throw new SchemaException("field length not specified for: " + field);
    }

    
    protected int getExpectedLength(List<SchemaField> fields){

        assert(fields != null);
        int retVal = 0;
        for (SchemaField field: fields)
            retVal = retVal + field.getLength();
        return retVal;
    }
    
}
