package com.realcomp.data.record.io.delimited;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVWriter;
import com.realcomp.data.DataType;
import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.record.Record;
import com.realcomp.data.record.io.BaseRecordWriter;
import com.realcomp.data.record.io.IOContext;
import com.realcomp.data.schema.Field;
import com.realcomp.data.schema.FieldList;
import com.realcomp.data.schema.FileSchema;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.transform.TransformContext;
import com.realcomp.data.transform.ValueSurgeon;
import com.realcomp.data.validation.ValidationException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author krenfro
 */
public class DelimitedFileWriter extends BaseRecordWriter {

    protected CSVWriter writer;
    protected List<String> current;
    protected TransformContext xCtx;
    protected ValueSurgeon surgeon;
    
    public DelimitedFileWriter(){
        super();
        format.putDefaultValue("header", "false");
        format.putDefaultValue("type", "TAB");
        format.putDefaultValue("quoteCharacter", Character.toString(CSVParser.DEFAULT_QUOTE_CHARACTER));
        format.putDefaultValue("escapeCharacter", Character.toString(CSVParser.DEFAULT_ESCAPE_CHARACTER));
        format.putDefaultValue("strictQuotes", Boolean.toString(CSVParser.DEFAULT_STRICT_QUOTES));
        
        current = new ArrayList<String>();
        xCtx = new TransformContext();
        surgeon = new ValueSurgeon();
    }

    @Override
    protected void write(Record record, Field field)
            throws ValidationException, ConversionException, IOException {

        xCtx.setRecord(record);
        xCtx.setKey(field.getName());
        Object value = surgeon.operate(field.getOperations(), xCtx);

        if (value == null)
            current.add("");
        else
            current.add((String) DataType.STRING.coerce(value));
    }

    @Override
    public void write(Record record)
            throws IOException, ValidationException, ConversionException, SchemaException {

        //optionally write header record
        if (count == 0 && isHeader()) {
            current.clear();
            writeHeader();
        }

        current.clear();
        super.write(record);
        writer.writeNext(current.toArray(new String[current.size()]));
        writer.flush();
    }

    @Override
    public void open(IOContext context) throws IOException, SchemaException {

        super.open(context);
        xCtx.setSchema(context.getSchema());
        switch (getDelimiter()) {
            case '\t':
                writer = new CSVWriter(new BufferedWriter(
                        new OutputStreamWriter(context.getOut(), getCharset())), '\t', '\u0000');
                break;
            default:
                writer = new CSVWriter(new BufferedWriter(
                        new OutputStreamWriter(
                        context.getOut(), getCharset())), getDelimiter(), getQuoteCharacter(), getEscapeCharacter());
        }
    }

    
    /**
     * Write a header record, constructed from a Record.
     *
     * @throws IOException
     * @throws ValidationException
     * @throws ConversionException
     */
    protected void writeHeader() throws IOException, ValidationException, ConversionException {

        //No operations should be run on the Record, so a temporary schema
        // is created with no operations.
        try {
            FileSchema originalSchema = context.getSchema();
            FileSchema headerSchema = new FileSchema(context.getSchema());
            for (FieldList fields : headerSchema.getFieldLists()) {
                for (Field field : fields) {
                    field.clearOperations();
                }
            }

            context.setSchema(headerSchema);
            super.write(getHeader());
            writer.writeNext(current.toArray(new String[current.size()]));
            writer.flush();
            context.setSchema(originalSchema); //put back the original schema
        } catch (SchemaException ex) {
            throw new IOException("Unable to create temporary header schema: " + ex.getMessage());
        }
    }

    protected Record getHeader() {
        Record retVal = new Record();
        for (Field field : context.getSchema().getDefaultFieldList()) {
            retVal.put(field.getName(), field.getName());
        }
        return retVal;
    }

    protected char getDelimiter(){
        char delimiter;
        String type = format.get("type");
        if (type.equalsIgnoreCase("TAB")){
            delimiter = '\t';
        }
        else if (type.equalsIgnoreCase("CSV")){
            delimiter = ',';
        }
        else{
            if (type.length() != 1)
                throw new IllegalArgumentException("invalid type [" + type + "]");
            delimiter = type.charAt(0);
        }   
        
        return delimiter;
    }
    
    protected char getAttributeAsChar(String name){
        String value = format.get(name);
        if (value.length() != 1)
            throw new IllegalArgumentException(String.format("invalid attribute [%s] = [%s]", name, value));
        return value.charAt(0);
    }
    
    protected char getEscapeCharacter(){
        return getAttributeAsChar("escapeCharacter");
    }
    
    protected char getQuoteCharacter(){
        return getAttributeAsChar("quoteCharacter");
    }
    
    protected boolean isStrictQuotes(){
        return Boolean.parseBoolean(format.get("strictQuotes"));
    }
    
    protected boolean isHeader(){
        return Boolean.parseBoolean(format.get("header"));
    }
    
    @Override
    protected void validateAttributes(){
        super.validateAttributes();
        getDelimiter();
        getEscapeCharacter();
        getQuoteCharacter();
        isStrictQuotes();
        isHeader();
    }

}
