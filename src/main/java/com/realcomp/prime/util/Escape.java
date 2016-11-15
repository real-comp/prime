package com.realcomp.prime.util;

import au.com.bytecode.opencsv.CSVParser;
import com.realcomp.prime.conversion.ConversionException;
import com.realcomp.prime.record.Record;
import com.realcomp.prime.record.io.*;
import com.realcomp.prime.record.io.delimited.DelimitedFileReader;
import com.realcomp.prime.schema.Schema;
import com.realcomp.prime.schema.SchemaException;
import com.realcomp.prime.schema.SchemaFactory;
import com.realcomp.prime.transform.TransformContext;
import com.realcomp.prime.validation.Severity;
import com.realcomp.prime.validation.ValidationException;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.*;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * Fixes unterminated escape characters in a delimited file.
 * 
 */
public class Escape{

    private static final Logger logger = Logger.getLogger(Escape.class.getName());

    private RecordReader in;
    private RecordWriter out;
    private RecordWriter error;

    public Escape(){
    }

    public void setIn(IOContext inputCtx) throws FormatException, SchemaException, IOException{
        if (inputCtx == null){
            throw new IllegalArgumentException("inputCtx is null");
        }

        in = RecordReaderFactory.build(inputCtx.getSchema());
        
        if (!(in instanceof DelimitedFileReader)){
            throw new IOException("Expected a delimited input file. Using reader of type: " + in.getClass().getName());
        }
        in.open(inputCtx);
    }

    public void setOut(IOContext outputCtx) throws FormatException, SchemaException, IOException{
        if (outputCtx == null){
            throw new IllegalArgumentException("outputCtx is null");
        }

        out = RecordWriterFactory.build(outputCtx.getSchema());
        out.open(outputCtx);
    }

    public void setErr(IOContext errorCtx) throws FormatException, SchemaException, IOException{
        if (errorCtx == null){
            throw new IllegalArgumentException("errorCtx is null");
        }

        error = RecordWriterFactory.build(errorCtx.getSchema());
        error.open(errorCtx);
    }


    public void reformat()
            throws SchemaException, IOException, ValidationException, ConversionException{

        TransformContext ctx = new TransformContext();
        ctx.setValidationExceptionThreshold(in.getIOContext().getValidationExeptionThreshold());

        Record record = getNextRecord(in);

        while (record != null){
            ctx.setRecord(record);

            try{

                out.write(ctx.getRecord());
            }
            catch (ValidationException ex){
                throw ex;
            }

            record = getNextRecord(in);
        }

        in.close();
        out.close();
        if (error != null){
            error.close();
        }
    }


    /**
     * Reads the next Record from the RecordReader.
     * Records that have Validation problems are optionally ignored, and logged.
     *
     * @param reader
     * @return
     * @throws IOException
     * @throws SchemaException
     * @throws ConversionException
     * @throws ValidationException
     */
    protected Record getNextRecord(RecordReader reader)
            throws IOException, SchemaException, ConversionException, ValidationException{

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
                throw ex;
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

        OptionParser parser = new OptionParser(){
            {
                acceptsAll(Arrays.asList("is", "input-schema"), "input schema")
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
                Escape reformatter = new Escape();
                IOContextBuilder inputBuilder = new IOContextBuilder();
                
                Schema inSchema = SchemaFactory.buildSchema(new FileInputStream((String) options.valueOf("is")));
                String escapeChar = inSchema.getFormat().get("escapeCharacter");
                
                inSchema.getFormat().put("escapeCharacter", "");
                inputBuilder.schema(inSchema);
                inputBuilder.in(
                        options.has("in")
                        ? new BufferedInputStream(new FileInputStream((String) options.valueOf("in")))
                        : new BufferedInputStream(System.in));


                IOContextBuilder outputBuilder = new IOContextBuilder();
                Schema outSchema = new Schema(inSchema);               
                if (escapeChar == null || escapeChar.isEmpty()){
                    outSchema.getFormat().put("escapeCharacter", Character.toString(CSVParser.DEFAULT_ESCAPE_CHARACTER));
                }
                else{
                    outSchema.getFormat().put("escapeCharacter", escapeChar);
                }
                
                outputBuilder.schema(outSchema);
                outputBuilder.out(
                        options.has("out")
                        ? new BufferedOutputStream(new FileOutputStream((String) options.valueOf("out")))
                        : new BufferedOutputStream(System.out));


                if (options.has("f")){
                    inputBuilder.validationExceptionThreshold(Severity.MEDIUM);
                    outputBuilder.validationExceptionThreshold(Severity.MEDIUM);

                    String errorFile = (String) options.valueOf("f");
                    if (errorFile != null && !errorFile.isEmpty()){
                        IOContextBuilder errorBuilder = new IOContextBuilder();
                        errorBuilder.schema(
                           SchemaFactory.buildSchema(new FileInputStream((String) options.valueOf("is"))));
                        errorBuilder.out(new FileOutputStream(errorFile));
                        errorBuilder.validationExceptionThreshold(Severity.LOW);
                        reformatter.setErr(errorBuilder.build());
                    }
                }
                
                reformatter.setIn(inputBuilder.build());
                reformatter.setOut(outputBuilder.build());
                reformatter.reformat();
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
