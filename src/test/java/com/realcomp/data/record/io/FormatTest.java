package com.realcomp.data.record.io;

import java.util.HashMap;
import java.util.Map;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author krenfro
 */
public class FormatTest {

    public FormatTest() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}

    @Test
    public void contructorTest(){
        Format format = new Format();
        assertNotNull(format.attributes);
        assertNotNull(format.defaults);
        assertNotNull(format.getDefaults());
        
        assertNull(format.get("a"));
        
        Map<String,String> map = new HashMap<String,String>();
        map.put("a", "1");
        
        format = new Format(map);
        assertEquals("1", format.get("a"));
        
        
        format = new Format(format);
        assertEquals("1", format.get("a"));
        
        
    }
    
    @Test
    public void testDefaults(){
        Format format = new Format();
        assertNull(format.get("a"));
        
        format.putDefault("a", "2");
        assertEquals("2", format.get("a"));
        format.put("a","1");
        assertEquals("1", format.get("a"));
        format.remove("a");
        assertEquals("2", format.get("a"));
        
    }
    
    @Test
    public void testEquals(){
        Format a = new Format();
        Format b = new Format();
        
        assertEquals(a, b);
        assertEquals(b, a);
        assertEquals(a.hashCode(), b.hashCode());
        
        a.put("a", "1");
        assertFalse(a.equals(b));
        assertFalse(b.equals(a));
        
        b.put("a", "1");
        assertEquals(a, b);
        assertEquals(b, a);
        assertEquals(a.hashCode(), b.hashCode());
        
        a.putDefault("b", "1");
        assertFalse(a.equals(b));        
        assertFalse(b.equals(a));
        
        b.putDefault("b", "1");
        assertEquals(a, b);
        assertEquals(b, a);
        assertEquals(a.hashCode(), b.hashCode());
    }
    
    
}