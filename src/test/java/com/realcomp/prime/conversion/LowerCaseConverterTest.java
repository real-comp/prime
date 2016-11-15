package com.realcomp.prime.conversion;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krenfro
 */
public class LowerCaseConverterTest extends StringConverterTest{

    public LowerCaseConverterTest(){
        converter = new LowerCase();
    }

    /**
     * Test of convert method, of class CurrentDate.
     */
    @Test
    public void testConvert() throws Exception{

        LowerCase converter = new LowerCase();
        assertEquals("asdf", converter.convert("ASDF"));
        assertEquals("2893", converter.convert("2893"));
    }

    @Test
    public void testCopyOf(){
        LowerCase a = new LowerCase();
        LowerCase b = a.copyOf();
        assertEquals(a, b);
    }

    @Test
    public void testHashCode(){
        LowerCase a = new LowerCase();
        LowerCase b = new LowerCase();
        assertEquals(a.hashCode(), b.hashCode());
    }
}