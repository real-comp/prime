package com.realcomp.prime.util;

import com.realcomp.prime.DataType;
import com.realcomp.prime.conversion.ConversionException;
import com.realcomp.prime.record.Record;
import com.realcomp.prime.record.io.*;
import com.realcomp.prime.schema.*;
import com.realcomp.prime.validation.Severity;
import com.realcomp.prime.validation.ValidationException;
import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import joptsimple.*;


public class SimpleGranularity{

    private static final Logger logger = Logger.getLogger(SimpleGranularity.class.getName());

    private final Map<Object,Long> counts;
    private final String key;
    
    public SimpleGranularity(String key){
        counts = new HashMap<>();
        this.key = key;
    }
    
    public void run(IOContext in, IOContext out)
            throws SchemaException, IOException, ConversionException, ValidationException{

        RecordReader reader = RecordReaderFactory.build(in.getSchema());
        reader.open(in);
        
        Record record = getNextRecord(reader);
        while (record != null){
            
            increment(record.get(key));
            
            record = getNextRecord(reader);
        }

        reader.close();
        writeResult(out);

        
    }
    
    private void writeResult(IOContext out)
            throws SchemaException, IOException, ConversionException, ValidationException{
                
        RecordWriter writer = RecordWriterFactory.build(out.getSchema());
        writer.open(out);
        String value = "count";
        if (key.equals("count")){
            value = "total";
        }
        for (Entry<Object,Long> entry: counts.entrySet()){
            Record record = new Record();
            record.put(key, entry.getKey());
            record.put(value, entry.getValue());
            writer.write(record);
        }            
 
        writer.close();
    }
    
    private void increment(Object key){
        if (key == null){
            key = "null";
        }
        Long count = counts.get(key);
        if (count == null){
            count = 1L;
        }
        else{
            count += 1;
        }
        counts.put(key, count);
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

    private static Schema buildOutputSchema(Schema inputSchema, String field) throws SchemaException{
        
        Schema outputSchema = new Schema(inputSchema);
        FieldList fieldList = outputSchema.getDefaultFieldList();
        if (fieldList == null){
            fieldList = outputSchema.getFieldLists().get(0);
        }
        
        for (FieldList fields: inputSchema.getFieldLists()){
            outputSchema.removeFieldList(fields);
        }
        
        FieldList outputFieldList = new FieldList();        
        outputFieldList.add(new Field(fieldList.get(field)));
        
        
        String valueField = "count";
        if (field.equals("count")){
            valueField = "total";
        }
        
        outputFieldList.add(new Field(valueField, DataType.LONG));
        outputSchema.addFieldList(outputFieldList);
        
        return outputSchema;
    }
    
    
    public static void main(String[] args){

        OptionParser parser = new OptionParser(){
            {
                acceptsAll(Arrays.asList("is", "input-schema"), "input schema")
                        .withRequiredArg().describedAs("schema").required();

                acceptsAll(Arrays.asList("f", "field"), "field name from input schema")
                        .withRequiredArg().describedAs("field").required();

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
                String field = (String) options.valueOf("f");
                SimpleGranularity gran = new SimpleGranularity(field);
                IOContextBuilder inputBuilder = new IOContextBuilder();
                Schema inputSchema = SchemaFactory.buildSchema(new FileInputStream((String) options.valueOf("is")));
                inputBuilder.schema(inputSchema);
                inputBuilder.in(
                        options.has("in")
                        ? new BufferedInputStream(new FileInputStream((String) options.valueOf("in")))
                        : new BufferedInputStream(System.in));
                inputBuilder.validationExceptionThreshold(Severity.MEDIUM);

                IOContextBuilder outputBuilder = new IOContextBuilder();
                Schema outputSchema = buildOutputSchema(inputSchema, field);
                outputBuilder.schema(outputSchema);
                outputBuilder.out(
                        options.has("out")
                        ? new BufferedOutputStream(new FileOutputStream((String) options.valueOf("out")))
                        : new BufferedOutputStream(System.out));
                outputBuilder.validationExceptionThreshold(Severity.MEDIUM);
                gran.run(inputBuilder.build(), outputBuilder.build());
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
