package com.realcomp.data.record;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krenfro
 */
public class RecordTest {
    
    public RecordTest() {
    }

    
    @Test
    public void testResolve() {
        Record record = new Record();
        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
        
        Map<String,Object> owner = new HashMap<String,Object>();
        owner.put("stuff","1");
        owner.put("more","2");
        list.add(owner);
                
        record.put("name", "asdf");
        record.put("owner", list);
        
        List<Object> result = record.resolve("owner");
        assertTrue(result.equals(list));
        
        assertEquals("1", record.resolve("owner.stuff").get(0));
        assertEquals("2", record.resolve("owner.more").get(0));
        
        assertEquals(list, record.resolve("owner"));
        assertEquals("asdf", record.resolve("name").get(0));
        
        
        Map<String,Object> owner2 = new HashMap<String,Object>();
        owner2.put("stuff","a");
        owner2.put("more","b");
        list.add(owner2);
        
        assertEquals(2, record.resolve("owner.stuff").size());
        assertEquals("1", record.resolve("owner.stuff").get(0));
        assertEquals("a", record.resolve("owner.stuff").get(1));
        assertEquals("2", record.resolve("owner.more").get(0));
        assertEquals("b", record.resolve("owner.more").get(1));
        
        assertEquals(list, record.resolve("owner"));
        assertEquals("asdf", record.resolve("name").get(0));
        
    }
    
    
    @Test
    public void testIndexedResolve() {
        Record record = new Record();
        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
        
        Map<String,Object> owner = new HashMap<String,Object>();
        owner.put("stuff","1");
        owner.put("more","2");
        list.add(owner);
                
        Map<String,Object> owner2 = new HashMap<String,Object>();
        owner2.put("stuff","a");
        owner2.put("more","b");
        list.add(owner2);
        
        record.put("name", "asdf");
        record.put("owner", list);
        
        assertEquals(0, record.resolve("owner[2]").size());
        
        assertEquals("1", record.resolve("owner[0].stuff").get(0));
        assertEquals("a", record.resolve("owner[1].stuff").get(0));
        assertEquals("2", record.resolve("owner[0].more").get(0));
        assertEquals("b", record.resolve("owner[1].more").get(0));
        
        assertEquals("1", record.resolve("owner[0].stuff[0]").get(0));
        assertEquals("a", record.resolve("owner[1].stuff[0]").get(0));
        assertEquals("2", record.resolve("owner[0].more[0]").get(0));
        assertEquals("b", record.resolve("owner[1].more[0]").get(0));
        
        
    }
}
