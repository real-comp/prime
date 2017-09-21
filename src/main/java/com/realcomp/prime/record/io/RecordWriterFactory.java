package com.realcomp.prime.record.io;

import com.realcomp.prime.record.io.delimited.DelimitedFileReader;
import com.realcomp.prime.record.io.delimited.DelimitedFileWriter;
import com.realcomp.prime.record.io.fixed.FixedFileWriter;
import com.realcomp.prime.schema.Schema;
import com.realcomp.prime.schema.SchemaException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class RecordWriterFactory{

    private static final Logger logger = Logger.getLogger(RecordWriterFactory.class.getName());

    private static final Map<String, String> types;

    static{
        types = new HashMap<String, String>();
        types.put("CSV", DelimitedFileWriter.class.getName());
        types.put("TAB", DelimitedFileWriter.class.getName());
        types.put("DELIM", DelimitedFileReader.class.getName());
        types.put("DELIMITED", DelimitedFileReader.class.getName());
        types.put("FIXED", FixedFileWriter.class.getName());
        types.put("JSON", "com.realcomp.prime.record.io.json.JsonWriter");
        types.put("DBF",   "com.realcomp.prime.xbase.XBaseWriter");
        types.put("XBASE", "com.realcomp.prime.xbase.XBaseWriter");
    }

    public static void registerWriter(String type, String writerClass){
        types.put(type, writerClass);
    }

    public static RecordWriter build(Schema schema) throws SchemaException{

        return build(schema.getFormat());
    }

    public static RecordWriter build(Map<String, String> format) throws FormatException{

        return getWriterForType(format.get("type"));
    }

    protected static RecordWriter getWriterForType(String type) throws FormatException{

        RecordWriter writer = null;
        String classname = types.get(type.toUpperCase());
        if (classname == null && type.length() == 1){
            classname = types.get("TAB"); //single character; default to delimited
        }
        else if (classname == null){
            classname = type;
        }

        try{
            writer = (RecordWriter) Class.forName(classname).newInstance();
        }
        catch (ClassNotFoundException ex){
            throw new FormatException(ex);
        }
        catch (InstantiationException ex){
            throw new FormatException(ex);
        }
        catch (IllegalAccessException ex){
            throw new FormatException(ex);
        }

        return writer;
    }
}
