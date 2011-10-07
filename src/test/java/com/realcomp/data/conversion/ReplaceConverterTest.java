package com.realcomp.data.conversion;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krenfro
 */
public class ReplaceConverterTest extends SimpleConverterTest{

    public ReplaceConverterTest() {
        converter = new Replace();
    }


    /**
     * Test of convert method, of class CurrentDate.
     */
    @Test
    public void testConvert() throws Exception {

        Replace converter = new Replace();        
        
        assertEquals("", converter.getRegex());
        assertEquals("", converter.getReplacement());
        
        
        converter.setRegex("a");
        assertEquals("", converter.convert("aaa"));
        assertEquals("b", converter.convert("aab"));
        assertEquals("b", converter.convert("aba"));
        assertEquals("b", converter.convert("baa"));
        assertEquals("bb", converter.convert("bab"));
    }


    @Test
    public void testCopyOf() {
        Replace a = new Replace();
        Replace b = a.copyOf();
        assertEquals(a, b);
    }
    
    @Test
    public void testHashCode() {
        Replace a = new Replace();
        Replace b = new Replace();
        assertEquals(a.hashCode(), b.hashCode());
    }
    
     @Test
    @Override
    public void testEquals() {
        
        super.testEquals();
        
        Replace a = new Replace();
        Replace b = new Replace();
        assertEquals(a, b);
        b.setRegex("A");
        assertFalse(a.equals(b));
        b.setRegex(a.getRegex());
        
        assertEquals(a, b);
        b.setReplacement("A");
        assertFalse(a.equals(b));
        b.setReplacement(a.getReplacement());
        
    }
    
    
}