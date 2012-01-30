package com.realcomp.data.record;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        
        List<Object> result = record.getAll("owner");
        assertTrue(result.equals(list));
        
        assertEquals("1", record.getAll("owner.stuff").get(0));
        assertEquals("2", record.getAll("owner.more").get(0));
        
        assertEquals(list, record.getAll("owner"));
        assertEquals("asdf", record.getAll("name").get(0));
        
        
        Map<String,Object> owner2 = new HashMap<String,Object>();
        owner2.put("stuff","a");
        owner2.put("more","b");
        list.add(owner2);
        
        assertEquals(2, record.getAll("owner.stuff").size());
        assertEquals("1", record.getAll("owner.stuff").get(0));
        assertEquals("a", record.getAll("owner.stuff").get(1));
        assertEquals("2", record.getAll("owner.more").get(0));
        assertEquals("b", record.getAll("owner.more").get(1));
        
        assertEquals(list, record.getAll("owner"));
        assertEquals("asdf", record.getAll("name").get(0));
        
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
        
        assertEquals(0, record.getAll("owner[2]").size());
        
        assertEquals("1", record.getAll("owner[0].stuff").get(0));
        assertEquals("a", record.getAll("owner[1].stuff").get(0));
        assertEquals("2", record.getAll("owner[0].more").get(0));
        assertEquals("b", record.getAll("owner[1].more").get(0));
        
        assertEquals("1", record.getAll("owner[0].stuff[0]").get(0));
        assertEquals("a", record.getAll("owner[1].stuff[0]").get(0));
        assertEquals("2", record.getAll("owner[0].more[0]").get(0));
        assertEquals("b", record.getAll("owner[1].more[0]").get(0));
        
        
    }
    
    
    
    @Test
    public void testNestedMapResolution() throws RecordKeyException{
        
        Record record = new Record();
        
        Map<String,Object> child = new HashMap<String,Object>();
        child.put("first", "Kyle");
        child.put("last", "Renfro");
        record.put("name", child);
        record.put("id", 12345);
        
        assertEquals(12345, record.get("id"));
        
        Map<String,Object> result = (Map) record.get("name");
        assertEquals(child, result);
        
        assertEquals("Kyle", record.get("name.first"));
        assertEquals("Renfro", record.get("name.last"));
    }
    
}
