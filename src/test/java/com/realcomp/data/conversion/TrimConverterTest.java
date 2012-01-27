package com.realcomp.data.conversion;

import com.realcomp.data.DataType;
import java.util.List;
import java.util.ArrayList;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krenfro
 */
public class TrimConverterTest extends StringConverterTest{

    public TrimConverterTest() {
        converter = new Trim();
    }


    /**
     * Test of convert method, of class CurrentDate.
     */
    @Test
    public void testConvert() throws Exception {

        Trim converter = new Trim();        
        
        assertEquals("abc", converter.convert("abc"));
        assertEquals("abc", converter.convert("abc "));
        assertEquals("abc", converter.convert("abc  "));
        assertEquals("abc", converter.convert("  abc  "));
        assertEquals("", converter.convert(""));
        assertEquals("", converter.convert(" "));
        assertEquals("", converter.convert("  "));
        assertEquals("", converter.convert("   "));
        
        
    }
    


    @Test
    public void testCopyOf() {
        Trim a = new Trim();
        Trim b = a.copyOf();
        assertEquals(a, b);
    }
    
    @Test
    public void testHashCode() {
        Trim a = new Trim();
        Trim b = new Trim();
        assertEquals(a.hashCode(), b.hashCode());
    }
    
    
}