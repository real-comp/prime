package com.realcomp.data.record.io;

import com.realcomp.data.record.io.delimited.DelimitedFileWriter;
import com.realcomp.data.record.io.fixed.FixedFileWriter;
import java.beans.IntrospectionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author krenfro
 */
public class RecordWriterFactory {
    
    private static final Logger logger = Logger.getLogger(RecordWriterFactory.class.getName());
    
    public static RecordWriter build(Format format) throws FormatException{
        
        String type = format.getType();
        RecordWriter writer = null;
        
        try {

            if (type.equals("CSV") || type.equalsIgnoreCase("TAB") || type.length() == 1){
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
        
        
        try {
            DynamicPropertySetter setter = new DynamicPropertySetter();
            setter.setProperties(writer, format.getAttributes());
        }
        catch (IntrospectionException ex) {
            logger.log(Level.WARNING, ex.getMessage(), ex);
        }
        
        return writer;
        
    }
}
