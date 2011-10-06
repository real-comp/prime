package com.realcomp.data.record.reader;

import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.record.Record;
import com.realcomp.data.schema.FileSchema;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.schema.Field;
import com.realcomp.data.schema.FieldList;
import com.realcomp.data.validation.Severity;
import com.realcomp.data.validation.ValidationException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
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
        open(in, Charset.forName(charset));
    }
    
    @Override
    public void open(InputStream in, Charset charset) throws IOException{
        
        super.open(in, charset);
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
            FieldList fields = schema.classify(data);            
            String[] parsed = parse(data, fields);
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

    protected String[] parse(String record, FieldList fields)
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
        
        for (Field field: fields){
            stop = start + field.getLength();
            result[index] = record.substring(start, stop);
            index++;
            start = stop;
        }

        return result;
    }



    protected void ensureFieldLengthsSpecified(FileSchema schema) throws SchemaException{
        
        for (FieldList fields: schema.getFieldLists())
            ensureFieldLengthsSpecified(fields);
    }
    
    
    protected void ensureFieldLengthsSpecified(FieldList fields) throws SchemaException{        
        for (Field field: fields)
            if (field.getLength() <= 0)
                throw new SchemaException("field length not specified for: " + field);
    }

    
    protected int getExpectedLength(List<Field> fields){

        assert(fields != null);
        int retVal = 0;
        for (Field field: fields)
            retVal = retVal + field.getLength();
        return retVal;
    }
    
}
