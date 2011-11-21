package com.realcomp.data.record.io.fixed;

import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.record.Record;
import com.realcomp.data.record.io.BaseRecordReader;
import com.realcomp.data.record.io.SkippingBufferedReader;
import com.realcomp.data.schema.FileSchema;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.schema.Field;
import com.realcomp.data.schema.FieldList;
import com.realcomp.data.validation.Severity;
import com.realcomp.data.validation.ValidationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;

/**
 * There is an implicit Resize converter on all fields that runs after all
 * 'after' operations.
 * 
 * @author krenfro
 */
public class FixedFileReader extends BaseRecordReader{
    
    private static final Logger logger = Logger.getLogger(FixedFileReader.class.getName());
    
    protected boolean header = false;
    protected SkippingBufferedReader reader;
    
    public FixedFileReader(){
        super();
    }
    
    public FixedFileReader(FixedFileReader copy){
        super(copy);
    }
    
    
    
    @Override
    public void open(InputStream in) throws IOException{
        
        close();
        super.open(in);
        
        Charset c = charset == null ? Charset.defaultCharset() : Charset.forName(charset);
        reader = new SkippingBufferedReader(new InputStreamReader(in, c));
        reader.setSkipLeading(skipLeading);
        reader.setSkipTrailing(skipTrailing);
        charset = c.name();
        count = 0;
    }


    @Override
    public void close(){        
        super.close();
        IOUtils.closeQuietly(reader);        
    }
    
    @Override
    public Record read()
            throws IOException, ValidationException, ConversionException, SchemaException{

        if (schema == null)
            throw new IllegalStateException("schema not specified");

        if (!beforeFirstOperationsRun){
            executeBeforeFirstOperations();
            beforeFirstOperationsRun = true;
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

    
    protected Record loadRecord(FieldList fields, String[] data)
            throws ValidationException, ConversionException{

        if (fields == null)
            throw new IllegalArgumentException("fields is null");
        if (data == null)
            throw new IllegalArgumentException("data is null");

        if (fields.size() != data.length)
            throw new ValidationException(
                    "number of fields in schema does not match data.",
                    fields.size() + " != " + data.length,
                    Severity.HIGH);

        return recordFactory.build(fields, data);
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
    
    
    public boolean isHeader() {
        return header;
    }

    public void setHeader(boolean header) {
        this.header = header;
        if (header && skipLeading == 0)
            skipLeading = 1;
        else if (!header && skipLeading == 1)
            skipLeading = 0;
    }
    
    
    
}
