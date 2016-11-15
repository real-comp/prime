package com.realcomp.prime.conversion;

import com.realcomp.prime.DataType;
import java.util.List;
import java.util.ArrayList;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 */
public class MultiplyConverterTest extends SimpleConverterTest{

    public MultiplyConverterTest(){
        converter = new Round();
    }

    /**
     * Test of convert method, of class CurrentDate.
     */
    @Test
    public void testConvert() throws Exception{

        Multiply converter = new Multiply();

        assertEquals(0d, converter.convert(""));

        assertEquals(100d, converter.convert("100"));

        converter.setFactor(0.1d);
        assertEquals(10d, converter.convert("100"));

        converter.setFactor(0.01d);
        assertEquals(1d, converter.convert("100"));
        assertEquals(1d, converter.convert("0100"));


        try{
            converter.convert("asdf");
            fail("should have thrown ConversionException");
        }
        catch (ConversionException expected){
        }

    }

    @Test
    public void testCopyOf(){
        Multiply a = new Multiply();
        Multiply b = a.copyOf();
        assertEquals(a, b);
    }

    @Test
    public void testHashCode(){
        Multiply a = new Multiply();
        Multiply b = new Multiply();
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Override
    @Test
    public void testSupportedTypes(){
        List<DataType> types = new ArrayList<>();
        types.add(DataType.STRING);
        types.add(DataType.INTEGER);
        types.add(DataType.LONG);
        types.add(DataType.FLOAT);
        types.add(DataType.DOUBLE);
        //   types.add(DataType.BOOLEAN);

        assertTrue(converter.getSupportedTypes().containsAll(types));

        assertFalse(converter.getSupportedTypes().contains(DataType.BOOLEAN));
    }
}