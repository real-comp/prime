package com.realcomp.data.record.io.delimited;

import au.com.bytecode.opencsv.CSVParser;
import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.record.Record;
import com.realcomp.data.record.io.BaseRecordReader;
import com.realcomp.data.record.io.IOContext;
import com.realcomp.data.record.io.SkippingBufferedReader;
import com.realcomp.data.schema.FieldList;
import com.realcomp.data.schema.Schema;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.validation.Severity;
import com.realcomp.data.validation.ValidationException;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author krenfro
 */
public class DelimitedFileReader extends BaseRecordReader{

    protected SkippingBufferedReader reader;
    protected CSVParser parser;
    private UnterminatedQuotedStringMechanic unterminatedMechanic;

    public DelimitedFileReader(){
        super();
        format.putDefault("header", "false");
        format.putDefault("type", "TAB");
        format.putDefault("quoteCharacter", Character.toString(CSVParser.DEFAULT_QUOTE_CHARACTER));
        format.putDefault("escapeCharacter", Character.toString(CSVParser.DEFAULT_ESCAPE_CHARACTER));
        format.putDefault("strictQuotes", Boolean.toString(CSVParser.DEFAULT_STRICT_QUOTES));
        unterminatedMechanic = new UnterminatedQuotedStringMechanic();
    }

    public DelimitedFileReader(DelimitedFileReader copy){
        super(copy);
        format.putDefault("header", "false");
        format.putDefault("type", "TAB");
        format.putDefault("quoteCharacter", Character.toString(CSVParser.DEFAULT_QUOTE_CHARACTER));
        format.putDefault("escapeCharacter", Character.toString(CSVParser.DEFAULT_ESCAPE_CHARACTER));
        format.putDefault("strictQuotes", Boolean.toString(CSVParser.DEFAULT_STRICT_QUOTES));
        unterminatedMechanic = new UnterminatedQuotedStringMechanic();
    }

    @Override
    public void open(IOContext context) throws IOException, SchemaException{
        super.open(context);
        if (context.getIn() == null){
            throw new IllegalArgumentException("Invalid IOContext. No InputStream specified");
        }

        reader = new SkippingBufferedReader(new InputStreamReader(context.getIn(), getCharset()));
        reader.setSkipLeading(getSkipLeading());
        reader.setSkipTrailing(getSkipTrailing());

        if (isHeader() && reader.getSkipLeading() == 0){
            reader.setSkipLeading(1);
        }

        switch (getDelimiter()){
            case '\t':
                parser = new CSVParser(getDelimiter(), '\u0000', getEscapeCharacter(), isStrictQuotes());
                break;
            default:
                parser = new CSVParser(getDelimiter(), getQuoteCharacter(), getEscapeCharacter(), isStrictQuotes());
        }
    }

    @Override
    public Record read() throws IOException, ValidationException, ConversionException, SchemaException{

        if (context.getSchema() == null){
            throw new IllegalStateException("schema not specified");
        }

        if (!beforeFirstOperationsRun){
            executeBeforeFirstOperations();
            beforeFirstOperationsRun = true;
        }

        Record record = null;
        String data = reader.readLine();
        String[] tokens;
        if (data != null){
            tokens = parse(data);
            record = loadRecord(classify(context.getSchema(), tokens), tokens);
        }

        if (record != null){
            count++;
        }
        else{
            executeAfterLastOperations();
        }

        return record;
    }

    protected String[] parse(String data) throws IOException{
        String[] tokens = null;
        try{
            tokens = parser.parseLine(data);
        }
        catch(IOException ex){
            if (ex.getMessage().contains("Un-terminated quoted field")){
                tokens = parser.parseLine(unterminatedMechanic.repair(data));
            }
            else{
                throw ex;
            }
        }
        return tokens;
    }

    /**
     * Classify some delimited data and return the FieldList that should be used to parse the data. If only one
     * FieldList is defined, then it is returned. If multiple FieldLists are defined, then the first FieldList has the
     * same number of fields as the data is returned.
     *
     * @param schema
     * @param data   not null
     * @return the FieldList that should be used to parse the data. never null
     * @throws SchemaException if more than one FieldList is defined and there is ambiguity
     */
    protected FieldList classify(Schema schema, String[] data) throws SchemaException{

        if (data == null){
            throw new IllegalArgumentException("data is null");
        }

        FieldList match = schema.getDefaultFieldList();

        if (schema.getFieldLists().size() > 1){
            int matchCount = 0;
            for (FieldList fieldList : schema.getFieldLists()){
                if (fieldList.size() == data.length){
                    match = fieldList;
                    matchCount++;
                    if (matchCount > 1){
                        throw new SchemaException(
                                "Ambiguous schema [" + schema.getName() + "]. "
                                + "Multiple field lists in the schema support records with "
                                + data.length + " fields.");
                    }
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
                    String.format(
                    "The number of fields in schema [%s] does not match number of fields in the data [%s].",
                    new Object[]{fields.size(), data.length}),
                    Severity.HIGH);
        }

        return recordFactory.build(fields, data);
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
        if (value.length() != 1){
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
