package com.realcomp.prime.util;

import com.realcomp.prime.conversion.ConversionException;
import com.realcomp.prime.record.io.IOContext;
import com.realcomp.prime.record.io.IOContextBuilder;
import com.realcomp.prime.record.io.RecordReader;
import com.realcomp.prime.record.io.RecordReaderFactory;
import com.realcomp.prime.schema.SchemaException;
import com.realcomp.prime.schema.SchemaFactory;
import com.realcomp.prime.validation.ValidationException;
import java.io.*;
import java.util.Arrays;
import java.util.logging.Logger;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

/**
 * Validates a file against a schema
 *
 */
public class Validate{

    private static final Logger logger = Logger.getLogger(Validate.class.getName());
    private boolean progress = false;

    public void validate(IOContext context)
            throws SchemaException, IOException, ValidationException, ConversionException{

        RecordReader reader = RecordReaderFactory.build(context.getSchema());
        reader.open(context);

        long lineNumber = 1;
        try{
            while (reader.read() != null){
                lineNumber++;
                
                if (progress && lineNumber % 10000 == 0){
                    System.out.println("" + lineNumber);
                }
            }
        }
        catch (IOException ex){
            throw new IOException(ex.getMessage() + " at record " + lineNumber, ex);
        }
        catch (ValidationException ex){
            throw new ValidationException(ex.getMessage() + " at record " + lineNumber, ex);
        }
        catch (ConversionException ex){
            throw new ConversionException(ex.getMessage() + " at record " + lineNumber, ex);
        }
    }

    public boolean isProgress(){
        return progress;
    }

    public void setProgress(boolean progress){
        this.progress = progress;
    }
    
    

    private static void printHelp(OptionParser parser){
        try{
            parser.printHelpOn(System.err);
        }
        catch (IOException ignored){
        }
    }

    public static void main(String[] args){

        OptionParser parser = new OptionParser(){
            {
                accepts("is", "input schema")
                        .withRequiredArg().describedAs("schema").required();
                accepts("in", "input file (default: STDIN)").withRequiredArg().describedAs("file");
                acceptsAll(Arrays.asList("p", "progress"), "show progress");                
                acceptsAll(Arrays.asList("h", "?", "help"), "help");
            }
        };

        int result = 1;

        try{
            OptionSet options = parser.parse(args);
            if (options.has("?")){
                printHelp(parser);
            }
            else{                        
                Validate validator = new Validate();
                validator.setProgress(options.has("p"));
                IOContextBuilder inputBuilder = new IOContextBuilder();
                inputBuilder.schema(
                        SchemaFactory.buildSchema(new FileInputStream((String) options.valueOf("is"))));
                inputBuilder.in(
                        options.has("in")
                        ? new BufferedInputStream(new FileInputStream((String) options.valueOf("in")))
                        : new BufferedInputStream(System.in));
                validator.validate(inputBuilder.build());
                result = 0;
            }
        }
        catch (SchemaException | ConversionException | ValidationException | IOException ex){
            logger.severe(ex.getMessage());
        }
        catch (OptionException ex){
            logger.severe(ex.getMessage());
            printHelp(parser);
        }

        System.exit(result);
    }
}
