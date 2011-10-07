package com.realcomp.data.conversion;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krenfro
 */
public class ResizeConverterTest extends SimpleConverterTest{

    public ResizeConverterTest() {
        converter = new Resize();
    }


    /**
     * Test of convert method, of class CurrentDate.
     */
    @Test
    public void testConvert() throws Exception {

        Resize converter = new Resize();        
        
        assertTrue(converter.getLength() != 0);
        assertTrue(converter.getLength() != 1);
        
        converter.setLength(10);
        assertEquals(10, converter.getLength());
        
        try{
            converter.setLength(-10);
            fail("should have thrown IllegalArgumentException");
        }
        catch(IllegalArgumentException expected){}
        
        try{
            new Resize(-10);
            fail("should have thrown IllegalArgumentException");
        }
        catch(IllegalArgumentException expected){}
        
       
        converter = new Resize(10);
        assertEquals(10, converter.getLength());
        
        converter.setLength(3);
        
        assertEquals("abc", converter.convert("abc"));
        assertEquals("abc", converter.convert("abcd"));
        assertEquals("abc", converter.convert("abcde"));
        assertEquals("abc", converter.convert("abcdef"));
        assertEquals("ab ", converter.convert("ab"));
        assertEquals("   ", converter.convert(""));
        
        converter.setLength(0);
        assertEquals("", converter.convert("abc"));
        assertEquals("", converter.convert(""));
        
        converter = new Resize();
        assertEquals("abc", converter.convert("abc"));
        assertEquals("abcd", converter.convert("abcd"));
        assertEquals("abcde", converter.convert("abcde"));
        assertEquals("", converter.convert(""));
        
        
    }
    


    @Test
    public void testCopyOf() {
        Resize a = new Resize();
        Resize b = a.copyOf();
        assertEquals(a, b);
    }
    
    @Test
    public void testHashCode() {
        Resize a = new Resize();
        Resize b = new Resize();
        assertEquals(a.hashCode(), b.hashCode());
    }
    
     @Test
    @Override
    public void testEquals() {
        
        super.testEquals();
        
        Resize a = new Resize();
        Resize b = new Resize();
        assertEquals(a, b);
        b.setLength(10);
        assertFalse(a.equals(b));
        a.setLength(10);
        assertEquals(a, b);
        
    }
    
    
}