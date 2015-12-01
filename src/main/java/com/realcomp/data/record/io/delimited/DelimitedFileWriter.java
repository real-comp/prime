package com.realcomp.data.record.io.delimited;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVWriter;
import com.realcomp.data.DataType;
import com.realcomp.data.Operations;
import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.record.Record;
import com.realcomp.data.record.io.BaseRecordWriter;
import com.realcomp.data.record.io.IOContext;
import com.realcomp.data.schema.Field;
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
public class DelimitedFileWriter extends BaseRecordWriter{

    protected CSVWriter writer;
    protected List<String> current;
    protected TransformContext transformContext;
    protected ValueSurgeon surgeon;
    protected boolean headerWritten = false;

    public DelimitedFileWriter(){
        super();
        format.putDefault("header", "false");
        format.putDefault("type", "TAB");
        format.putDefault("quoteCharacter", Character.toString(CSVParser.DEFAULT_QUOTE_CHARACTER));
        format.putDefault("escapeCharacter", Character.toString(CSVParser.DEFAULT_ESCAPE_CHARACTER));
        format.putDefault("strictQuotes", Boolean.toString(CSVParser.DEFAULT_STRICT_QUOTES));
        format.putDefault("recordDelimiter", CSVWriter.DEFAULT_LINE_END);

        current = new ArrayList<>();
        transformContext = new TransformContext();
        surgeon = new ValueSurgeon();
        headerWritten = false;

    }

    public DelimitedFileWriter(DelimitedFileWriter copy){
        super(copy);
        format.putDefault("header", "false");
        format.putDefault("type", "TAB");
        format.putDefault("quoteCharacter", Character.toString(CSVParser.DEFAULT_QUOTE_CHARACTER));
        format.putDefault("escapeCharacter", Character.toString(CSVParser.DEFAULT_ESCAPE_CHARACTER));
        format.putDefault("strictQuotes", Boolean.toString(CSVParser.DEFAULT_STRICT_QUOTES));
        format.putDefault("recordDelimiter", CSVWriter.DEFAULT_LINE_END);

        current = new ArrayList<>();
        transformContext = new TransformContext(copy.transformContext);
        surgeon = new ValueSurgeon();
        headerWritten = false;
    }

    @Override
    protected void write(Record record, Field field)
            throws ValidationException, ConversionException, IOException{

        transformContext.setRecord(record);
        transformContext.setKey(field.getName());
        Object value = surgeon.operate(Operations.getOperations(schema, field), transformContext);

        if (value == null){
            current.add("");
        }
        else{
            current.add((String) DataType.STRING.coerce(value));
        }
    }

    @Override
    public void write(Record record)
            throws IOException, ValidationException, ConversionException, SchemaException{


        //optionally write header record
        if (!headerWritten && isHeader()){
            writeHeader();
        }

        current.clear();
        super.write(record);
        writer.writeNext(current.toArray(new String[current.size()]));
        writer.flush();
    }

    @Override
    public void open(IOContext context) throws IOException, SchemaException{

        super.open(context);
        transformContext.setSchema(schema);
        transformContext.setValidationExceptionThreshold(context.getValidationExeptionThreshold());
        if (context.getOut() == null){
            throw new IllegalArgumentException("Invalid IOContext. No OutputStream specified");
        }


        switch (getDelimiter()){
            case '\t':
                writer = new CSVWriter(new BufferedWriter(
                        new OutputStreamWriter(context.getOut(), getCharset())), '\t', '\u0000', "\n");
                break;
            default:
                writer = new CSVWriter(new BufferedWriter(
                        new OutputStreamWriter(
                        context.getOut(), getCharset())), getDelimiter(), getQuoteCharacter(), getEscapeCharacter(), getRecordDelimiter());
        }
        headerWritten = false;
    }

    /**
     * Write a header record, constructed from the schema.
     *
     * @throws IOException
     * @throws ValidationException
     * @throws ConversionException
     */
    protected void writeHeader() throws IOException, ValidationException, ConversionException{

        List<String> header = getHeader();
        writer.writeNext(header.toArray(new String[header.size()]));
        writer.flush();
        headerWritten = true;
    }


    protected List<String> getHeader(){
        List<String> header = new ArrayList<>();
        for (Field field : schema.getDefaultFieldList()){
            header.add(field.getName());
        }
        return header;
    }

    public char getDelimiter(){
        char delimiter;
        String type = format.get("type");
        if (type.equalsIgnoreCase("TAB")){
            delimiter = '\t';
        }
        else if (type.equalsIgnoreCase("CSV")){
            delimiter = ',';
        }
        else{
            if (type.length() != 1){
                throw new IllegalArgumentException("invalid type [" + type + "]");
            }
            delimiter = type.charAt(0);
        }

        return delimiter;
    }


    protected char getAttributeAsChar(String name){
        String value = format.get(name);
        if (value.isEmpty()){
            return '\u0000';
        }
        else if (value.length() != 1){
            throw new IllegalArgumentException(String.format("invalid attribute [%s] = [%s]", name, value));
        }
        return value.charAt(0);
    }

    public char getEscapeCharacter(){
        return getAttributeAsChar("escapeCharacter");
    }

    public char getQuoteCharacter(){
        return getAttributeAsChar("quoteCharacter");
    }

    public boolean isStrictQuotes(){
        return Boolean.parseBoolean(format.get("strictQuotes"));
    }

    public boolean isHeader(){
        return Boolean.parseBoolean(format.get("header"));
    }
    
    public String getRecordDelimiter(){
        return format.get("recordDelimiter");
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
