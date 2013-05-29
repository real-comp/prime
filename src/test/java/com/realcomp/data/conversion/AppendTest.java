package com.realcomp.data.conversion;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krenfro
 */
public class AppendTest extends SimpleConverterTest{

    public AppendTest(){
        converter = new Append();
    }

    @Test
    public void testValue(){
        Append append = new Append();
        assertEquals("", append.getValue());

        append.setValue("X");
        assertEquals("X", append.getValue());

        append.setValue("");
        assertEquals("", append.getValue());


    }

    @Override
    @Test
    public void testNullInput() throws ConversionException{

        Append a = new Append();
        assertEquals(a.getValue(), a.convert(null));
    }

    /**
     * Test of convert method, of class CurrentDate.
     */
    @Test
    public void testConvert() throws Exception{

        Append converter = new Append();
        converter.setValue("");
        assertEquals("abc", converter.convert("abc"));

        converter.setValue("X");
        assertEquals("abcX", converter.convert("abc"));
        assertEquals("1X", converter.convert(1));

        converter.setValue(" ");
        assertEquals("abc ", converter.convert("abc"));


    }

    @Test
    public void testCopyOf(){
        Append a = new Append();
        a.setValue("X");
        Append b = a.copyOf();
        assertEquals(a, b);

        b.setValue("Y");
        assertFalse(a.equals(b));
    }

    @Test
    public void testHashCode(){
        Append a = new Append();
        Append b = new Append();
        assertEquals(a.hashCode(), b.hashCode());
    }
}