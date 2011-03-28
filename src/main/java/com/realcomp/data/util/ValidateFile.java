package com.realcomp.data.util;

import com.realcomp.data.schema.XStreamFactory;
import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.record.Record;
import com.realcomp.data.record.reader.RecordReader;
import com.realcomp.data.schema.FileSchema;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.schema.SchemaFactory;
import com.realcomp.data.validation.ValidationException;
import com.thoughtworks.xstream.XStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Validates a file against a schema
 * @author krenfro
 */
public class ValidateFile {

    private static final Logger logger =  Logger.getLogger(ValidateFile.class.getName());

    
    private FileSchema schema;
    
    public ValidateFile(){
    }


    public void setSchema(InputStream in) throws IOException{
        schema = SchemaFactory.buildFileSchema(in);
    }

    public void validate(InputStream in) throws SchemaException, IOException, ValidationException, ConversionException {

        RecordReader reader = schema.getReader();
        reader.open(in, schema);
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

        if (args.length <= 1 || args[0].equalsIgnoreCase("-h") || args[0].equals("--help")) {
            System.err.println("Usage: ");
            System.err.println("  ValidateFile <schema> [file]");
            System.exit(1);
        }

        try {
            ValidateFile validator = new ValidateFile();
            validator.setSchema(new FileInputStream(args[0]));
            InputStream in = null;
            if (args.length == 2)
                in = new FileInputStream(args[1]);
            else
                in = System.in;
            
            validator.validate(in);
        }
        catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            System.exit(1);
        }
    }
}
