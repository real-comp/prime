package com.realcomp.data.util;

import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.record.reader.RecordReader;
import com.realcomp.data.schema.FileSchema;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.schema.SchemaFactory;
import com.realcomp.data.validation.ValidationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Validates a file against a schema
 * @author krenfro
 */
public class Validate {

    private static final Logger logger =  Logger.getLogger(Validate.class.getName());


    private FileSchema schema;

    public Validate(){
    }


    public void setSchema(InputStream in) throws IOException{
        schema = SchemaFactory.buildFileSchema(in);
    }

    public void setSchema(FileSchema schema){
        if (schema == null)
            throw new IllegalArgumentException("schema is null");
        this.schema = schema;
    }

    public void validate(File file) throws SchemaException, IOException, ValidationException, ConversionException{
        validate(new FileInputStream(file));
    }

    public void validate(InputStream in) throws SchemaException, IOException, ValidationException, ConversionException {

        RecordReader reader = schema.getReader();
        reader.open(in);
        long lineNumber = 1;
        try {
            while (reader.read() != null){
                lineNumber++;
            }
        }
        catch (IOException ex) {
            throw new IOException(ex.getMessage() + " at record " + lineNumber, ex);
        }
        catch (ValidationException ex) {
            throw new ValidationException(ex.getMessage() + " at record " + lineNumber, ex);
        }
        catch (ConversionException ex) {
            throw new ConversionException(ex.getMessage() + " at record " + lineNumber, ex);
        }
    }


    public static void main(String[] args){

        if (args.length < 1 || args[0].equalsIgnoreCase("-h") || args[0].equals("--help")) {
            System.err.println("Usage: ");
            System.err.println("  Validate <schema> [file]");
            System.exit(1);
        }

        try {
            Validate validator = new Validate();
            validator.setSchema(new FileInputStream(args[0]));
            InputStream in = null;
            if (args.length == 2)
                in = new FileInputStream(args[1]);
            else
                in = System.in;

            validator.validate(in);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            logger.log(Level.SEVERE, ex.getMessage());
            System.exit(1);
        }
    }
}