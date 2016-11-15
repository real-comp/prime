package com.realcomp.prime.conversion;

import java.util.List;
import java.util.ArrayList;
import com.realcomp.prime.DataType;
import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 */
public class MultiFieldConverterTest{

    MultiFieldConverter converter;

    public MultiFieldConverterTest(){
        converter = new Concat();
    }

    @Test
    public void testSupportedTypes(){

        assertTrue(converter.getSupportedTypes().size() > 0);
        assertTrue(Arrays.asList(DataType.values()).containsAll(converter.getSupportedTypes()));

        List<DataType> types = new ArrayList<DataType>();
        types.add(DataType.STRING);
        types.add(DataType.INTEGER);
        types.add(DataType.LONG);
        types.add(DataType.FLOAT);
        types.add(DataType.DOUBLE);
        types.add(DataType.BOOLEAN);

        assertTrue(converter.getSupportedTypes().containsAll(types));
    }

    @Test
    public void testNullInput() throws ConversionException{

        try{
            converter.convert("", null);
            fail("Expected IllegalArgumentException on null input");
        }
        catch (IllegalArgumentException expected){
        }
    }

    @Test
    public void testEquals(){

        assertFalse(converter.equals(null));
        assertFalse(converter.equals(""));

    }
}
