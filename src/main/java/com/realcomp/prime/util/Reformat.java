package com.realcomp.prime.util;

import com.realcomp.prime.conversion.ConversionException;
import com.realcomp.prime.record.Record;
import com.realcomp.prime.record.io.*;
import com.realcomp.prime.schema.Schema;
import com.realcomp.prime.schema.SchemaException;
import com.realcomp.prime.schema.SchemaFactory;
import com.realcomp.prime.transform.TransformContext;
import com.realcomp.prime.transform.Transformer;
import com.realcomp.prime.validation.Severity;
import com.realcomp.prime.validation.ValidationException;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reformat, optionally filtering,  a file using an input and output schema
 * Filtering can happen because of validation problems against the input schema AND the output schema.
 * You can optionally write filtered records to a file.  The optional filter file will conform to the input schema,
 * and contain both records that failed on input, and those that failed on output.
 *
 */
public class Reformat{

    private static final Logger logger = Logger.getLogger(Reformat.class.getName());

    private List<Transformer> transformers;
    private Map<String, String> constants;
    private boolean filter = false;

    private RecordReader in;
    private RecordWriter out;
    private RecordWriter error;

    public Reformat(){
        transformers = new ArrayList<>();
        constants = new HashMap<>();
    }

    public void setIn(IOContext inputCtx) throws FormatException, SchemaException, IOException{
        if (inputCtx == null){
            throw new IllegalArgumentException("inputCtx is null");
        }

        in = RecordReaderFactory.build(inputCtx.getSchema());
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
        filter = true;
    }


    public void reformat()
            throws SchemaException, IOException, ValidationException, ConversionException{

        TransformContext ctx = new TransformContext();
        ctx.setValidationExceptionThreshold(in.getIOContext().getValidationExeptionThreshold());

        Record record = getNextRecord(in);

        while (record != null){
            ctx.setRecord(record);

            for (Entry<String, String> constant : constants.entrySet()){
                record.put(constant.getKey(), constant.getValue());
            }

            try{
                for (Transformer t : transformers){
                    t.transform(ctx);
                }

                out.write(ctx.getRecord());
            }
            catch (ValidationException ex){
                if (filter){
                    logger.log(Level.INFO,
                           "filtered output: {0} : {1}",
                           new Object[]{out.getIOContext().getSchema().classify(record).toString(record), ex.getMessage()});
                    if (error != null && ex.getRecord().isPresent()){
                        error.write(record);
                    }
                }
                else{
                    throw ex;
                }
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
                if (filter){
                    logger.log(Level.INFO, "filtered input: {0}", new Object[]{ex.getMessage()});
                    if (error != null && ex.getRecord().isPresent()){
                        error.write(ex.getRecord().get());
                    }
                    record = null;
                }
                else{
                    throw ex;
                }
            }
        }
        return record;
    }


    public void addTransformer(String file) throws FileNotFoundException{
        Transformer t = SchemaFactory.buildTransformer(new FileInputStream(file));
        transformers.add(t);
    }
    
    public void addTransformer(Transformer transformer){
        Objects.requireNonNull(transformer);
        transformers.add(transformer);
    }

    public void addConstantValue(String name, String value){
        constants.put(name, value);
    }

    private static void printHelp(OptionParser parser){
        try{
            parser.printHelpOn(System.err);
        }
        catch (IOException ignored){
        }
    }

    public boolean isFilter(){
        return filter;
    }

    public void setFilter(boolean filter){
        this.filter = filter;
    }


    public static void main(String[] args){

        OptionParser parser = new OptionParser(){
            {
                acceptsAll(Arrays.asList("is", "input-schema"), "input schema")
                        .withRequiredArg().describedAs("schema").required();

                acceptsAll(Arrays.asList("os", "output-schema"), "output schema")
                        .withRequiredArg().describedAs("schema or 'json'").required();

                accepts("in", "input file (default: STDIN)").withRequiredArg().describedAs("file");
                accepts("out", "output file (default: STDOUT)").withRequiredArg().describedAs("file");
                acceptsAll(Arrays.asList("t", "transform"), "transform schema(s)").withRequiredArg().describedAs("transform");
                acceptsAll(Arrays.asList("f", "filter"), "filter invalid records").withOptionalArg().describedAs("file");
                accepts("c").withOptionalArg().describedAs("constant(s) set in every Record (i.e., orderId:4844)");
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
                Reformat reformatter = new Reformat();
                IOContextBuilder inputBuilder = new IOContextBuilder();
                Schema inputSchema =
                        SchemaFactory.buildSchema(new FileInputStream((String) options.valueOf("is")));
                inputBuilder.schema(inputSchema);
                inputBuilder.in(
                        options.has("in")
                        ? new BufferedInputStream(new FileInputStream((String) options.valueOf("in")))
                        : new BufferedInputStream(System.in));


                Schema outputSchema = null;
                if (options.valueOf("os").equals("json")){
                    outputSchema = SchemaFactory.buildSchema(new FileInputStream((String) options.valueOf("is")));
                    Map<String,String> format = new HashMap<>();
                    format.put("type", "json");
                    outputSchema.setFormat(format);
                }
                else{
                    outputSchema = SchemaFactory.buildSchema(new FileInputStream((String) options.valueOf("os")));
                }

                IOContextBuilder outputBuilder = new IOContextBuilder();
                outputBuilder.schema(outputSchema);
                outputBuilder.out(
                        options.has("out")
                        ? new BufferedOutputStream(new FileOutputStream((String) options.valueOf("out")))
                        : new BufferedOutputStream(System.out));



                if (options.has("f")){
                    reformatter.setFilter(true);
                    inputBuilder.validationExceptionThreshold(Severity.MEDIUM);
                    outputBuilder.validationExceptionThreshold(Severity.MEDIUM);

                    String errorFile = (String) options.valueOf("f");
                    if (errorFile != null && !errorFile.isEmpty()){
                        IOContext errContext = new IOContextBuilder()
                            .schema(new Schema(inputSchema))
                            .out(new FileOutputStream(errorFile))
                            .validationExceptionThreshold(Severity.LOW)
                            .build();
                        reformatter.setErr(errContext);
                    }
                }

                for (String constant : (List<String>) options.valuesOf("c")){
                    int pos = constant.indexOf(":");
                    if (pos > 0){
                        reformatter.addConstantValue(constant.substring(0, pos), constant.substring(pos + 1));
                    }
                }


                for (Object t : options.valuesOf("t")){
                    reformatter.addTransformer(t.toString());
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
