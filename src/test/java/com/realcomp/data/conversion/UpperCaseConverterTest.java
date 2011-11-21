package com.realcomp.data.conversion;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krenfro
 */
public class UpperCaseConverterTest extends SimpleConverterTest{

    public UpperCaseConverterTest() {
        converter = new UpperCase();
    }


    /**
     * Test of convert method, of class CurrentDate.
     */
    @Test
    public void testConvert() throws Exception {

        UpperCase converter = new UpperCase();        
        assertEquals("ASDF", converter.convert("ASDF"));
        assertEquals("ASDF", converter.convert("asdf"));
        assertEquals("ASDF", converter.convert("asdF"));
        assertEquals("2893", converter.convert("2893"));
    }


    @Test
    public void testCopyOf() {
        UpperCase a = new UpperCase();
        UpperCase b = a.copyOf();
        assertEquals(a, b);
    }
    
    @Test
    public void testHashCode() {
        UpperCase a = new UpperCase();
        UpperCase b = new UpperCase();
        assertEquals(a.hashCode(), b.hashCode());
    }
    
    
    
}