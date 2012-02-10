package com.realcomp.data.util;

import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.record.Record;
import com.realcomp.data.record.io.*;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.schema.SchemaFactory;
import com.realcomp.data.validation.ValidationException;
import java.io.*;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

/**
 * WARNING: This class does more than filter, it filters and runs the conversions!
 * 
 * 
 * @author krenfro
 */
public class Filter {

    private static final Logger logger =  Logger.getLogger(Filter.class.getName());


    public void filter(IOContext in, IOContext out) 
            throws SchemaException, IOException, ConversionException, ValidationException {

        RecordReader reader = RecordReaderFactory.build(in.getSchema());
        reader.open(in);
        
        RecordWriter writer = RecordWriterFactory.build(out.getSchema());
        writer.open(out);
        
        Record record = getNextRecord(reader);
        while (record != null){
            try{
                writer.write(record);
            }
            catch(ValidationException ex){
                logger.log(Level.INFO, 
                           "filtered: {0} because: {1}", 
                           new Object[]{out.getSchema().classify(record).toString(record), ex.getMessage()});
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
    
    private static void printHelp(OptionParser parser){
        try {
            parser.printHelpOn(System.err);
        }
        catch (IOException ignored) {
        }
    }
    

    public static void main(String[] args){

        OptionParser parser = new OptionParser(){{
            acceptsAll(Arrays.asList("is","input-schema"), "input schema" )
                    .withRequiredArg().describedAs("schema").required();
            
            acceptsAll(Arrays.asList("os", "output-schema"), "output schema" )
                    .withRequiredArg().describedAs("schema").required();
            
            accepts("in", "input file (default: STDIN)").withRequiredArg().describedAs("file");
            accepts("out", "output file (default: STDOUT)").withRequiredArg().describedAs("file");
            acceptsAll(Arrays.asList("h", "?", "help"), "help");
        }};
        
        int result = 1;
        
        try{
            OptionSet options = parser.parse(args);
            if (options.has("?")){
                printHelp(parser);
            }
            else{
                Filter filterer = new Filter();       
                IOContext.Builder inputBuilder = new IOContext.Builder();                
                inputBuilder.schema(
                        SchemaFactory.buildSchema(new FileInputStream((String) options.valueOf("is"))));
                inputBuilder.in(
                        options.has("in") ? 
                            new BufferedInputStream(new FileInputStream((String) options.valueOf("in"))) : 
                            new BufferedInputStream(System.in));
                
                IOContext.Builder outputBuilder = new IOContext.Builder();                
                outputBuilder.schema(
                        SchemaFactory.buildSchema(new FileInputStream((String) options.valueOf("os"))));
                outputBuilder.out(
                        options.has("out") ? 
                            new BufferedOutputStream(new FileOutputStream((String) options.valueOf("out"))) :
                            new BufferedOutputStream(System.out));
                
                filterer.filter(inputBuilder.build(), outputBuilder.build());
                result = 0;
            }
        }
        catch (SchemaException ex){
            System.err.println(ex.getMessage());
        }
        catch (ConversionException ex){
            System.err.println(ex.getMessage());
        }
        catch (ValidationException ex){
            System.err.println(ex.getMessage());
        }
        catch (FileNotFoundException ex){
            System.err.println(ex.getMessage());
        }
        catch (IOException ex){
            System.err.println(ex.getMessage());
        }
        catch (OptionException ex){
            System.err.println(ex.getMessage());
            printHelp(parser);
        }
        
        System.exit(result);
    }
}
