package com.realcomp.data.record.io.fixed;

import com.realcomp.data.DataType;
import com.realcomp.data.Operation;
import com.realcomp.data.Operations;
import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.record.Record;
import com.realcomp.data.record.io.BaseRecordWriter;
import com.realcomp.data.record.io.IOContext;
import com.realcomp.data.record.io.IOContextBuilder;
import com.realcomp.data.schema.Field;
import com.realcomp.data.schema.FieldList;
import com.realcomp.data.schema.Schema;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.transform.TransformContext;
import com.realcomp.data.transform.ValueSurgeon;
import com.realcomp.data.validation.ValidationException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author krenfro
 */
public class FixedFileWriter extends BaseRecordWriter{

    protected static final Logger logger = Logger.getLogger(BaseRecordWriter.class.getName());

    protected BufferedWriter writer;
    protected TransformContext transformContext;
    protected ValueSurgeon surgeon;

    public FixedFileWriter(){
        super();
        format.putDefault("header", "false");
        format.putDefault("type", "fixed");
        format.putDefault("length", "");
        transformContext = new TransformContext();
        surgeon = new ValueSurgeon();
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
    }

    @Override
    public void close(){
        IOUtils.closeQuietly(writer);
        super.close();
    }

    @Override
    public void close(boolean closeIOContext){
        IOUtils.closeQuietly(writer);
        super.close(closeIOContext);
    }

    @Override
    public void write(Record record)
            throws IOException, ValidationException, ConversionException, SchemaException{

        //optionally write header record
        if (count == 0 && isHeader()){
            writeHeader();
        }

        super.write(record);
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
    }

    @Override
    protected void write(Record record, Field field)
            throws ValidationException, ConversionException, IOException{

        transformContext.setRecord(record);
        transformContext.setKey(field.getName());
        Object value = surgeon.operate(Operations.getOperations(schema, field), transformContext);
        writer.write(resize((String) DataType.STRING.coerce(value == null ? "" : value), field.getLength()));
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
