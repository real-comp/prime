package com.realcomp.data.record.writer;

import au.com.bytecode.opencsv.CSVWriter;
import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.record.Record;
import com.realcomp.data.record.io.Delimiter;
import com.realcomp.data.schema.Classifier;
import com.realcomp.data.schema.FileSchema;
import com.realcomp.data.schema.SchemaException;
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
    protected CSVWriter writer;
    protected List<String> current;
    protected boolean header = false;
    
    public DelimitedFileWriter(){
        current = new ArrayList<String>();
    }
    
    public DelimitedFileWriter(DelimitedFileWriter copy){
        current = new ArrayList<String>();
        delimiter = copy.delimiter;
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
            if (writer != null){
                writer.close();
            }
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

        //optionally write header record
        if (!beforeFirstOperationsRun && header){
            current.clear();
            writeHeader();
        }

        current.clear();
        super.write(record);
        writer.writeNext(current.toArray(new String[current.size()]));
        writer.flush();
    }

    /**
     * Write a header record, constructed from a Record.
     * 
     * @param record
     * @throws IOException
     * @throws ValidationException
     * @throws ConversionException
     */
    protected void writeHeader() throws IOException, ValidationException, ConversionException{

        //No operations should be run on the Record, so a temporary schema
        // is created with no operations.
        try {
            FileSchema originalSchema = getSchema();
            FileSchema headerSchema = new FileSchema(getSchema());
            for (SchemaField f: headerSchema.getFields()){
                f.clearOperations();
            }
            for (Classifier c: headerSchema.getClassifiers())
                for (SchemaField f: c.getFields())
                    f.clearOperations();

            setSchema(headerSchema);
            super.write(getHeader());
            writer.writeNext(current.toArray(new String[current.size()]));
            writer.flush();
            setSchema(originalSchema); //put back the original schema
        }
        catch (SchemaException ex) {
            throw new IOException("Unable to create temporary header schema: " + ex.getMessage());
        }
    }

    protected Record getHeader(){
        Record retVal = new Record();
        for(SchemaField field: schema.getFields())
            retVal.put(field.getName(), field.getName());
        return retVal;
    }

    @Override
    protected void write(Record record, SchemaField field)
            throws ValidationException, ConversionException, IOException{

        current.add(toString(record, field));
    }

    public boolean isHeader() {
        return header;
    }

    public void setHeader(boolean header) {
        this.header = header;
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
        if (this.header != other.header)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + (this.delimiter != null ? this.delimiter.hashCode() : 0);
        hash = 47 * hash + (this.header ? 1 : 0);
        return hash;
    }
}
