package com.realcomp.data.util;

import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.record.Record;
import com.realcomp.data.record.parser.RecordParser;
import com.realcomp.data.schema.FileSchema;
import com.realcomp.data.schema.SchemaException;
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

    private XStream xstream;
    private FileSchema schema;
    
    public ValidateFile(){

        xstream = new XStreamFactory().build();        
    }


    public void setSchema(InputStream in) throws IOException{

        schema = (FileSchema) xstream.fromXML(in);
    }

    public void validate(InputStream in) throws 
            IOException, ConversionException, SchemaException, ValidationException{

        RecordParser parser = schema.getParser();
        parser.open(in);
        Record record = parser.next();

        while (record != null){
            record = parser.next();
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
