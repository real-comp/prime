package com.realcomp.prime.conversion;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krenfro
 */
public class ReplaceConverterTest extends SimpleConverterTest{

    public ReplaceConverterTest(){
        converter = new Replace();
    }

    /**
     * Test of convert method, of class CurrentDate.
     */
    @Test
    public void testConvert() throws Exception{

        Replace converter = new Replace();

        assertEquals("", converter.getRegex());
        assertEquals("", converter.getReplacement());


        converter.setRegex("a");
        assertEquals("", converter.convert("aaa"));
        assertEquals("b", converter.convert("aab"));
        assertEquals("b", converter.convert("aba"));
        assertEquals("b", converter.convert("baa"));
        assertEquals("bb", converter.convert("bab"));
    }


    @Test
    public void testRemoveMultiSpaces() throws Exception{

        Replace converter = new Replace();
        converter.setRegex("[ ]+");
        converter.setReplacement(" ");

        assertEquals(" ", converter.convert("  "));
        assertEquals(" a", converter.convert("  a"));
        assertEquals(" a ", converter.convert("  a "));
        assertEquals(" a ", converter.convert("  a  "));
        assertEquals("a a", converter.convert("a  a"));
        assertEquals("a a", converter.convert("a   a"));
        assertEquals("a a", converter.convert("a    a"));
        assertEquals("a a", converter.convert("a     a"));
        assertEquals("a a ", converter.convert("a     a "));
        assertEquals("a a ", converter.convert("a     a  "));
        assertEquals("a a ", converter.convert("a     a   "));
        assertEquals("a a ", converter.convert("a     a    "));
    }

    @Test
    public void testLeadingZeros() throws Exception{

        Replace replace = new Replace();
        replace.setRegex("^0+");
        replace.setReplacement("");

        assertEquals("1", replace.convert("1"));
        assertEquals("1", replace.convert("01"));
        assertEquals("1", replace.convert("001"));
        assertEquals("1", replace.convert("0001"));
        assertEquals("1", replace.convert("00001"));
    }


    @Test
    public void testReplacementContainsRegex() throws Exception{

        Replace replace = new Replace();
        replace.setRegex("I");
        replace.setReplacement("ID");

        assertEquals("ID", replace.convert("I"));
        assertEquals("ID ", replace.convert("I "));
        assertEquals(" ID", replace.convert(" I"));
        assertEquals(" ID ", replace.convert(" I "));
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

    @Test
    public void testAsterisks() throws Exception{

        Replace converter = new Replace();
        converter.setRegex("[\\*].+");
        converter.setReplacement("*");

        assertEquals("123", converter.convert("123"));
        assertEquals("123*", converter.convert("123*"));
        assertEquals("123*", converter.convert("123*a"));
    }
}