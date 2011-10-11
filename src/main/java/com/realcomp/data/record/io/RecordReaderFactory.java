package com.realcomp.data.record.io;

import com.realcomp.data.record.io.delimited.DelimitedFileReader;
import com.realcomp.data.record.io.fixed.FixedFileReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author krenfro
 */
public class RecordReaderFactory {
    
    
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
        
        throw new IllegalStateException("TODO: set properties dynamically");
    }
}
