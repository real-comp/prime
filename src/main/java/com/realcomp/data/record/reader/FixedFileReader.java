package com.realcomp.data.record.reader;

import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.record.Record;
import com.realcomp.data.schema.Classifier;
import com.realcomp.data.schema.FileSchema;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.schema.SchemaField;
import com.realcomp.data.validation.Severity;
import com.realcomp.data.validation.ValidationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * There is an implicit Resize converter on all fields that runs after all
 * 'after' operations.
 * 
 * @author krenfro
 */
public class FixedFileReader extends BaseFileReader{

    protected boolean beforeFirst = true;

    
    public FixedFileReader(){
        super();
    }
    
    public FixedFileReader(FixedFileReader copy){
        super(copy);
    }

    @Override
    public void open(InputStream in) throws IOException{
        
        super.open(in);
        beforeFirst = true;
    }

    @Override
    public Record read()
            throws IOException, ValidationException, ConversionException, SchemaException{

        if (schema == null)
            throw new IllegalStateException("schema not specified");

        if (beforeFirst){
            executeBeforeFirstOperations();
            beforeFirst = true;
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

    @Override
    public void setSchema(FileSchema schema) throws SchemaException{
        ensureFieldLengthsSpecified(schema);
        super.setSchema(schema);
    }

    protected String[] parse(String record, List<SchemaField> fields)
            throws ValidationException, SchemaException{

        if (fields == null)
            throw new IllegalArgumentException("fields is null");
        if (fields.isEmpty())
            throw new IllegalArgumentException("fields is empty");

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



    protected void ensureFieldLengthsSpecified(FileSchema schema) throws SchemaException{
        for (Classifier c: schema.getClassifiers())
            ensureFieldLengthsSpecified(c.getFields());

        ensureFieldLengthsSpecified(schema.getFields());
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
