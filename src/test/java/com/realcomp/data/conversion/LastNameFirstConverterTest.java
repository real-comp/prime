package com.realcomp.data.conversion;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krenfro
 */
public class LastNameFirstConverterTest extends SimpleConverterTest{

    public LastNameFirstConverterTest() {
        converter = new LastNameFirst();
    }


    /**
     * Test of convert method, of class CurrentDate.
     */
    @Test
    public void testConvert() throws Exception {

        LastNameFirst converter = new LastNameFirst();        
        assertEquals("RENFRO, RYAN KYLE", converter.convert("RENFRO, RYAN KYLE"));        
        assertEquals("REAL-COMP, INC.", converter.convert("REAL-COMP, INC."));
    }


    @Test
    public void testCopyOf() {
        LastNameFirst a = new LastNameFirst();
        LastNameFirst b = a.copyOf();
        assertEquals(a, b);
    }
    
    @Test
    public void testHashCode() {
        LastNameFirst a = new LastNameFirst();
        LastNameFirst b = new LastNameFirst();
        assertEquals(a.hashCode(), b.hashCode());
    }
    
    
    
}