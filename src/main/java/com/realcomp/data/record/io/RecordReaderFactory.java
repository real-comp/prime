package com.realcomp.data.record.io;

import com.realcomp.data.record.io.delimited.DelimitedFileReader;
import com.realcomp.data.record.io.fixed.FixedFileReader;
import com.realcomp.data.record.io.json.JsonReader;
import com.realcomp.data.schema.Schema;
import com.realcomp.data.schema.SchemaException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author krenfro
 */
public class RecordReaderFactory {
        
    private static Map<String,String> types;
    
    static{
        types = new HashMap<String,String>();
        types.put("CSV", DelimitedFileReader.class.getName());
        types.put("TAB", DelimitedFileReader.class.getName());
        types.put("FIXED", FixedFileReader.class.getName());
        types.put("JSON", JsonReader.class.getName());
    }
    
    public static void registerReader(String type, String readerClass){
        types.put(type, readerClass);
    }
    
    public static RecordReader build(Schema schema) throws FormatException, SchemaException{        
        return build(schema.getFormat());
    }
    
    
    public static RecordReader build(Map<String,String> format) throws FormatException{        
        return getReaderForType(format.get("type"));
    }
    
    protected static RecordReader getReaderForType(String type) throws FormatException{
        
        if (type == null)
            throw new IllegalArgumentException("type is null");
        
        RecordReader reader = null;        
        String classname = types.get(type.toUpperCase());
        if (classname == null && type.length() == 1){
            classname = types.get("TAB"); //single character; default to delimited
        }
        else if (classname == null){
            classname = type;
        }
        
        try {
            reader = (RecordReader) Class.forName(classname).newInstance();
        }
        catch (ClassNotFoundException ex) {
            throw new FormatException(ex);
        }
        catch (InstantiationException ex) {
            throw new FormatException(ex);
        }
        catch (IllegalAccessException ex) {
            throw new FormatException(ex);
        }

        return reader;
    }
    
}
