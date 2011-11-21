package com.realcomp.data;

import com.realcomp.data.conversion.ConversionException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krenfro
 */
public class DataTypeTest {
    
    public DataTypeTest() {
    }

    @Test
    public void testCoerceToInteger() throws ConversionException{
        
        assertEquals(0d, (Integer) DataType.INTEGER.coerce("0"), 0.0d);
        assertEquals(0d, (Integer) DataType.INTEGER.coerce(""), 0.0d);
        assertEquals(0d, (Integer) DataType.INTEGER.coerce("0.0"), 0.0d);
    }
}
