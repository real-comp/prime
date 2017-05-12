package com.realcomp.prime.record.io.fixed;

import com.realcomp.prime.DataType;
import com.realcomp.prime.Operations;
import com.realcomp.prime.conversion.ConversionException;
import com.realcomp.prime.record.Record;
import com.realcomp.prime.record.io.BaseRecordWriter;
import com.realcomp.prime.record.io.IOContext;
import com.realcomp.prime.schema.Field;
import com.realcomp.prime.schema.FieldList;
import com.realcomp.prime.schema.Schema;
import com.realcomp.prime.schema.SchemaException;
import com.realcomp.prime.transform.TransformContext;
import com.realcomp.prime.transform.ValueSurgeon;
import com.realcomp.prime.validation.ValidationException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.logging.Logger;

public class FixedFileWriter extends BaseRecordWriter{

    protected static final Logger logger = Logger.getLogger(BaseRecordWriter.class.getName());

    protected BufferedWriter writer;
    protected TransformContext transformContext;
    protected ValueSurgeon surgeon;
    protected StringBuilder currentRecord;
    protected boolean headerWritten = false;

    public FixedFileWriter(){
        super();
        format.putDefault("header", "false");
        format.putDefault("type", "fixed");
        format.putDefault("length", "");
        transformContext = new TransformContext();
        surgeon = new ValueSurgeon();
        headerWritten = false;
    }

    @Override
    public void open(IOContext context) throws IOException, SchemaException{

        super.open(context);
        if (context.getOut() == null){
            throw new IllegalArgumentException("Invalid IOContext. No OutputStream specified");
        }

        ensureFieldLengthsSpecified(schema);
        transformContext.setSchema(schema);
        transformContext.setValidationExceptionThreshold(context.getValidationExeptionThreshold());
        writer = new BufferedWriter(new OutputStreamWriter(context.getOut(), getCharset()));
        headerWritten = false;
    }

    @Override
    public void close() throws IOException{
        IOUtils.closeQuietly(writer);
        super.close();
    }

    @Override
    public void close(boolean closeIOContext) throws IOException{
        IOUtils.closeQuietly(writer);
        super.close(closeIOContext);
    }

    @Override
    public void write(Record record)
            throws IOException, ValidationException, ConversionException, SchemaException{


        //optionally write header record
        if (!headerWritten && isHeader()){
            writeHeader();
        }

        currentRecord = new StringBuilder();
        super.write(record);
        writer.write(currentRecord.toString());
        writer.newLine();

    }

    /**
     * Write a header record, constructed from a Record.
     *
     * @throws IOException
     * @throws ValidationException
     * @throws ConversionException
     */
    protected void writeHeader() throws IOException, ValidationException, ConversionException{

        for (Field field : schema.getDefaultFieldList()){
            writer.write(resize(field.getName(), field.getLength()));
        }

        writer.newLine();
        writer.flush();
        headerWritten = true;
    }

    @Override
    protected void write(Record record, Field field)
            throws ValidationException, ConversionException, IOException{

        transformContext.setRecord(record);
        transformContext.setKey(field.getName());
        Object value = surgeon.operate(Operations.getOperations(schema, field), transformContext);
        currentRecord.append(resize((String) DataType.STRING.coerce(value == null ? "" : value), field.getLength()));
    }



    protected String resize(String s, int length){
        return StringUtils.rightPad(s, length).substring(0, length);
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
        int retVal = 0;
        for (Field field : fields){
            retVal += field.getLength();
        }
        return retVal;
    }

    public boolean isHeader(){
        return Boolean.parseBoolean(format.get("header"));
    }

    @Override
    protected void validateAttributes(){

        super.validateAttributes();
        isHeader();
    }
}
