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

    @Test
    public void testEmptyString(){
        
        List<RecordKey> keys = RecordKey.parse("");
        assertTrue(keys.isEmpty());
    }
    
    @Test
    public void testPattern(){
        
        
        assertFalse(RecordKey.pattern.matcher("").matches());
        assertFalse(RecordKey.pattern.matcher("prop#imp").matches());        
        
        
        assertTrue(RecordKey.pattern.matcher("a").matches());
        assertTrue(RecordKey.pattern.matcher("AB").matches());
        assertTrue(RecordKey.pattern.matcher("AB1").matches());
        assertTrue(RecordKey.pattern.matcher("AB1_").matches());
        assertTrue(RecordKey.pattern.matcher("AB1_:").matches());
        assertTrue(RecordKey.pattern.matcher("AB1_:-").matches());
        assertTrue(RecordKey.pattern.matcher("A B ").matches());
        
        Matcher m = RecordKey.pattern.matcher("prop.imp");     
        assertTrue(m.find());
        assertEquals(2, m.groupCount());
        assertEquals("prop", m.group(1));
        assertEquals(null, m.group(2));
        assertTrue(m.find());
        assertEquals(2, m.groupCount());
        assertEquals("imp", m.group(1));
        assertEquals(null, m.group(2));
        assertFalse(m.find());
        
        
        m = RecordKey.pattern.matcher("prop[0].imp[1]");        
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
}
