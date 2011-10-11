package com.realcomp.data.record.io;

import com.realcomp.data.record.io.delimited.DelimitedFileWriter;
import com.realcomp.data.record.io.fixed.FixedFileWriter;

/**
 *
 * @author krenfro
 */
public class RecordWriterFactory {
    
    
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
        
        throw new IllegalStateException("TODO: set properties dynamically");
    }
}
