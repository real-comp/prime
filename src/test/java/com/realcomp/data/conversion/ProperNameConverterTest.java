package com.realcomp.data.conversion;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krenfro
 */
public class ProperNameConverterTest extends SimpleConverterTest{

    public ProperNameConverterTest() {
        converter = new ProperName();
    }


    /**
     * Test of convert method, of class CurrentDate.
     */
    @Test
    public void testConvert() throws Exception {

        ProperName converter = new ProperName();        
        assertEquals("RYAN KYLE RENFRO", converter.convert("RENFRO, RYAN KYLE"));        
        assertEquals("REAL-COMP, INC.", converter.convert("REAL-COMP, INC."));
    }


    @Test
    public void testCopyOf() {
        ProperName a = new ProperName();
        ProperName b = a.copyOf();
        assertEquals(a, b);
    }
    
    @Test
    public void testHashCode() {
        ProperName a = new ProperName();
        ProperName b = new ProperName();
        assertEquals(a.hashCode(), b.hashCode());
    }
    
    
    
}