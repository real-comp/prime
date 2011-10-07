package com.realcomp.data.conversion;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krenfro
 */
public class LastNameConverterTest extends SimpleConverterTest{

    public LastNameConverterTest() {
        converter = new LastName();
    }


    /**
     * Test of convert method, of class CurrentDate.
     */
    @Test
    public void testConvert() throws Exception {

        LastName converter = new LastName();        
        assertEquals("RENFRO", converter.convert("RENFRO, RYAN KYLE"));        
        assertEquals("REAL-COMP, INC.", converter.convert("REAL-COMP, INC."));
    }


    @Test
    public void testCopyOf() {
        LastName a = new LastName();
        LastName b = a.copyOf();
        assertEquals(a, b);
    }
    
    @Test
    public void testHashCode() {
        LastName a = new LastName();
        LastName b = new LastName();
        assertEquals(a.hashCode(), b.hashCode());
    }
    
    
    
}