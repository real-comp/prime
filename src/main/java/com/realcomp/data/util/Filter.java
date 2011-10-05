package com.realcomp.data.util;

import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.record.Record;
import com.realcomp.data.record.reader.RecordReader;
import com.realcomp.data.record.writer.RecordWriter;
import com.realcomp.data.schema.FileSchema;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.schema.SchemaFactory;
import com.realcomp.data.schema.SchemaField;
import com.realcomp.data.validation.ValidationException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reformat a file using an input and output schema
 * @author krenfro
 */
public class Filter {

    private static final Logger logger =  Logger.getLogger(Filter.class.getName());


    private FileSchema inputSchema;
    private FileSchema outputSchema;

    public Filter(){
    }


    public void filter(InputStream in, OutputStream out) 
            throws SchemaException, IOException, ConversionException, ValidationException {

        RecordReader reader = inputSchema.getReader();
        if (reader == null)
            throw new SchemaException("No reader specified in input schema.");
        reader.open(in);
        
        RecordWriter writer = outputSchema.getWriter();        
        if (writer == null)
            throw new SchemaException("No writer specified in output schema.");
        writer.open(out);
        
        checkFields();
        Record record = getNextRecord(reader);
        while (record != null){
            try{
                writer.write(record);
            }
            catch(ValidationException ex){
                logger.log(Level.INFO, 
                           "filtered: {0} because: {1}", 
                           new Object[]{outputSchema.toString(record), ex.getMessage()});
            }
            record = getNextRecord(reader);
        }

        writer.close();
        reader.close();
    }

    protected Record getNextRecord(RecordReader reader)
            throws IOException, SchemaException, ConversionException{

        Record r = null;
        try {
            r = reader.read();
        }
        catch (ValidationException ex) {
            logger.log(Level.INFO, 
                       "filtered input record because: {0}", 
                       new Object[]{ex.getMessage()});
            
            return getNextRecord(reader);
        }
        return r;
    }

    protected void checkFields(){
        for (SchemaField f: outputSchema.getFields().get(FileSchema.DEFAULT_CLASSIFIER)){
            if (inputSchema.getField(f.getName()) == null)
                logger.log(
                        Level.WARNING,
                        "No field in the input schema with name: {0}", f.getName());
        }

    }

    public void setInputSchema(InputStream in) throws IOException{
        this.inputSchema = SchemaFactory.buildFileSchema(in);
    }

    public void setInputSchema(FileSchema schema){
        if (schema == null)
            throw new IllegalArgumentException("schema is null");
        this.inputSchema = schema;
    }

    public void setOutputSchema(InputStream in) throws IOException{
        this.outputSchema = SchemaFactory.buildFileSchema(in);
    }

    public void setOutputSchema(FileSchema schema){
        if (schema == null)
            throw new IllegalArgumentException("schema is null");
        this.outputSchema = schema;
    }


    public static void main(String[] args){

        if (args.length < 2 || args[0].equalsIgnoreCase("-h") || args[0].equals("--help")) {
            System.err.println("Usage: ");
            System.err.println("  Filter <input schema> <output schema> [<in file> <out file>]");
            System.exit(1);
        }

        if (args.length == 3 ) {
            System.err.println("Both input AND output files must be specified.");
            System.err.println("Usage: ");
            System.err.println("  Filter <input schema> <output schema> [<in file> <out file>]");
            System.exit(1);
        }

        try {
            Filter filterer = new Filter();
            filterer.setInputSchema(new FileInputStream(args[0]));
            filterer.setOutputSchema(new FileInputStream(args[1]));
            InputStream in = null;
            OutputStream out = null;
            if (args.length == 4){
                in = new FileInputStream(args[2]);
                out = new FileOutputStream(args[3]);
            }
            else{
                in = System.in;
                out = System.out;
            }

            filterer.filter(in, out);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            logger.log(Level.SEVERE, ex.getMessage());
            System.exit(1);
        }
    }
}
