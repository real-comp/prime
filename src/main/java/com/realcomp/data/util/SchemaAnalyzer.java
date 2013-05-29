package com.realcomp.data.util;

import com.realcomp.data.schema.*;
import com.thoughtworks.xstream.XStream;
import au.com.bytecode.opencsv.CSVParser;
import com.realcomp.data.schema.xml.XStreamFactory;
import java.io.*;
import java.util.*;
import joptsimple.OptionParser;

/**
 *
 * @author BGoering
 */
public class SchemaAnalyzer{

    private static final String DEFAULT_INFILE = "C:\\temp\\test.csv";

    private File inFile = null;
    private File schemaFile = null;
    private static OptionParser parser = null;
    private CSVParser lineParser;
    private boolean hasHeader = true;
    private char inDelimiter = '\u0000';
    //private Reader reader;
    private char[] delimiters = {',', '|', '\t'};
    /*
     * private static final char COMMA_CHAR = ',';
     * private static final char PIPE_CHAR = '|';
     * private static final char TAB_CHAR = '\t';
     *
     */
    private static final char QUOTE_CHAR = '"';
    private static final String LINE_END = System.getProperty("line.separator");

    public void CreateSchema(String inFileName)
            throws FileNotFoundException, IOException{

        if (inFileName == null || inFileName.trim().isEmpty()){
            throw new IOException("No file name specified");
        }

        inFile = new File(inFileName);

        if (!inFile.exists()){
            throw new FileNotFoundException("Could not find input file: " + inFileName);
        }

        if (schemaFile == null){
            getSchemaFile(inFile);
        }

        List<String> records = getRecords(inFile, 5);
        inDelimiter = getDelimiter(records);
        String header = records.get(0);
        lineParser = new CSVParser(inDelimiter);
        String[] fields = lineParser.parseLine(header);
        Schema schema = new Schema();
        schema.setName(inFile.getName());
        Map<String, String> format = new HashMap<String, String>();

        if (inDelimiter == ','){
            format.put("type", "CSV");
        }
        else if (inDelimiter == '\t'){
            format.put("type", "TAB");
        }
        else{
            format.put("type", Character.toString(inDelimiter));
        }

        if (hasHeader){
            format.put("header", "true");
        }
        else{
            format.put("header", "false");
        }

        schema.setFormat(format);
        int fieldNum = -1;

        for (String field : fields){
            fieldNum++;

            if (hasHeader && !field.trim().isEmpty()){
                schema.addField(new Field(field));
            }
            else{
                schema.addField(new Field("Field " + fieldNum));
            }
        }

        /*
         * XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(...);
         * writer = new IndentingXMLStreamWriter(writer);
         *
         */
        XStream xstream = XStreamFactory.build(true);
        xstream.toXML(schema, new FileOutputStream(schemaFile));
    }

    private void getSchemaFile(File inFile){
        String[] fileNameParts = inFile.getName().split("\\.");

        if (fileNameParts.length > 1){
            String ext = "." + fileNameParts[fileNameParts.length - 1];
            schemaFile = new File(inFile.getPath().replace(ext, ".schema"));
        }
        else{
            schemaFile = new File(inFile.getPath() + ".schema");
        }
    }

