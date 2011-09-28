package com.realcomp.data.conversion;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.text.DateFormat;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krenfro
 */
public class CurrentDateTest {

    public CurrentDateTest() {
    }


    /**
     * Test of convert method, of class CurrentDate.
     */
    @Test
    public void testConvert() throws Exception {

        CurrentDate converter = new CurrentDate();
        Calendar now = Calendar.getInstance();
        DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        assertEquals(formatter.format(now.getTime()), converter.convert(""));
        assertEquals(8, converter.convert(null).toString().length());

        converter.setFormat("yyyy");
        formatter = new SimpleDateFormat("yyyy");
        assertEquals(formatter.format(now.getTime()), converter.convert(""));
        assertEquals(4, converter.convert(null).toString().length());


        converter.setFormat("MM/dd/yyyy");
        formatter = new SimpleDateFormat("MM/dd/yyyy");
        assertEquals(formatter.format(now.getTime()), converter.convert(""));
        assertEquals(10, converter.convert(null).toString().length());

        converter.setFormat("M/d/yyyy");
        formatter = new SimpleDateFormat("M/d/yyyy");
        assertEquals(formatter.format(now.getTime()), converter.convert(""));
        assertTrue(converter.convert(null).toString().length() >= 8);
        assertTrue(converter.convert(null).toString().length() <= 10);

        converter.setFormat("short");
        formatter = DateFormat.getDateInstance(DateFormat.SHORT);
        assertEquals(formatter.format(now.getTime()), converter.convert(""));

        converter.setFormat("medium");
        formatter = DateFormat.getDateInstance(DateFormat.MEDIUM);
        assertEquals(formatter.format(now.getTime()), converter.convert(""));

        converter.setFormat("long");
        formatter = DateFormat.getDateInstance(DateFormat.LONG);
        assertEquals(formatter.format(now.getTime()), converter.convert(""));

        converter.setFormat("full");
        formatter = DateFormat.getDateInstance(DateFormat.FULL);
        assertEquals(formatter.format(now.getTime()), converter.convert(""));


    }

 
    /**
     * Test of setFormat method, of class CurrentDate.
     */
    @Test
    public void testSetFormat() {
        CurrentDate instance = new CurrentDate();
        instance.setFormat("");
        instance.setFormat("yyyyMMdd");
        instance.setFormat("MM/dd/yyyy");
        instance.setFormat("short");
        instance.setFormat("medium");
        instance.setFormat("long");
        instance.setFormat("full");

    }

}