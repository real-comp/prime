/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcomp.data.record;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.List;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krenfro
 */
public class RecordKeyTest {
    
    public RecordKeyTest() {
    }

    @Test(expected=RecordKeyException.class)
    public void testEmptyString(){
        
        RecordKey key = new RecordKey("");
        assertFalse(key.hasParent());
        assertFalse(key.isIndexed());
    }
    
    @Test
    public void testPattern(){
        
        
        assertFalse(RecordKey.parsingPattern.matcher("").matches());
        assertFalse(RecordKey.parsingPattern.matcher("prop#imp").matches());        
        
        
        assertTrue(RecordKey.parsingPattern.matcher("a").matches());
        assertTrue(RecordKey.parsingPattern.matcher("AB").matches());
        assertTrue(RecordKey.parsingPattern.matcher("AB1").matches());
        assertTrue(RecordKey.parsingPattern.matcher("AB1_").matches());
        assertTrue(RecordKey.parsingPattern.matcher("AB1_:").matches());
        assertTrue(RecordKey.parsingPattern.matcher("AB1_:-").matches());
        assertTrue(RecordKey.parsingPattern.matcher("A B ").matches());
        
        Matcher m = RecordKey.parsingPattern.matcher("prop.imp");     
        assertTrue(m.find());
        assertEquals(2, m.groupCount());
        assertEquals("prop", m.group(1));
        assertEquals(null, m.group(2));
        assertTrue(m.find());
        assertEquals(2, m.groupCount());
        assertEquals("imp", m.group(1));
        assertEquals(null, m.group(2));
        assertFalse(m.find());
        
        
        m = RecordKey.parsingPattern.matcher("prop[0].imp[1]");        
        assertTrue(m.find());
        assertEquals(2, m.groupCount());
        assertEquals("prop", m.group(1));
        assertEquals("0", m.group(2));
        assertTrue(m.find());
        assertEquals(2, m.groupCount());
        assertEquals("imp", m.group(1));
        assertEquals("1", m.group(2));
        assertFalse(m.find());
        
    }
    
    @Test
    public void testKey(){
        
        new RecordKey("improvements[1].value");
        new RecordKey("improvements[10].value");
        new RecordKey("improvements[18].value");
        
        
    }
    
}
