package com.realcomp.data.record.io;

import com.realcomp.data.record.io.delimited.DelimitedFileReader;
import com.realcomp.data.record.io.fixed.FixedFileReader;
import com.realcomp.data.schema.FileSchema;
import com.realcomp.data.schema.SchemaException;
import java.beans.IntrospectionException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author krenfro
 */
public class RecordReaderFactory {
    
    private static final Logger logger = Logger.getLogger(RecordReaderFactory.class.getName());
    
    public static RecordReader build(FileSchema schema) throws FormatException, SchemaException{
        
        RecordReader reader = build(schema.getFormat());
        reader.setSchema(schema);
        return reader;
    }
    
    public static RecordReader build(Format format) throws FormatException{
        
        String type = format.getType();
        RecordReader reader = null;
        
        try {
            if (type.equals("CSV") || type.equalsIgnoreCase("TAB") || type.length() == 1){
                reader = new DelimitedFileReader();
            }
            else if (type.equalsIgnoreCase("FIXED")){
                reader = new FixedFileReader();
            }
            else{
                reader = (RecordReader) Class.forName(type).newInstance();
            }
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
        
        try {
            DynamicPropertySetter setter = new DynamicPropertySetter();
            Set<String> unused = setter.setProperties(reader, format.getAttributes());
            for (String property: unused){
                logger.log(Level.WARNING, 
                           String.format("Reader [%s] does not support a property named [%s]",
                                         new Object[]{reader.getClass().getName(), property}));
            }
        }
        catch (IntrospectionException ex) {
            logger.log(Level.WARNING, ex.getMessage(), ex);
        }
        
        return reader;
    }
}
