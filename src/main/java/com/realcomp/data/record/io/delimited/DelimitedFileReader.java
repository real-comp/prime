package com.realcomp.data.record.io.delimited;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVWriter;
import com.realcomp.data.DataType;
import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.record.Record;
import com.realcomp.data.record.io.BaseRecordReader;
import com.realcomp.data.record.io.IOContext;
import com.realcomp.data.record.io.SkippingBufferedReader;
import com.realcomp.data.schema.Field;
import com.realcomp.data.schema.FieldList;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.validation.Severity;
import com.realcomp.data.validation.ValidationException;
import com.realcomp.data.validation.field.RegexValidator;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class DelimitedFileReader extends BaseRecordReader{

    private static final Logger logger = Logger.getLogger(DelimitedFileReader.class.getName());
    
    protected SkippingBufferedReader reader;
    protected CSVParser parser;
    private final UnterminatedQuotedStringMechanic unterminatedMechanic;
    
    /**
     * When the schema describes a header, the skipped header record
     * is parsed as a FieldList and used as a hint when parsing the data in a file.
     * This hint is important only when there are multiple FieldLists described in a Schema.
     */
    private FieldList headerFieldList;

    public DelimitedFileReader(){
        super();
        format.putDefault("header", "false");
        format.putDefault("type", "TAB");
        format.putDefault("quoteCharacter", Character.toString(CSVParser.DEFAULT_QUOTE_CHARACTER));
        format.putDefault("escapeCharacter", Character.toString(CSVParser.DEFAULT_ESCAPE_CHARACTER));
        format.putDefault("strictQuotes", Boolean.toString(CSVParser.DEFAULT_STRICT_QUOTES));
        format.putDefault("recordDelimiter", CSVWriter.DEFAULT_LINE_END);
        unterminatedMechanic = new UnterminatedQuotedStringMechanic();
    }

    public DelimitedFileReader(DelimitedFileReader copy){
        super(copy);
        format.putDefault("header", "false");
        format.putDefault("type", "TAB");
        format.putDefault("quoteCharacter", Character.toString(CSVParser.DEFAULT_QUOTE_CHARACTER));
        format.putDefault("escapeCharacter", Character.toString(CSVParser.DEFAULT_ESCAPE_CHARACTER));
        format.putDefault("strictQuotes", Boolean.toString(CSVParser.DEFAULT_STRICT_QUOTES));
        format.putDefault("recordDelimiter", CSVWriter.DEFAULT_LINE_END);
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
                parser = new CSVParser(getDelimiter(), '\u0000', getEscapeCharacter(), false);
                break;
            default:
                parser = new CSVParser(getDelimiter(), getQuoteCharacter(), getEscapeCharacter(), isStrictQuotes());
        }
    }

    @Override
    public Record read() throws IOException, ValidationException, ConversionException, SchemaException{

        if (schema == null){
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
            record = loadRecord(classify(tokens), tokens);
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
     * @param data   not null
     * @return the FieldList that should be used to parse the data. never null
     * @throws SchemaException if more than one FieldList is defined and there is ambiguity
     */
    protected FieldList classify(String[] data) throws SchemaException{

        if (data == null){
            throw new IllegalArgumentException("data is null");
        }

        FieldList match = defaultFieldList;

        if (fieldListCount > 1){            
            if (headerFieldList == null && isHeader()){
                if (!reader.getSkipped().isEmpty()){
                    List<String> skipped = reader.getSkipped();
                    String header = skipped.get(skipped.size() - 1);
                    headerFieldList = createFieldListFromHeader(header);
                }
            }
            
                      
            List<FieldList> candidates = getCandidateFieldLists(data.length);            
            if (candidates.size() == 1){
                match = candidates.get(0);
            }
            else if (candidates.size() > 1){
                throw new SchemaException(
                        "Ambiguous schema [" + schema.getName() + "]. "
                        + "Multiple field lists in the schema support records with "
                        + data.length + " fields.");
            }
        }

        if (match == null){
            throw new SchemaException("The schema [" + schema.getName() + "] does not support the specified data.");
        }

        return match;
    }
    
    protected List<FieldList> getFieldListsOfSize(int size){
        List<FieldList> result = new ArrayList<>();
        for (FieldList fieldList : schema.getFieldLists()){
            if (fieldList.size() == size){
                result.add(fieldList);
            }
        }
        return result;
    }
    
    
    protected List<FieldList> getCandidateFieldLists(int size){
        List<FieldList> candidates = getFieldListsOfSize(size);
        if (candidates.size() > 1 && headerFieldList != null && headerFieldList.size() == size){
            List<FieldList> bad = new ArrayList<>();
            for (FieldList entry: candidates){
                for (int x = 0; x < size; x++){
                    if (!entry.get(x).getName().equals(headerFieldList.get(x).getName())){
                        bad.add(entry);
                        break;
                    }
                }
            }
            
            if (candidates.size() - bad.size() == 1){
                //if there was one match to the header record, use it.
                candidates.removeAll(bad);
            }
        }
        return candidates;
    }
    
    
    
    protected FieldList createFieldListFromHeader(String header){
        
        try{
            String[] tokens = parse(header);
            FieldList result = new FieldList();
            for (String token: tokens){
                Field field = new Field(token);
                field.addOperation(new RegexValidator(token));
                result.add(field);
            }
            return result;
        }
        catch (IOException ignored){
            logger.fine("Unable to parse the header record: " + ignored.getMessage());
            return null;
        }
    }
    
    protected FieldList removeOperationsAndTypes(FieldList original){
        FieldList result = new FieldList(original);
        result.clear();
        for (Field field: original){
            Field noops = new Field(field);
            noops.clearOperations();
            noops.setType(DataType.STRING);
            result.add(noops); 
        }
        return result;
    }
    
    
    protected boolean doesFieldListHaveAllFields(Record record, FieldList fieldList){
        Objects.requireNonNull(record);
        Objects.requireNonNull(fieldList);        
        List<String> names = new ArrayList<>();
        for (Field field: fieldList){
            names.add(field.getName());
        }
        return Arrays.deepEquals(names.toArray(), record.keySet().toArray());
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
