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
public class RoundConverterTest extends SimpleConverterTest{

    public RoundConverterTest(){
        converter = new Round();
    }

    /**
     * Test of convert method, of class CurrentDate.
     */
    @Test
    public void testConvert() throws Exception{

        Round converter = new Round();

        assertEquals("", converter.convert(""));

        assertEquals(100l, converter.convert("100"));
        assertEquals(100l, converter.convert(100));
        assertEquals(100l, converter.convert(100f));
        assertEquals(100l, converter.convert(100l));
        assertEquals(100l, converter.convert(100.0));
        assertEquals(100l, converter.convert(100.f));
        assertEquals(100l, converter.convert(100.1f));
        assertEquals(100l, converter.convert(100.4));
        assertEquals(101l, converter.convert(100.5f));
        assertEquals(101l, converter.convert(100.9f));
        assertEquals(101l, converter.convert(101.4f));


        try{
            converter.convert("asdf");
            fail("should have thrown ConversionException");
        }
        catch (ConversionException expected){
        }

    }

    @Test
    public void testCopyOf(){
        Round a = new Round();
        Round b = a.copyOf();
        assertEquals(a, b);
    }

    @Test
    public void testHashCode(){
        Round a = new Round();
        Round b = new Round();
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Override
    @Test
    public void testSupportedTypes(){


        List<DataType> types = new ArrayList<DataType>();
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