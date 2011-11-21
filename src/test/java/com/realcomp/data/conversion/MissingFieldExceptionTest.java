
package com.realcomp.data.conversion;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krenfro
 */
public class MissingFieldExceptionTest extends ConversionExceptionTest {
    
    public MissingFieldExceptionTest() {
    }

    @Test
    public void testFieldName(){
        
        MissingFieldException e = new MissingFieldException("asdf");
        assertEquals("asdf", e.getFieldName());
        e.setFieldName("a");
        assertEquals("a", e.getFieldName());
        
        e.setFieldName("");
        assertEquals("", e.getFieldName());
                
        e.setFieldName(null);
        assertEquals(null, e.getFieldName());
        
        e = new MissingFieldException("asdf", new IllegalArgumentException());
    }
}
