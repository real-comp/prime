package com.realcomp.prime.conversion;

import com.realcomp.prime.DataType;
import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 */
public class ConverterTest{

    Converter converter;

    public ConverterTest(){
        converter = new MockConverter();
    }

    @Test
    public void testSupportedTypes(){

        Converter b = new MockConverter();
        assertTrue(converter.getSupportedTypes().size() > 0);
        assertTrue(Arrays.asList(DataType.values()).containsAll(converter.getSupportedTypes()));
    }

    @Test
    public void testNullInput() throws ConversionException{

        assertNull(converter.convert(null));
    }

    @Test
    public void testEquals(){

        assertFalse(converter.equals(null));
        assertFalse(converter.equals(""));

    }
}
