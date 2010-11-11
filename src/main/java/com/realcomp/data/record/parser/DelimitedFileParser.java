package com.realcomp.data.record.parser;

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
public class DelimitedFileParser extends BaseFileParser{

    public static final Type DEFAULT_TYPE=Type.TAB;

    protected Type type = Type.TAB;
    protected BufferedReader reader;
    protected CSVParser parser;
    
    public DelimitedFileParser(){
    }

    @Override
    public void open(InputStream in){

        close();
        super.open(in);
        reader = new BufferedReader(new InputStreamReader(in));
        switch(type){
            case TAB:
                parser = new CSVParser('\t');
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

    public enum Type{
        CSV, TAB;

        public static Type parse(String type){

            if (type == null)
                throw new IllegalArgumentException("type is null");
            
            if (type.equalsIgnoreCase("tab") || type.equalsIgnoreCase("tabbed"))
                return TAB;
            else if (type.equalsIgnoreCase("csv"))
                return CSV;
            throw new IllegalArgumentException("invalid type: " + type);
        }
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        if (type == null)
            throw new IllegalArgumentException("type is null");
        if (reader != null)
            throw new IllegalStateException("already open. unable to change type.");
        this.type = type;
    }
    
    @Override
    public Record next() throws IOException, ValidationException, ConversionException, SchemaException{

        if (schema == null)
            throw new IllegalStateException("schema not specified");

        String data = reader.readLine();
        if (data != null){
            List<SchemaField> fields = schema.classify(data);
            return loadRecord(fields, parser.parseLine(data));
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final DelimitedFileParser other = (DelimitedFileParser) obj;
        if (this.type != other.type)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.type != null ? this.type.hashCode() : 0);
        return hash;
    }
    
}
