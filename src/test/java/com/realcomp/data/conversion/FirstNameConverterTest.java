package com.realcomp.data.conversion;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krenfro
 */
public class FirstNameConverterTest extends StringConverterTest{

    public FirstNameConverterTest(){
        converter = new FirstName();
    }

    /**
     * Test of convert method, of class CurrentDate.
     */
    @Test
    public void testConvert() throws Exception{

        FirstName converter = new FirstName();
        assertEquals("RYAN", converter.convert("RENFRO, RYAN KYLE"));
        assertEquals("", converter.convert("REAL-COMP"));
    }

    @Test
    public void testCopyOf(){
        FirstName a = new FirstName();
        FirstName b = a.copyOf();
        assertEquals(a, b);
    }

    @Test
    public void testHashCode(){
        FirstName a = new FirstName();
        FirstName b = new FirstName();
        assertEquals(a.hashCode(), b.hashCode());
    }
}