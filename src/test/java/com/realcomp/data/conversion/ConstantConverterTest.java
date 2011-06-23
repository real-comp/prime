package com.realcomp.data.conversion;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krenfro
 */
public class ConstantConverterTest {

    public ConstantConverterTest() {
    }

    @Test
    public void testConverter() throws ConversionException{

        ConstantConverter c = new ConstantConverter();
        c.setValue("A");
        assertEquals("A", c.convert("anything"));
        assertEquals("A", c.convert("else"));

        c.setValue("B");
        assertEquals("B", c.convert("anything"));
        assertEquals("B", c.convert("else"));

        try{
            c.convert(null);
            fail("should have thrown IllegalArgumentException");
        }
        catch(IllegalArgumentException expected){
        }


    }
}