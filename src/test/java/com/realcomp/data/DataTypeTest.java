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
public class DataTypeTest{

    public DataTypeTest(){
    }

    @Test
    public void testCoerceToInteger() throws ConversionException{

        assertEquals(0d, (Integer) DataType.INTEGER.coerce("0"), 0.0d);
        assertEquals(0d, (Integer) DataType.INTEGER.coerce(""), 0.0d);
        assertEquals(0d, (Integer) DataType.INTEGER.coerce("0.0"), 0.0d);


        assertFalse((Boolean) DataType.BOOLEAN.coerce("false"));
        assertFalse((Boolean) DataType.BOOLEAN.coerce("FALSE"));
        assertTrue((Boolean) DataType.BOOLEAN.coerce("true"));
        assertTrue((Boolean) DataType.BOOLEAN.coerce("TRUE"));
    }

    @Test
    public void testLeadingZeros() throws ConversionException{

        assertEquals(123d, (Double) DataType.DOUBLE.coerce("123"), 0.0d);
        assertEquals(123d, (Double) DataType.DOUBLE.coerce("0123"), 0.0d);
        assertEquals(123d, (Double) DataType.DOUBLE.coerce("00123"), 0.0d);
        assertEquals(123d, (Double) DataType.DOUBLE.coerce("000123"), 0.0d);

        assertEquals(123l, (Long) DataType.LONG.coerce("123"), 0.0d);
        assertEquals(123l, (Long) DataType.LONG.coerce("0123"), 0.0d);
        assertEquals(123l, (Long) DataType.LONG.coerce("00123"), 0.0d);
        assertEquals(123l, (Long) DataType.LONG.coerce("000123"), 0.0d);

    }
}
