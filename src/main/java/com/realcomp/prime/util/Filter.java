package com.realcomp.prime.util;

import com.realcomp.prime.conversion.ConversionException;
import com.realcomp.prime.record.Record;
import com.realcomp.prime.record.io.*;
import com.realcomp.prime.schema.SchemaException;
import com.realcomp.prime.schema.SchemaFactory;
import com.realcomp.prime.validation.Severity;
import com.realcomp.prime.validation.ValidationException;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.*;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * WARNING: This class does more than filter, it filters and runs the conversions!
 *
 */
@Deprecated
public class Filter{

    private static final Logger logger = Logger.getLogger(Filter.class.getName());

    public void filter(IOContext in, IOContext out)
            throws SchemaException, IOException, ConversionException, ValidationException{

        RecordReader reader = RecordReaderFactory.build(in.getSchema());
        reader.open(in);
        RecordWriter writer = RecordWriterFactory.build(out.getSchema());
        writer.open(out);

        Record record = getNextRecord(reader);
        while (record != null){
            try{
                writer.write(record);
            }
            catch (ValidationException ex){
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

        Record record = null;
        boolean done = false;
        while (!done && record == null){
            try{
                record = reader.read();
                if (record == null){
                    done = true;
                }
            }
            catch (ValidationException ex){
                logger.log(Level.INFO,
                           "filtered input record because: {0}",
                           new Object[]{ex.getMessage()});

                record = null;
            }
        }
        return record;
    }

    private static void printHelp(OptionParser parser){
        try{
            parser.printHelpOn(System.err);
        }
        catch (IOException ignored){
        }
    }

    public static void main(String[] args){

        logger.severe("This class is Deprecated.  Use the Reformat tool with the --filter option");


        OptionParser parser = new OptionParser(){
            {
                acceptsAll(Arrays.asList("is", "input-schema"), "input schema")
                        .withRequiredArg().describedAs("schema").required();

                acceptsAll(Arrays.asList("os", "output-schema"), "output schema")
                        .withRequiredArg().describedAs("schema").required();

                accepts("in", "input file (default: STDIN)").withRequiredArg().describedAs("file");
                accepts("out", "output file (default: STDOUT)").withRequiredArg().describedAs("file");
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
                Filter filterer = new Filter();
                IOContextBuilder inputBuilder = new IOContextBuilder();
                inputBuilder.schema(
                        SchemaFactory.buildSchema(new FileInputStream((String) options.valueOf("is"))));
                inputBuilder.in(
                        options.has("in")
                        ? new BufferedInputStream(new FileInputStream((String) options.valueOf("in")))
                        : new BufferedInputStream(System.in));
                inputBuilder.validationExceptionThreshold(Severity.MEDIUM);

                IOContextBuilder outputBuilder = new IOContextBuilder();
                outputBuilder.schema(
                        SchemaFactory.buildSchema(new FileInputStream((String) options.valueOf("os"))));
                outputBuilder.out(
                        options.has("out")
                        ? new BufferedOutputStream(new FileOutputStream((String) options.valueOf("out")))
                        : new BufferedOutputStream(System.out));
                outputBuilder.validationExceptionThreshold(Severity.MEDIUM);
                filterer.filter(inputBuilder.build(), outputBuilder.build());
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
