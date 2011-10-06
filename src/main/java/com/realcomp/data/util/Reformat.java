package com.realcomp.data.util;

import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.record.Record;
import com.realcomp.data.record.reader.RecordReader;
import com.realcomp.data.record.writer.RecordWriter;
import com.realcomp.data.schema.FileSchema;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.schema.SchemaFactory;
import com.realcomp.data.schema.Field;
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
public class Reformat {

    private static final Logger logger =  Logger.getLogger(Reformat.class.getName());


    private FileSchema inputSchema;
    private FileSchema outputSchema;

    public Reformat(){
    }


    public void reformat(InputStream in, OutputStream out) throws SchemaException, IOException, ValidationException, ConversionException {

        RecordReader reader = inputSchema.getReader();
        reader.open(in);
        RecordWriter writer = outputSchema.getWriter();
        writer.open(out);
        Record record = reader.read();
        while (record != null){
            writer.write(record);
            record = reader.read();
        }

        writer.close();
        reader.close();
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
            System.err.println("  Reformat <input schema> <output schema> [<in file> <out file>]");
            System.exit(1);
        }

        if (args.length == 3 ) {
            System.err.println("Both input AND output files must be specified.");
            System.err.println("Usage: ");
            System.err.println("  Reformat <input schema> <output schema> [<in file> <out file>]");
            System.exit(1);
        }

        try {
            Reformat reformatter = new Reformat();
            reformatter.setInputSchema(new FileInputStream(args[0]));
            reformatter.setOutputSchema(new FileInputStream(args[1]));
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

            reformatter.reformat(in, out);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            logger.log(Level.SEVERE, ex.getMessage());
            System.exit(1);
        }
    }
}
