package com.realcomp.data.conversion;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krenfro
 */
public class GenerateUniqueIdTest extends SimpleConverterTest{

    public GenerateUniqueIdTest() {
        converter = new GenerateUniqueId();
    }


    @Test
    @Override
    public void testNullInput() throws ConversionException{

        assertNotNull(converter.convert(null));
    }


    /**
     * Test of convert method, of class CurrentDate.
     */
    @Test
    public void testConvert() throws Exception {

        GenerateUniqueId a = new GenerateUniqueId();

        assertEquals(1l, a.convert(null));
        assertEquals(2l, a.convert(null));
        assertEquals(3l, a.convert(null));
        assertEquals(4l, a.convert(null));
        assertEquals(5l, a.convert(null));

        GenerateUniqueId b = new GenerateUniqueId();
        assertEquals(1l, b.convert(null));

        assertEquals(6l, a.convert(null));
    }

}