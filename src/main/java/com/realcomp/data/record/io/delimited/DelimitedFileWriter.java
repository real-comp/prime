package com.realcomp.data.record.io.delimited;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVWriter;
import com.realcomp.data.DataType;
import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.record.Record;
import com.realcomp.data.record.io.BaseRecordWriter;
import com.realcomp.data.schema.Field;
import com.realcomp.data.schema.FieldList;
import com.realcomp.data.schema.FileSchema;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.validation.ValidationException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author krenfro
 */
public class DelimitedFileWriter extends BaseRecordWriter{

    protected char delimiter = '\t';
    protected char quoteCharacter = CSVParser.DEFAULT_QUOTE_CHARACTER;
    protected char escapeCharacter = CSVParser.DEFAULT_ESCAPE_CHARACTER;
    protected boolean strictQuotes = CSVParser.DEFAULT_STRICT_QUOTES;
    
    protected CSVWriter writer;
    protected List<String> current;
    protected boolean header = false;
    
    public DelimitedFileWriter(){    
       current = new ArrayList<String>();
    }
    
    @Override
    protected void write(Record record, Field field)
            throws ValidationException, ConversionException, IOException{

        context.setRecord(record);
        context.setKey(field.getName());
        List<Object> values = surgeon.operate(field.getOperations(), context);
      
        if (!values.isEmpty())        
            current.add((String) DataType.STRING.coerce(values.get(0)));
    }
    
    
    @Override
    public void write(Record record)
            throws IOException, ValidationException, ConversionException, SchemaException{

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
    
    
    
    @Override
    public void open(OutputStream out) throws IOException{

        close();
        super.open(out);

        switch(delimiter){
            case '\t':
                writer = new CSVWriter(new BufferedWriter(new OutputStreamWriter(out, charset)), '\t', '\u0000');
                break;
            default:
                writer = new CSVWriter(new BufferedWriter(new OutputStreamWriter(out, charset)), delimiter, quoteCharacter, escapeCharacter);
                break;
        }
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
             for (FieldList fields : headerSchema.getFieldLists()){
                for (Field field: fields)
                    field.clearOperations();
            }
             
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
        for(Field field: schema.getDefaultFieldList())
            retVal.put(field.getName(), field.getName());
        return retVal;
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

    public boolean isHeader() {
        return header;
    }

    public void setHeader(boolean header) {
        this.header = header;
    }
    
    
    
}
