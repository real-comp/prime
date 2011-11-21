package com.realcomp.data.conversion;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krenfro
 */
public class FormatPhoneConverterTest extends SimpleConverterTest{

    public FormatPhoneConverterTest() {
        converter = new FormatPhone();
    }


    /**
     * Test of convert method, of class CurrentDate.
     */
    @Test
    public void testConvert() throws Exception {

        FormatPhone converter = new FormatPhone();        
        assertEquals("(512) 289-3035", converter.convert("5122893035"));
        assertEquals("(512) 289-3035", converter.convert("5122893035"));
        assertEquals("(512) 289-3035", converter.convert("15122893035"));
        assertEquals("(512) 289-3035", converter.convert("512-289-3035"));
        assertEquals("(512) 289-3035", converter.convert("(512)289*3035"));
        assertEquals("(512) 289-3035", converter.convert("---5-1-22-89-3-035"));
        assertEquals("289-3035", converter.convert("2893035"));
        assertEquals("2893", converter.convert("2893"));
    }


    @Test
    public void testCopyOf() {
        FormatPhone a = new FormatPhone();
        FormatPhone b = a.copyOf();
        assertEquals(a, b);
    }
    
    @Test
    public void testHashCode() {
        FormatPhone a = new FormatPhone();
        FormatPhone b = new FormatPhone();
        assertEquals(a.hashCode(), b.hashCode());
    }
    
    
    
}