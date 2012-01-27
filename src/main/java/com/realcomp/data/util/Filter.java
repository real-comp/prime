package com.realcomp.data.util;

import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.record.Record;
import com.realcomp.data.record.io.RecordReader;
import com.realcomp.data.record.io.RecordReaderFactory;
import com.realcomp.data.record.io.RecordWriter;
import com.realcomp.data.record.io.RecordWriterFactory;
import com.realcomp.data.schema.FileSchema;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.schema.SchemaFactory;
import com.realcomp.data.validation.ValidationException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * WARNING: This class does more than filter, it filters and runs the conversions!
 * 
 * 
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

        RecordReader reader = RecordReaderFactory.build(inputSchema);
        reader.open(in);
        
        RecordWriter writer = RecordWriterFactory.build(outputSchema);
        writer.open(out);
        
        Record record = getNextRecord(reader);
        while (record != null){
            try{
                writer.write(record);
            }
            catch(ValidationException ex){
                logger.log(Level.INFO, 
                           "filtered: {0} because: {1}", 
                           new Object[]{outputSchema.classify(record).toString(record), ex.getMessage()});
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
