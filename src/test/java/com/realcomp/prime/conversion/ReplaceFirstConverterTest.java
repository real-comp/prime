package com.realcomp.prime.conversion;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krenfro
 */
public class ReplaceFirstConverterTest extends SimpleConverterTest{

    public ReplaceFirstConverterTest(){
        converter = new ReplaceFirst();
    }

    /**
     * Test of convert method, of class CurrentDate.
     */
    @Test
    public void testConvert() throws Exception{

        ReplaceFirst converter = new ReplaceFirst();

        assertEquals("", converter.getRegex());
        assertEquals("", converter.getReplacement());


        converter.setRegex("a");
        assertEquals("aa", converter.convert("aaa"));
        assertEquals("ab", converter.convert("aab"));
        assertEquals("ba", converter.convert("aba"));
        assertEquals("ba", converter.convert("baa"));
        assertEquals("bb", converter.convert("bab"));
    }

    @Test
    public void testLeadingZeros() throws Exception{

        ReplaceFirst replaceFirst = new ReplaceFirst();
        replaceFirst.setRegex("^0");
        replaceFirst.setReplacement("");

        assertEquals("1", replaceFirst.convert("1"));
        assertEquals("1", replaceFirst.convert("01"));
        assertEquals("01", replaceFirst.convert("001"));
        assertEquals("001", replaceFirst.convert("0001"));
        assertEquals("0001", replaceFirst.convert("00001"));
    }

    @Test
    public void testCopyOf(){
        ReplaceFirst a = new ReplaceFirst();
        ReplaceFirst b = a.copyOf();
        assertEquals(a, b);
    }

    @Test
    public void testHashCode(){
        ReplaceFirst a = new ReplaceFirst();
        ReplaceFirst b = new ReplaceFirst();
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    @Override
    public void testEquals(){

        super.testEquals();

        ReplaceFirst a = new ReplaceFirst();
        ReplaceFirst b = new ReplaceFirst();
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