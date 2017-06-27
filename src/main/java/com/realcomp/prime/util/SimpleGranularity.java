package com.realcomp.prime.util;

import com.realcomp.prime.DataType;
import com.realcomp.prime.conversion.ConversionException;
import com.realcomp.prime.record.Record;
import com.realcomp.prime.record.io.*;
import com.realcomp.prime.schema.*;
import com.realcomp.prime.validation.Severity;
import com.realcomp.prime.validation.ValidationException;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SimpleGranularity{

    private static final Logger logger = Logger.getLogger(SimpleGranularity.class.getName());

    private final Map<String, Entry> counts;
    private final String fieldName;
    private final Optional<String> key;
    private final OutputStream out;

    public SimpleGranularity(Schema inputSchema, String fieldName, OutputStream out){
        Objects.requireNonNull(inputSchema);
        Objects.requireNonNull(fieldName);
        Objects.requireNonNull(out);
        counts = new HashMap<>();
        this.fieldName = fieldName;
        key = getKeyFieldName(inputSchema);
        this.out = out;
    }

    /**
     * @param schema
     * @return the name of the first field marked as a 'key' in the provided Schema
     */
    private Optional<String> getKeyFieldName(Schema schema){
        FieldList fieldList = schema.getDefaultFieldList();
        if (fieldList == null){
            fieldList = schema.getFieldLists().get(0);
        }
        for (Field field: fieldList){
            if (field.isKey()){
                return Optional.of(field.getName());
            }
        }

        return Optional.empty();
    }

    private class Entry implements Comparable<Entry>{
        private String value;
        private long count;
        private String key;

        public Entry(String value){
            this.value = value;
            count = 1;
        }


        public String getValue(){
            return value;
        }

        public void setValue(String value){
            this.value = value;
        }

        public long getCount(){
            return count;
        }

        public void setCount(long count){
            this.count = count;
        }

        public String getKey(){
            return key;
        }

        public void setKey(String key){
            this.key = key;
        }

        public void inc(){
            count++;
        }

        @Override
        public int compareTo(Entry other){
            return Long.compare(count, other.count);
        }
    }

    public void add(Record record){
        String value = record.get(fieldName, "null").toString();
        Entry entry = counts.get(value);
        if (entry == null){
            entry = new Entry(value);
            if (key.isPresent()){
                entry.setKey(record.get(key.get(), "null").toString());
            }
            counts.put(value, entry);
        }
        else{
            entry.inc();
        }
    }


    private void writeResult()
            throws SchemaException, IOException, ConversionException, ValidationException{

        Schema outputSchema = buildOutputSchema();
        try(RecordWriter writer = RecordWriterFactory.build(outputSchema);
            IOContext outputCtx = new IOContextBuilder().schema(outputSchema).out(out).build()){

            writer.open(outputCtx);
            List<Entry> entries = new ArrayList<>();
            entries.addAll(counts.values());
            Collections.sort(entries);

            for (Entry entry : entries){
                Record record = new Record();
                record.put(fieldName, entry.getValue());
                record.put("count", entry.getCount());
                record.put("key", entry.getKey());
                writer.write(record);
            }
        }
    }

    public void run(IOContext in)
            throws SchemaException, IOException, ConversionException, ValidationException{

        RecordReader reader = RecordReaderFactory.build(in.getSchema());
        reader.open(in);
        Record record = getNextRecord(reader);
        while (record != null){
            add(record);
            record = getNextRecord(reader);
        }
        reader.close();
        writeResult();
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

    private Schema buildOutputSchema() throws SchemaException{

        Schema outputSchema = new Schema();
        Format format = new Format();
        format.put("type", "TAB");
        format.put("header", "true");
        outputSchema.setFormat(format);

        FieldList fieldList = new FieldList();
        Field field = new Field(fieldName);
        field.setType(DataType.STRING);
        fieldList.add(field);

        field = new Field("count");
        field.setType(DataType.LONG);
        fieldList.add(field);

        if (key.isPresent()){
            field = new Field("key");
            field.setType(DataType.STRING);
            fieldList.add(field);
        }

        outputSchema.addFieldList(fieldList);
        return outputSchema;
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
                OutputStream out = options.has("out")
                        ? new BufferedOutputStream(new FileOutputStream((String) options.valueOf("out")))
                        : new BufferedOutputStream(System.out);

                IOContextBuilder inputBuilder = new IOContextBuilder();

                Schema inputSchema;
                if (options.valueOf("is").equals("json")){
                    inputSchema = new Schema();
                    Map<String,String> format = new HashMap<>();
                    format.put("type", "json");
                    inputSchema.setFormat(format);
                    FieldList fields = new FieldList();
                    fields.add(new Field(field));
                    inputSchema.addFieldList(fields);
                }
                else{
                    inputSchema = SchemaFactory.buildSchema(new FileInputStream((String) options.valueOf("is")));
                }

                inputBuilder.schema(inputSchema);
                inputBuilder.in(
                        options.has("in")
                                ? new BufferedInputStream(new FileInputStream((String) options.valueOf("in")))
                                : new BufferedInputStream(System.in));
                inputBuilder.validationExceptionThreshold(Severity.MEDIUM);
                SimpleGranularity gran = new SimpleGranularity(inputSchema, field, out);
                gran.run(inputBuilder.build());
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
