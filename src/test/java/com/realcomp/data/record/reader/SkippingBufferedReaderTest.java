/*
 */

package com.realcomp.data.record.reader;

import com.realcomp.data.record.io.SkippingBufferedReader;
import java.io.StringReader;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krenfro
 */
public class SkippingBufferedReaderTest {

    public SkippingBufferedReaderTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    protected String getData(int numLines){

        StringBuilder s = new StringBuilder();
        boolean needDelimiter = false;

        for (int x = 0; x < numLines; x++){
            if (needDelimiter)
                s.append("\n");
            needDelimiter = true;
            s.append(x + 1);
        }
        
        return s.toString();
    }

    /**
     * Test of setLeading method, of class SkippingBufferedReader.
     */
    @Test
    public void testSetLeading() {
        SkippingBufferedReader instance = new SkippingBufferedReader(new StringReader(getData(0)));
        assertEquals(0, instance.getSkipLeading());
        instance.setSkipLeading(0);
        assertEquals(0, instance.getSkipLeading());
        instance.setSkipLeading(1);
        assertEquals(1, instance.getSkipLeading());
        try{
            instance.setSkipLeading(-1);
            fail("should have thrown IllegalArgumentException");
        }
        catch(IllegalArgumentException expected){}
        
    }

    /**
     * Test of setTrailing method, of class SkippingBufferedReader.
     */
    @Test
    public void testSetTrailing() {
        SkippingBufferedReader instance = new SkippingBufferedReader(new StringReader(getData(0)));
        assertEquals(0, instance.getSkipTrailing());
        instance.setSkipTrailing(0);
        assertEquals(0, instance.getSkipTrailing());
        instance.setSkipTrailing(1);
        assertEquals(1, instance.getSkipTrailing());
        try{
            instance.setSkipTrailing(-1);
            fail("should have thrown IllegalArgumentException");
        }
        catch(IllegalArgumentException expected){}
    }

    
    /**
     * Test of readLine method, of class SkippingBufferedReader.
     */
    @Test
    public void testReadLine() throws Exception {

        SkippingBufferedReader instance = new SkippingBufferedReader(new StringReader(getData(0)));
        assertEquals(0, instance.getSkipLeading());
        assertEquals(0, instance.getSkipTrailing());
        assertNull(instance.readLine());

        instance = new SkippingBufferedReader(new StringReader(getData(1)));
        assertEquals("1", instance.readLine());
        assertNull(instance.readLine());

        instance = new SkippingBufferedReader(new StringReader(getData(2)));
        assertEquals("1", instance.readLine());
        assertEquals("2", instance.readLine());
        assertNull(instance.readLine());

        instance = new SkippingBufferedReader(new StringReader(getData(2)));
        instance.setSkipLeading(1);
        assertEquals("2", instance.readLine());
        assertNull(instance.readLine());

        instance = new SkippingBufferedReader(new StringReader(getData(3)));
        instance.setSkipLeading(1);
        assertEquals("2", instance.readLine());
        assertEquals("3", instance.readLine());
        assertNull(instance.readLine());

        instance = new SkippingBufferedReader(new StringReader(getData(2)));
        instance.setSkipTrailing(1);
        assertEquals("1", instance.readLine());
        assertNull(instance.readLine());

        instance = new SkippingBufferedReader(new StringReader(getData(3)));
        instance.setSkipTrailing(1);
        assertEquals("1", instance.readLine());
        assertEquals("2", instance.readLine());
        assertNull(instance.readLine());

        instance = new SkippingBufferedReader(new StringReader(getData(3)));
        instance.setSkipLeading(1);
        instance.setSkipTrailing(1);
        assertEquals("2", instance.readLine());
        assertNull(instance.readLine());

        instance = new SkippingBufferedReader(new StringReader(getData(5)));
        instance.setSkipTrailing(5);
        assertNull(instance.readLine());

        instance = new SkippingBufferedReader(new StringReader(getData(5)));
        instance.setSkipTrailing(4);
        assertEquals("1", instance.readLine());
        assertNull(instance.readLine());


        instance = new SkippingBufferedReader(new StringReader(getData(10)));
        instance.setSkipLeading(4);
        instance.setSkipTrailing(4);
        assertEquals("5", instance.readLine());
        assertEquals("6", instance.readLine());
        assertNull(instance.readLine());
    }

    /**
     * Test of read method, of class SkippingBufferedReader.
     */
    @Test
    public void testRead() throws Exception {
        
        try{
            SkippingBufferedReader instance = new SkippingBufferedReader(new StringReader(getData(1)));
            instance.read(null, 0, 0);
            fail("Should have throws UnsupportedOperationException");
        }
        catch(UnsupportedOperationException expected){}
        
    }

}