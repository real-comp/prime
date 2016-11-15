package com.realcomp.prime.conversion;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krenfro
 */
public class RemoveLeadingConverterTest extends SimpleConverterTest{

    public RemoveLeadingConverterTest(){
        converter = new RemoveLeading();
    }

    /**
     * Test of convert method, of class CurrentDate.
     */
    @Test
    public void testConvert() throws Exception{

        RemoveLeading converter = new RemoveLeading();
        assertEquals("aaa", converter.convert("aaa"));
        converter.setValue("a");
        assertEquals("", converter.convert("aaa"));
        converter.setValue("aa");
        assertEquals("a", converter.convert("aaa"));
        converter.setValue("aaa");
        assertEquals("", converter.convert("aaa"));
    }

    @Test
    public void testLeadingZeros() throws Exception{

        RemoveLeading replace = new RemoveLeading();
        replace.setValue("0");

        assertEquals("1", replace.convert("1"));
        assertEquals("1", replace.convert("01"));
        assertEquals("1", replace.convert("001"));
        assertEquals("1", replace.convert("0001"));
        assertEquals("1", replace.convert("00001"));
    }

    @Test
    public void testCopyOf(){
        Replace a = new Replace();
        Replace b = a.copyOf();
        assertEquals(a, b);
    }

    @Test
    public void testHashCode(){
        Replace a = new Replace();
        Replace b = new Replace();
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    @Override
    public void testEquals(){

        super.testEquals();

        Replace a = new Replace();
        Replace b = new Replace();
        assertEquals(a, b);
        b.setRegex("A");
        assertFalse(a.equals(b));
        b.setRegex(a.getRegex());

        assertEquals(a, b);
        b.setReplacement("A");
        assertFalse(a.equals(b));
        b.setReplacement(a.getReplacement());

    }
}