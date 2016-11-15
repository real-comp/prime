package com.realcomp.prime.conversion;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author krenfro
 */
public class DefaultConverterTest extends SimpleConverterTest{

    public DefaultConverterTest(){
        converter = new Replace();
    }


    @Test
    public void testConverter() throws Exception{

        Default converter = new Default();
        converter.setValue("test");
        assertEquals("test", converter.convert(""));
        assertEquals("a", converter.convert("a"));
        assertEquals("test", converter.convert(null));

    }


}