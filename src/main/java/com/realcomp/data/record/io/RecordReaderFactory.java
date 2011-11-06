package com.realcomp.data.record.io;

import com.realcomp.data.record.io.delimited.DelimitedFileReader;
import com.realcomp.data.record.io.fixed.FixedFileReader;
import com.realcomp.data.record.io.json.JsonFileReader;
import com.realcomp.data.schema.FileSchema;
import com.realcomp.data.schema.SchemaException;
import java.beans.IntrospectionException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author krenfro
 */
public class RecordReaderFactory {
    
    private static final Logger logger = Logger.getLogger(RecordReaderFactory.class.getName());
    
    private static Map<String,String> types;
    
    static{
        types = new HashMap<String,String>();
        types.put("CSV", DelimitedFileReader.class.getName());
        types.put("TAB", DelimitedFileReader.class.getName());
        types.put("FIXED", FixedFileReader.class.getName());
        types.put("JSON", JsonFileReader.class.getName());
    }
    
    public static void registerReader(String type, String readerClass){
        types.put(type, readerClass);
    }
    
    public static RecordReader build(FileSchema schema) throws FormatException, SchemaException{
        
        RecordReader reader = build(schema.getFormat());
        reader.setSchema(schema);
        return reader;
    }
    
    
    public static RecordReader build(Format format) throws FormatException{        
        RecordReader reader = getReaderForType(format.getType());
        applyFormatAttributes(reader, format);
        return reader;
    }
    
    
    protected static void applyFormatAttributes(RecordReader reader, Format format) throws FormatException{
        
        if (reader instanceof DelimitedFileReader){            
            String type = format.getType();
            if (type.equalsIgnoreCase("CSV"))
                ((DelimitedFileReader) reader).setDelimiter("CSV");
            else if (type.equalsIgnoreCase("TAB"))
                ((DelimitedFileReader) reader).setDelimiter("TAB");
            else if (type.length() == 1)
                ((DelimitedFileReader) reader).setDelimiter(type);
        }
        
         try {
            DynamicPropertySetter setter = new DynamicPropertySetter();
            Set<String> unused = setter.setProperties(reader, (Map) format.getAttributes());
            for (String property: unused){
                logger.log(Level.WARNING, 
                           String.format("Reader [%s] does not support a property named [%s]",
                                         new Object[]{reader.getClass().getName(), property}));
            }
        }
        catch (IntrospectionException ex) {
            logger.log(Level.WARNING, ex.getMessage(), ex);
        }
         
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
