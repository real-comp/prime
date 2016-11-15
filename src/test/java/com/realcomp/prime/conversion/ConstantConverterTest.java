package com.realcomp.prime.conversion;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 */
public class ConstantConverterTest extends SimpleConverterTest{

    public ConstantConverterTest(){
        converter = new ConstantConverter();
    }

    @Test
    @Override
    public void testNullInput() throws ConversionException{

        assertNotNull(converter.convert(null));
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

    }

    @Test
    public void testCopyOf(){
        ConstantConverter a = new ConstantConverter();
        a.setValue("KYLE");
        ConstantConverter b = a.copyOf();
        assertEquals(a, b);
        assertEquals("KYLE", b.getValue());
    }

    @Test
    @Override
    public void testEquals(){

        super.testEquals();

        ConstantConverter a = new ConstantConverter();
        ConstantConverter b = new ConstantConverter();
        assertEquals(a, b);
        a.setValue("A");
        assertFalse(a.equals(b));
        b.setValue("A");
        assertEquals(a, b);
    }

    @Test
    public void testHashCode(){
        ConstantConverter a = new ConstantConverter();
        ConstantConverter b = new ConstantConverter();
        assertEquals(a.hashCode(), b.hashCode());
    }
}