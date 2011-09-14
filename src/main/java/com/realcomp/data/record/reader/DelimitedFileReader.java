package com.realcomp.data.record.reader;

import com.realcomp.data.record.io.Delimiter;
import au.com.bytecode.opencsv.CSVParser;
import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.record.Record;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.schema.SchemaField;
import com.realcomp.data.validation.ValidationException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

/**
 *
 * Don't think this handles "\r\n" within a field.
 * 
 * @author krenfro
 */
public class DelimitedFileReader extends BaseFileReader implements Cloneable{

    public static final Delimiter DEFAULT_TYPE=Delimiter.TAB;

    protected Delimiter delimiter = Delimiter.TAB;
    protected char quoteCharacter = CSVParser.DEFAULT_QUOTE_CHARACTER;
    protected char escapeCharacter = CSVParser.DEFAULT_ESCAPE_CHARACTER;
    protected boolean strictQuotes = CSVParser.DEFAULT_STRICT_QUOTES;
    protected CSVParser parser;
    protected boolean beforeFirst = true;
    
    public DelimitedFileReader(){
        super();
    }
    
    /**
     * Construct an un-opened copy of a DelimitedFileReader.
     * @param copy 
     */
    public DelimitedFileReader(DelimitedFileReader copy){
        super(copy);
        delimiter = copy.delimiter;
    }
    
    @Override
    public void open(InputStream in) throws IOException{

        open(in, Charset.forName(charset));
    }
    
    @Override
    public void open(InputStream in, Charset charset) throws IOException{

        super.open(in, charset);
        beforeFirst = true;
        switch(delimiter){
            case TAB:
                parser = new CSVParser('\t', '\u0000', escapeCharacter, strictQuotes);
                break;
            case CSV:
                parser = new CSVParser(CSVParser.DEFAULT_SEPARATOR, quoteCharacter, escapeCharacter, strictQuotes);
                break;
        }
    }
    

    
    public Delimiter getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(Delimiter delimiter) {
        if (delimiter == null)
            throw new IllegalArgumentException("delimiter is null");
        if (reader != null)
            throw new IllegalStateException("already open. unable to change delimiter.");
        this.delimiter = delimiter;
    }
    
    @Override
    public Record read() throws IOException, ValidationException, ConversionException, SchemaException{

        if (schema == null)
            throw new IllegalStateException("schema not specified");

        if (beforeFirst){
            executeBeforeFirstOperations();
            beforeFirst = false;
        }

        Record record = null;
        String data = reader.readLine();
        if (data != null){
            List<SchemaField> fields = schema.classify(data);
            record = loadRecord(fields, parser.parseLine(data));
        }

        if (record != null)
            count++;
        else
            executeAfterLastOperations();
        
        return record;
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final DelimitedFileReader other = (DelimitedFileReader) obj;
        if (this.delimiter != other.delimiter)
            return false;
        if (this.quoteCharacter != other.quoteCharacter)
            return false;
        if (this.escapeCharacter != other.escapeCharacter)
            return false;
        if (this.strictQuotes != other.strictQuotes)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (this.delimiter != null ? this.delimiter.hashCode() : 0);
        hash = 59 * hash + this.quoteCharacter;
        hash = 59 * hash + this.escapeCharacter;
        hash = 59 * hash + (this.strictQuotes ? 1 : 0);
        return hash;
    }
    
    
    
}
