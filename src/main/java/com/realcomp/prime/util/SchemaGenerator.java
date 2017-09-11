package com.realcomp.prime.util;

import au.com.bytecode.opencsv.CSVParser;
import com.realcomp.prime.record.Record;
import com.realcomp.prime.schema.Field;
import com.realcomp.prime.schema.FieldList;
import com.realcomp.prime.schema.Schema;
import com.realcomp.prime.schema.xml.XStreamFactory;
import com.thoughtworks.xstream.XStream;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Schema generator for delimited files.
 * Uses the first record in a delimited file to generate a schema.
 * Escapes invalid characters in the header.
 *
 */
public class SchemaGenerator{

    private static final Logger logger = Logger.getLogger(SchemaGenerator.class.getName());

    private String delimiter = "TAB";

    public void generate(InputStream in, OutputStream out) throws IOException{
        String header = new BufferedReader(new InputStreamReader(in)).readLine();
        String[] fieldNames = getFieldNames(header);
        Schema schema = buildSchema(fieldNames);
        writeSchema(schema, out);
    }

    public Schema generate(File file) throws IOException{
        String header = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))){
            header = reader.readLine();
        }
        String[] fieldNames = getFieldNames(header);
        return buildSchema(fieldNames);
    }

    public Schema generate(Record record) throws IOException{
        String[] fieldNames = record.keySet().toArray(new String[record.keySet().size()]);
        return buildSchema(fieldNames);
    }


    protected String toXml(Schema schema){
        XStream xstream = XStreamFactory.build(true);
        StringWriter temp = new StringWriter();
        xstream.toXML(schema, temp);
        return temp.getBuffer().toString();
    }

    protected void writeSchema(Schema schema, OutputStream out) throws IOException{

        String xml = toXml(schema);
        xml = clean(xml);
        out.write(xml.getBytes());
    }

    protected String[] getFieldNames(String header) throws IOException{
        CSVParser parser;

        if (delimiter.equals("\t") || delimiter.equalsIgnoreCase("TAB")){
            parser = new CSVParser('\t', '\u0000');
        }
        else if (delimiter.equalsIgnoreCase("CSV")){
            parser = new CSVParser(',', CSVParser.DEFAULT_QUOTE_CHARACTER);
        }
        else{
            parser = new CSVParser(delimiter.charAt(0), CSVParser.DEFAULT_QUOTE_CHARACTER);
        }

        return parser.parseLine(header);
    }

    protected Schema buildSchema(String[] fieldNames){
        Schema schema = new Schema();
        Map<String, String> format = new HashMap<>();
        format.put("header", "true");

        if (delimiter.equals("\t") || delimiter.equalsIgnoreCase("TAB")){
            format.put("type", "TAB");
        }
        else if (delimiter.equals(",") || delimiter.equalsIgnoreCase("CSV")){
            format.put("type", "CSV");
        }
        else{
            format.put("type", "" + delimiter.charAt(0));
        }

        schema.setFormat(format);


        int count = 1;
        FieldList fieldList = new FieldList();
        for (String fieldName : fieldNames){
            fieldName = escapeFieldName(fieldName);
            fieldList.add(new Field(fieldName.isEmpty() ? "FIELD" + count : fieldName));
            count++;
        }
        schema.addFieldList(fieldList);

        return schema;
    }

    protected String escapeFieldName(String original){
        return original.replaceAll("[.+]","_");
    }

    protected String clean(String dirty){
        String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
        header = header.concat("<rc:schema\n");
        header = header.concat("   xmlns:rc=\"http://www.real-comp.com/realcomp-data/schema/file-schema/1.2\"\n");
        header = header.concat("   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
        header =
                header.concat("   xsi:schemaLocation=\"http://www.real-comp.com/realcomp-prime/schema/file-schema/1.2 http://www.real-comp.com/realcomp-prime/schema/file-schema/1.2/file-schema.xsd\">\n");

        String clean = header.concat(dirty.replace("<schema>", ""));
        clean = clean.replace("</schema>", "</rc:schema>");
        clean = clean.replaceAll(" length=\"0\"", "");
        clean = clean.replaceAll(" classifier=\".*\"", "");
        clean = clean.concat("\n");
        return clean;
    }

    public String getDelimiter(){
        return delimiter;
    }

    public void setDelimiter(String delimiter){
        this.delimiter = delimiter;
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
                acceptsAll(Arrays.asList("d", "delimiter"), "delimiter").withRequiredArg().describedAs("delimiter");
                accepts("in", "input file (default: STDIN)").withRequiredArg().describedAs("file");
                accepts("out", "output file (default: STDOUT)").withRequiredArg().describedAs("file");
                acceptsAll(Arrays.asList("h", "?", "help"), "help");
            }
        };

        int result = 0;

        try{
            OptionSet options = parser.parse(args);
            if (options.has("?")){
                printHelp(parser);
            }
            else{
                SchemaGenerator generator = new SchemaGenerator();
                if (options.has("d")){
                    generator.setDelimiter((String) options.valueOf("d"));
                }

                InputStream in =  options.has("in")
                        ? new BufferedInputStream(new FileInputStream((String) options.valueOf("in")))
                        : new BufferedInputStream(System.in);
                OutputStream out = options.has("out")
                        ? new BufferedOutputStream(new FileOutputStream((String) options.valueOf("out")))
                        : new BufferedOutputStream(System.out);

                generator.generate(in, out);
                result = 0;
                in.close();
                out.close();
            }
        }
        catch (IOException ex){
            logger.severe(ex.getMessage());
        }
        catch (OptionException ex){
            logger.severe(ex.getMessage());
            printHelp(parser);
        }

        System.exit(result);
    }
}