    private List<String> getRecords(File file, int numRecords)
            throws FileNotFoundException, IOException{

        List<String> records = new ArrayList<String>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))){
            String nextLine;

            for (int i = 0; i < numRecords; i++){
                nextLine = reader.readLine();

                if (nextLine == null){
                    throw new IOException("Insufficient records: File must have at least " + numRecords + " to analyze");
                }
                records.add(nextLine);
            }
        }

        return records;
    }

    private char getDelimiter(List<String> records)
            throws IOException{

        char delimiter = '\u0000';

        for (char delim : delimiters){
            lineParser = new CSVParser(delim);
            String nextLine;
            int counter = 0;

            for (String record : records){

                if (lineParser.parseLine(record).length > 1){
                    counter++;
                }
            }

            if (counter == records.size()){
                delimiter = delim;
            }

            if (delimiter != '\u0000'){
                break;
            }
        }

        if (delimiter == '\u0000'){
            throw new IOException("Could not detect a valid delimiter");
        }

        return delimiter;
    }

    private static void getOptions(){

        parser = new OptionParser(){
            {
                accepts("in", "input file").withRequiredArg().describedAs("file");
                accepts("s", "schema").withRequiredArg().describedAs("schema");
                accepts("header");
                accepts("nh", "noheader");
                acceptsAll(Arrays.asList("h", "?", "help"), "help");
            }
        };

    }

    private static void printHelp(){
        try{
            parser.printHelpOn(System.err);
        }
        catch (IOException ignored){
        }
    }

    public static void main(String[] args)
            throws IOException{

        //getOptions();
        SchemaAnalyzer analyzer = new SchemaAnalyzer();
        analyzer.hasHeader = true;
        analyzer.CreateSchema(args[0]);
        //analyzer.CreateSchema(DEFAULT_INFILE);

        //System.out.println("Delimiter: " + analyzer.getDelimiter(new File(DEFAULT_INFILE)));

        /*
         * OptionParser parser = new OptionParser(){{
         * acceptsAll(Arrays.asList("is","input-schema"), "input schema" )
         * .withRequiredArg().describedAs("schema").required();
         *
         * acceptsAll(Arrays.asList("os", "output-schema"), "output schema" )
         * .withRequiredArg().describedAs("schema").required();
         *
         * accepts("in", "input file (default: STDIN)").withRequiredArg().describedAs("file");
         * accepts("out", "output file (default: STDOUT)").withRequiredArg().describedAs("file");
         * acceptsAll(Arrays.asList("t","transform"), "transform schema(s)"
         * ).withRequiredArg().describedAs("transform");
         * accepts( "c" ).withOptionalArg().describedAs( "constant(s) set in every Record (i.e., orderId:4844)" );
         * acceptsAll(Arrays.asList("h", "?", "help"), "help");
         * }};
         *
         */

        /*
         * int result = 1;
         *
         * try{
         * OptionSet options = parser.parse(args);
         * if (options.has("?")){
         * printHelp();
         * }
         * else{
         * Reformat reformatter = new Reformat();
         * IOContextBuilder inputBuilder = new IOContextBuilder();
         * inputBuilder.schema(
         * SchemaFactory.buildSchema(new FileInputStream((String) options.valueOf("is"))));
         * inputBuilder.in(
         * options.has("in") ?
         * new BufferedInputStream(new FileInputStream((String) options.valueOf("in"))) :
         * new BufferedInputStream(System.in));
         *
         * IOContextBuilder outputBuilder = new IOContextBuilder();
         * outputBuilder.schema(
         * SchemaFactory.buildSchema(new FileInputStream((String) options.valueOf("os"))));
         * outputBuilder.out(
         * options.has("out") ?
         * new BufferedOutputStream(new FileOutputStream((String) options.valueOf("out"))) :
         * new BufferedOutputStream(System.out));
         *
         * for (Object t: options.valuesOf("t")){
         * reformatter.addTransformer(t.toString());
         * }
         *
         * for (String constant: (List<String>) options.valuesOf("c")){
         * int pos = constant.indexOf(":");
         * if (pos > 0){
         * reformatter.addConstantValue(constant.substring(0, pos), constant.substring(pos + 1));
         * }
         * }
         *
         * reformatter.reformat(inputBuilder.build(), outputBuilder.build());
         * result = 0;
         * }
         * }
         * catch (SchemaException ex){
         * System.err.println(ex.getMessage());
         * }
         * catch (ConversionException ex){
         * System.err.println(ex.getMessage());
         * }
         * catch (ValidationException ex){
         * System.err.println(ex.getMessage());
         * }
         * catch (FileNotFoundException ex){
         * System.err.println(ex.getMessage());
         * }
         * catch (IOException ex){
         * System.err.println(ex.getMessage());
         * }
         * catch (OptionException ex){
         * System.err.println(ex.getMessage());
         * printHelp();
         * }
         *
         * System.exit(result);
         *
         */
    }
}
