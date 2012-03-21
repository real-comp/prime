package com.realcomp.data.util;

import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.record.Record;
import com.realcomp.data.record.io.*;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.schema.SchemaFactory;
import com.realcomp.data.transform.TransformContext;
import com.realcomp.data.transform.Transformer;
import com.realcomp.data.validation.ValidationException;
import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

/**
 * Reformat a file using an input and output schema
 * @author krenfro
 */
public class Reformat {

    private List<Transformer> transformers;
    private Map<String,String> constants;

    public Reformat(){
        transformers = new ArrayList<Transformer>();
        constants = new HashMap<String,String>();
    }
    
    public void reformat(IOContext in, IOContext out) 
            throws SchemaException, IOException, ValidationException, ConversionException {

        RecordReader reader = RecordReaderFactory.build(in.getSchema());
        reader.open(in);
        
        RecordWriter writer = RecordWriterFactory.build(out.getSchema());
        writer.open(out);
                
        Record record = reader.read();
        TransformContext ctx = new TransformContext();
        ctx.setValidationExceptionThreshold(in.getValidationExeptionThreshold());
        while (record != null){
            ctx.setRecord(record);
            
            for (Entry<String,String> constant: constants.entrySet()){
                record.put(constant.getKey(), constant.getValue());
            }
            
            for (Transformer t: transformers){
                t.transform(ctx);
            }
            
            writer.write(ctx.getRecord());
            record = reader.read();
        }

        writer.close();
        reader.close();
    }
    
    public void addTransformer(String file) throws FileNotFoundException{
        Transformer t = SchemaFactory.buildTransformer(new FileInputStream(file));
        transformers.add(t);
    }

    
    public void addConstantValue(String name, String value){
        constants.put(name, value);
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
            acceptsAll(Arrays.asList("t","transform"), "transform schema(s)" ).withRequiredArg().describedAs("transform");
            accepts( "c" ).withOptionalArg().describedAs( "constant(s) set in every Record (i.e., orderId:4844)" );
            acceptsAll(Arrays.asList("h", "?", "help"), "help");
        }};
        
        int result = 1;
        
        try{
            OptionSet options = parser.parse(args);
            if (options.has("?")){
                printHelp(parser);
            }
            else{
                Reformat reformatter = new Reformat();
                IOContextBuilder inputBuilder = new IOContextBuilder();                
                inputBuilder.schema(
                        SchemaFactory.buildSchema(new FileInputStream((String) options.valueOf("is"))));
                inputBuilder.in(
                        options.has("in") ? 
                            new BufferedInputStream(new FileInputStream((String) options.valueOf("in"))) : 
                            new BufferedInputStream(System.in));
                
                IOContextBuilder outputBuilder = new IOContextBuilder();                
                outputBuilder.schema(
                        SchemaFactory.buildSchema(new FileInputStream((String) options.valueOf("os"))));
                outputBuilder.out(
                        options.has("out") ? 
                            new BufferedOutputStream(new FileOutputStream((String) options.valueOf("out"))) :
                            new BufferedOutputStream(System.out));
                
                for (Object t:  options.valuesOf("t")){
                    reformatter.addTransformer(t.toString());
                }
                
                for (String constant: (List<String>) options.valuesOf("c")){
                    int pos = constant.indexOf(":");
                    if (pos > 0){
                        reformatter.addConstantValue(constant.substring(0, pos), constant.substring(pos + 1));
                    }
                }
                
                reformatter.reformat(inputBuilder.build(), outputBuilder.build());
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
