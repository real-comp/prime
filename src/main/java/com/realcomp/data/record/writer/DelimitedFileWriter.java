package com.realcomp.data.record.writer;

import au.com.bytecode.opencsv.CSVWriter;
import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.record.Record;
import com.realcomp.data.record.reader.Delimiter;
import com.realcomp.data.schema.SchemaField;
import com.realcomp.data.validation.ValidationException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * Don't think this handles "\r\n" within a field.
 * 
 * @author krenfro
 */
public class DelimitedFileWriter extends BaseFileWriter{

    protected static final Logger logger = Logger.getLogger(DelimitedFileWriter.class.getName());
    public static final Delimiter DEFAULT_TYPE=Delimiter.TAB;

    protected Delimiter delimiter = Delimiter.TAB;
    protected BufferedWriter buff;
    protected CSVWriter writer;
    protected List<String> current;
    
    public DelimitedFileWriter(){
        current = new ArrayList<String>();
    }
    

    @Override
    public void open(OutputStream out){

        close();
        super.open(out);

        switch(delimiter){
            case TAB:
                writer = new CSVWriter(new BufferedWriter(new OutputStreamWriter(out)), '\t', '\u0000');
                break;
            case CSV:
                writer = new CSVWriter(new BufferedWriter(new OutputStreamWriter(out)));
                break;
        }
    }

    @Override
    public void close(){

        try {
            if (writer != null)
                writer.close();
        }
        catch (IOException ex) {
            logger.log(Level.WARNING, null, ex);
        }
        
        super.close();
    }


    public Delimiter getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(Delimiter delimiter) {
        if (delimiter == null)
            throw new IllegalArgumentException("delimiter is null");
        if (writer != null)
            throw new IllegalStateException("already open. unable to change delimiter.");
        this.delimiter = delimiter;
    }

    @Override
    public void write(Record record)
            throws IOException, ValidationException, ConversionException{

        current.clear();
        super.write(record);
        writer.writeNext(current.toArray(new String[current.size()]));
        writer.flush();
    }


    @Override
    protected void write(Record record, SchemaField field)
            throws ValidationException, ConversionException, IOException{

        current.add(toString(record, field));
    }
    

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final DelimitedFileWriter other = (DelimitedFileWriter) obj;
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
