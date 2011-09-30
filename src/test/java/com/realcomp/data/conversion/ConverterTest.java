package com.realcomp.data.conversion;

import com.realcomp.data.DataType;
import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krenfro
 */
public class ConverterTest {
    
    Converter converter;
    
    public ConverterTest(){
        converter = new MockConverter();
    }
      
    @Test
    public void testSupportedTypes(){
        
        Converter b = new MockConverter();        
        assertTrue(converter.getSupportedTypes().size() > 0);
        assertTrue(Arrays.asList(DataType.values()).containsAll(converter.getSupportedTypes()));
    }
    
    @Test
    public void testNullInput() throws ConversionException{
        
        try{
            converter.convert(null);
            fail("Expected IllegalArgumentException on null input");
        }
        catch(IllegalArgumentException expected){
        }
    }
    
    @Test
    public void testEquals(){
     
        assertFalse(converter.equals(null));
        assertFalse(converter.equals(""));
        
    }
              
}
