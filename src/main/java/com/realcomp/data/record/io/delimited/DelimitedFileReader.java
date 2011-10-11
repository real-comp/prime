package com.realcomp.data.record.io.delimited;

import au.com.bytecode.opencsv.CSVParser;
import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.record.Record;
import com.realcomp.data.record.io.BaseRecordReader;
import com.realcomp.data.record.io.SkippingBufferedReader;
import com.realcomp.data.schema.FieldList;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.validation.Severity;
import com.realcomp.data.validation.ValidationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 *
 * @author krenfro
 */
public class DelimitedFileReader extends BaseRecordReader{

    protected SkippingBufferedReader reader;
    
    protected char delimiter = '\t';
    protected char quoteCharacter = CSVParser.DEFAULT_QUOTE_CHARACTER;
    protected char escapeCharacter = CSVParser.DEFAULT_ESCAPE_CHARACTER;
    protected boolean strictQuotes = CSVParser.DEFAULT_STRICT_QUOTES;
    protected CSVParser parser;
    
        
    @Override
    public void open(InputStream in) throws IOException{

        super.open(in);
        
        Charset c = charset == null ? Charset.defaultCharset() : Charset.forName(charset);
        reader = new SkippingBufferedReader(new InputStreamReader(in, c));
        reader.setSkipLeading(skipLeading);
        reader.setSkipTrailing(skipTrailing);
        charset = c.name();
        
        switch(delimiter){
            case '\t':
                parser = new CSVParser(delimiter, '\u0000', escapeCharacter, strictQuotes);
                break;
            default:
                parser = new CSVParser(delimiter, quoteCharacter, escapeCharacter, strictQuotes);
        }
    }
    
    
    @Override
    public Record read() throws IOException, ValidationException, ConversionException, SchemaException{

        if (schema == null)
            throw new IllegalStateException("schema not specified");

        if (!beforeFirstOperationsRun){
            executeBeforeFirstOperations();
            beforeFirstOperationsRun = true;
        }

        Record record = null;
        String data = reader.readLine();
        if (data != null){
            record = loadRecord(schema.classify(data), parser.parseLine(data));
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
    
    public char getEscapeCharacter() {
        return escapeCharacter;
    }

    public void setEscapeCharacter(char escapeCharacter) {
        this.escapeCharacter = escapeCharacter;
    }

    public char getQuoteCharacter() {
        return quoteCharacter;
    }

    public void setQuoteCharacter(char quoteCharacter) {
        this.quoteCharacter = quoteCharacter;
    }

    public boolean isStrictQuotes() {
        return strictQuotes;
    }

    public void setStrictQuotes(boolean strictQuotes) {
        this.strictQuotes = strictQuotes;
    }

    public String getDelimiter() {
        if (delimiter == '\t')
            return "TAB";
        else if (delimiter == ',')
            return "CSV";
        else
            return "" + delimiter;
    }

    public void setDelimiter(String delimiter) {
        
        if (delimiter == null)
            throw new IllegalArgumentException("delimiter is null");
        else if (delimiter.equalsIgnoreCase("TAB"))
            this.delimiter = '\t';
        else if (delimiter.equalsIgnoreCase("CSV"))
            this.delimiter = ',';
        else{
            if (delimiter.length() != 1)
                throw new IllegalArgumentException("invalid delimiter [" + delimiter + "]");
            this.delimiter = delimiter.charAt(0);
        }   
    }
    
    
    
}
