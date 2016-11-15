package com.realcomp.prime.conversion;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 */
public class BooleanConverterTest extends SimpleConverterTest{

    public BooleanConverterTest(){
        converter = new BooleanConverter();
    }

    @Test
    public void testConvert() throws Exception{

        BooleanConverter a = new BooleanConverter();
        assertFalse(a.isCaseSensitive());

        assertTrue((Boolean) a.convert("true"));
        assertTrue((Boolean) a.convert("TRUE"));
        assertTrue((Boolean) a.convert("TrUe"));
        assertTrue((Boolean) a.convert("1"));
        assertTrue((Boolean) a.convert("YES"));
        assertTrue((Boolean) a.convert("yes"));
        assertTrue((Boolean) a.convert("y"));
        assertTrue((Boolean) a.convert("Y"));

        assertFalse((Boolean) a.convert("FALSE"));
        assertFalse((Boolean) a.convert("false"));
        assertFalse((Boolean) a.convert(""));
        assertFalse((Boolean) a.convert("anything"));
        assertFalse((Boolean) a.convert("0"));
        assertFalse((Boolean) a.convert("NO"));
        assertFalse((Boolean) a.convert("No"));
        assertFalse((Boolean) a.convert("N"));

        a.setCaseSensitive(true);
        assertFalse((Boolean) a.convert("false"));
        assertFalse((Boolean) a.convert("true"));
        assertFalse((Boolean) a.convert("TrUe"));
        assertFalse((Boolean) a.convert("yes"));
        assertFalse((Boolean) a.convert("y"));
        assertTrue((Boolean) a.convert("TRUE"));
        assertTrue((Boolean) a.convert("YES"));

        a.setFalsy("ASDF");
        assertFalse((Boolean) a.convert("ASDF"));

        a.setTruthy("*");
        assertTrue((Boolean) a.convert("STUFF"));


    }

    @Test
    public void testCopyOf(){
        BooleanConverter a = new BooleanConverter();
        a.setFalsy("KYLE");
        BooleanConverter b = a.copyOf();
        assertEquals(a, b);
        assertEquals("KYLE", b.getFalsy());
    }

    @Test
    public void testGetFalsy(){
        BooleanConverter a = new BooleanConverter();
        a.setFalsy("A");
        assertEquals("A", a.getFalsy());
    }

    @Test
    public void testGetTruthy(){
        BooleanConverter a = new BooleanConverter();
        a.setTruthy("A");
        assertEquals("A", a.getTruthy());
    }

    @Test
    public void testBadInputs(){

        BooleanConverter a = new BooleanConverter();
        try{
            a.setTruthy(null);
            fail("should have thrown IAE");
        }
        catch (IllegalArgumentException expected){
        }

        try{
            a.setTruthy("*,T");
            fail("should have thrown IAE");
        }
        catch (IllegalArgumentException expected){
        }

        try{
            a.setFalsy(null);
            fail("should have thrown IAE");
        }
        catch (IllegalArgumentException expected){
        }

        try{
            a.setFalsy("*,F");
            fail("should have thrown IAE");
        }
        catch (IllegalArgumentException expected){
        }


        try{
            a.setFalsy("ASDF");
            assertFalse((Boolean) a.convert("stuff"));
            fail("should have thrown ConversionException");
        }
        catch (ConversionException expected){
        }

    }

    @Test
    @Override
    public void testEquals(){

        super.testEquals();

        BooleanConverter a = new BooleanConverter();
        BooleanConverter b = new BooleanConverter();
        assertEquals(a, b);
        b.setTruthy("A");
        assertFalse(a.equals(b));
        b.setTruthy(a.getTruthy());

        assertEquals(a, b);
        b.setFalsy("A");
        assertFalse(a.equals(b));
        b.setFalsy(a.getFalsy());

        assertEquals(a, b);
        b.setCaseSensitive(true);
        assertFalse(a.equals(b));
        b.setCaseSensitive(false);
        assertEquals(a, b);
    }

    @Test
    public void testHashCode(){
        BooleanConverter a = new BooleanConverter();
        BooleanConverter b = new BooleanConverter();
        assertEquals(a.hashCode(), b.hashCode());
    }
}
