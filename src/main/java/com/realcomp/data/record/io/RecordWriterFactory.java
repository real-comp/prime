package com.realcomp.data.record.io;

import com.realcomp.data.record.io.delimited.DelimitedFileWriter;
import com.realcomp.data.record.io.fixed.FixedFileWriter;
import com.realcomp.data.schema.FileSchema;
import com.realcomp.data.schema.SchemaException;
import java.beans.IntrospectionException;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author krenfro
 */
public class RecordWriterFactory {
    
    private static final Logger logger = Logger.getLogger(RecordWriterFactory.class.getName());
    
    public static RecordWriter build(FileSchema schema) throws FormatException, SchemaException{
        
        RecordWriter writer = build(schema.getFormat());
        writer.setSchema(schema);
        return writer;
    }
     
    public static RecordWriter build(Format format) throws FormatException{
        
        RecordWriter writer = getWriterForType(format.getType());
        applyFormatAttributes(writer, format);
        return writer;
    }
    
    
    protected static void applyFormatAttributes(RecordWriter writer, Format format){
        
        if (writer instanceof DelimitedFileWriter){
            String type = format.getType();
            if (type.equalsIgnoreCase("CSV"))
                ((DelimitedFileWriter) writer).setDelimiter("CSV");
            else if (type.equalsIgnoreCase("TAB"))
                ((DelimitedFileWriter) writer).setDelimiter("TAB");
            else if (type.length() == 1)
                ((DelimitedFileWriter) writer).setDelimiter(type);
        }
        
         try {
            DynamicPropertySetter setter = new DynamicPropertySetter();
            Set<String> unused = setter.setProperties(writer, (Map) format.getAttributes());
            for (String property: unused){
                logger.log(Level.WARNING, 
                           String.format("Writer [%s] does not support a property named [%s]",
                                         new Object[]{writer.getClass().getName(), property}));
            }
        }
        catch (IntrospectionException ex) {
            logger.log(Level.WARNING, ex.getMessage(), ex);
        }
         
    }
    
    protected static RecordWriter getWriterForType(String type) throws FormatException{
        
        RecordWriter writer = null;
        
        try {
            if (type.equalsIgnoreCase("CSV") || type.equalsIgnoreCase("TAB") || type.length() == 1){
                writer = new DelimitedFileWriter();
            }
            else if (type.equalsIgnoreCase("FIXED")){
                writer = new FixedFileWriter();
            }
            else{
                writer = (RecordWriter) Class.forName(type).newInstance();
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
        
        return writer;
    }
    
}
