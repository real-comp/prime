package com.realcomp.data.record.reader;

import au.com.bytecode.opencsv.CSVParser;
import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.record.Record;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.schema.SchemaField;
import com.realcomp.data.validation.ValidationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import org.apache.commons.io.IOUtils;

/**
 *
 * Don't think this handles "\r\n" within a field.
 * 
 * @author krenfro
 */
public class DelimitedFileReader extends BaseFileReader{

    public static final Delimiter DEFAULT_TYPE=Delimiter.TAB;

    protected Delimiter delimiter = Delimiter.TAB;
    protected BufferedReader reader;
    protected CSVParser parser;
    protected boolean leadingRecordsSkipped = false;
    
    public DelimitedFileReader(){
    }

    @Override
    public void open(InputStream in){

        close();
        leadingRecordsSkipped = false;
        super.open(in);
        reader = new BufferedReader(new InputStreamReader(in));
        switch(delimiter){
            case TAB:
                parser = new CSVParser('\t', '\u0000');
                break;
            case CSV:
                parser = new CSVParser();
                break;
        }
    }

    @Override
    public void close(){
        if (reader != null)
            IOUtils.closeQuietly(reader);
        super.close();
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

        if (super.getSkipTrailing() > 0)
            throw new IllegalStateException("skipTrailing is not yet supported by this reader.");

        if (!leadingRecordsSkipped){
            executeBeforeFirstOperations();
            for (int x = 0; x < super.getSkipLeading(); x++)
                reader.readLine();
            leadingRecordsSkipped = true;
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
