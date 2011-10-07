
package com.realcomp.data.conversion;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krenfro
 */
public class ConversionExceptionTest {
    
    public ConversionExceptionTest() {
    }

    @Test
    public void testExceptions(){
        
        IllegalArgumentException iae = new IllegalArgumentException("iae");
        
        ConversionException a = new ConversionException();
        assertEquals(null, a.getMessage());
        assertEquals(null, a.getCause());
        
        ConversionException b = new ConversionException("message");
        assertEquals("message", b.getMessage());
        assertEquals(null, b.getCause());
        
        ConversionException c = new ConversionException(iae);
        assertEquals(iae.toString(), c.getMessage());
        assertEquals(iae, c.getCause());
        
        ConversionException d = new ConversionException("message", iae);
        assertEquals("message", d.getMessage());
        assertEquals(iae, d.getCause());
        
        
        
        
        
    }
}
