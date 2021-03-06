package com.realcomp.prime.record.io.delimited;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVWriter;
import com.realcomp.prime.DataType;
import com.realcomp.prime.conversion.ConversionException;
import com.realcomp.prime.record.Record;
import com.realcomp.prime.record.io.BaseRecordReader;
import com.realcomp.prime.record.io.IOContext;
import com.realcomp.prime.record.io.SkippingBufferedReader;
import com.realcomp.prime.schema.Field;
import com.realcomp.prime.schema.FieldList;
import com.realcomp.prime.schema.SchemaException;
import com.realcomp.prime.validation.RawValidationException;
import com.realcomp.prime.validation.Severity;
import com.realcomp.prime.validation.ValidationException;
import com.realcomp.prime.validation.field.RegexValidator;
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
        format.putDefault("delimiter", "\t");
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
        format.putDefault("delimiter", "\t");
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
            try{
                FieldList fieldList = null;
                try {
                    //it is faster to match on the number of the tokens than the schema classifier system.
                    fieldList = classify(tokens);
                }
                catch(SchemaException ex){
                    try{
                        fieldList = classify(data, tokens.length);
                    }
                    catch(SchemaException ignore){
                        throw ex;
                    }
                }

                record = loadRecord(fieldList, tokens);
            }
            catch(ValidationException ex){
                if (ex instanceof RawValidationException){
                    ((RawValidationException) ex).setRaw(data);
                    throw ex;
                }
                else{
                    throw new RawValidationException(ex, data);
                }
            }
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
     * Classify some data and return the FieldList that should be used to parse the data. If only one FieldList is
     * defined, then it is returned. If multiple FieldLists are defined, then the first FieldList who's regex classifier
     * matches the data is returned
     *
     * @param data not null
     * @param numTokens number of tokens in the delimited raw data
     * @return the FieldList that should be used to parse the data. never null
     * @throws SchemaException if no defined layout supports the data.
     */
    protected FieldList classify(String data, int numTokens) throws SchemaException{

        assert(data != null);
        FieldList match = defaultFieldList;

        if (fieldListCount > 1){
            if (headerFieldList == null && isHeader()){
                if (!reader.getSkipped().isEmpty()){
                    List<String> skipped = reader.getSkipped();
                    String header = skipped.get(skipped.size() - 1);
                    headerFieldList = createFieldListFromHeader(header);
                }
            }
            for (FieldList fieldList : getCandidateFieldLists(numTokens)){
                if (fieldList.supports(data)){
                    match = fieldList;
                }
            }

            if (match == null){
                throw new SchemaException("The schema [" + schema.getName() + "] does not support the specified data.");
            }
        }


        return match;
    }


    /**
     * Classify some delimited data and return the FieldList that should be used to parse the data. If only one
     * FieldList is defined, then it is returned. If multiple FieldLists are defined, then the first FieldList has the
     * same number of fields as the data is returned.
     *
     * @param tokens   not null
     * @return the FieldList that should be used to parse the data. never null
     * @throws SchemaException if more than one FieldList is defined and there is ambiguity
     */
    protected FieldList classify(String[] tokens) throws SchemaException{
        Objects.requireNonNull(tokens);
        FieldList match = defaultFieldList;

        if (fieldListCount > 1){            
            if (headerFieldList == null && isHeader()){
                if (!reader.getSkipped().isEmpty()){
                    List<String> skipped = reader.getSkipped();
                    String header = skipped.get(skipped.size() - 1);
                    headerFieldList = createFieldListFromHeader(header);
                }
            }

            List<FieldList> candidates = getCandidateFieldLists(tokens.length);
            if (candidates.size() == 1){
                match = candidates.get(0);
            }
            else if (candidates.size() > 1){
                throw new SchemaException(
                        "Ambiguous schema [" + schema.getName() + "]. "
                        + "Multiple field lists in the schema support records with "
                        + tokens.length + " fields.");
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


    protected String escapeFieldName(String original){
        return original.replaceAll("[.+]","_");
    }

    protected FieldList createFieldListFromHeader(String header){
        try{
            String[] tokens = parse(header);
            FieldList result = new FieldList();
            for (int x = 0; x < tokens.length; x++){
                String fieldName = escapeFieldName(tokens[x]);
                if (fieldName.isEmpty()){
                    fieldName = "FIELD" + (x + 1);
                }
                Field field = new Field(fieldName);
                field.addOperation(new RegexValidator(tokens[x]));
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
            throw new IllegalArgumentException("prime is null");
        }

        if (fields.size() != data.length){
            throw new ValidationException.Builder()
                    .message(String.format(
                            "Unable to match a FieldList for a record with [%s] fields. ",
                            data.length))
                    .severity(Severity.HIGH)
                    .build();
        }

        return recordFactory.build(fields, data);
    }

    public char getDelimiter(){
        char delimiter;
        String type = format.get("type");
        if (type.equalsIgnoreCase("TAB")){
            delimiter = '\t';
            if (!format.get("delimiter").equals("\t")){
                throw new IllegalArgumentException(
                        "invalid type [" + type + "] with delimiter [" +
                                format.get("delimiter") + "]. " +
                                "You might want to set the type to 'DELIMITED'");
            }
        }
        else if (type.equalsIgnoreCase("CSV")){
            delimiter = ',';
            String d = format.get("delimiter");
            //the default delimiter is tab - for backwards compatibility,
            //don't override the delimiter if type = CSV
            //allow type = CSV and a non-comma delimiter
            if (d.length() == 1 && d.charAt(0) != '\t'){
                delimiter = d.charAt(0);
            }
            else if (d.length() != 1){
                throw new IllegalArgumentException("invalid type [" + type + "] with delimiter [" + d + "]. You might want to set the type to 'DELIMITED'");
            }
        }
        else if (type.equalsIgnoreCase("DELIM") || type.equalsIgnoreCase("DELIMITED")){
            String d = format.get("delimiter");
            if (d.length() != 1){
                throw new IllegalArgumentException("invalid type [" + type + "] with delimiter [" + d + "].  Only a single character delimiter is supported.");
            }
            delimiter = d.charAt(0);
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

        char escape = getAttributeAsChar("escapeCharacter");
        if (escape == '"'){
            //the CSVParser will handle escaped double-quotes properly without specifying this as the escape character.
            escape = CSVParser.DEFAULT_ESCAPE_CHARACTER;
        }
        return escape;
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
