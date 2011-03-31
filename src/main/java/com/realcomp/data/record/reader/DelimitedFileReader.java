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
import java.util.List;

/**
 *
 * Don't think this handles "\r\n" within a field.
 * 
 * @author krenfro
 */
public class DelimitedFileReader extends BaseFileReader{

    public static final Delimiter DEFAULT_TYPE=Delimiter.TAB;

    protected Delimiter delimiter = Delimiter.TAB;
    protected CSVParser parser;
    protected boolean beforeFirst = true;
    
    @Override
    public void open(InputStream in) throws IOException{

        super.open(in);
        beforeFirst = true;
        switch(delimiter){
            case TAB:
                parser = new CSVParser('\t', '\u0000');
                break;
            case CSV:
                parser = new CSVParser();
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final DelimitedFileReader other = (DelimitedFileReader) obj;
        if (this.delimiter != other.delimiter)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.delimiter != null ? this.delimiter.hashCode() : 0);
        return hash;
    }
    
}
