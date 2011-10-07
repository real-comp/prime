package com.realcomp.data.record;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krenfro
 */
public class RecordValueResolverTest {
    
    public RecordValueResolverTest() {
    }
    
    public Record getRecord(){
        
        Record record = new Record();
        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
        
        Map<String,Object> owner = new HashMap<String,Object>();
        owner.put("stuff","1");
        owner.put("more","2");

        List<Map<String,Object>> addresses = new ArrayList<Map<String,Object>>();
        Map<String,Object> address = new HashMap<String,Object>();
        address.put("city","austin");
        addresses.add(address);
        owner.put("address", addresses);
        
        
        Map<String,Object> owner2 = new HashMap<String,Object>();
        owner2.put("stuff","a");
        owner2.put("more","b");
        
        addresses = new ArrayList<Map<String,Object>>();
        address = new HashMap<String,Object>();
        address.put("city","dallas");
        addresses.add(address);
        owner2.put("address", addresses);
        
        
        list.add(owner);
        list.add(owner2);
        

        record.put("name", "asdf");
        record.put("owner", list);
        
        return record;
        
    }

    @Test
    public void testConstructor(){
        new RecordValueResolver();
    }
    
    @Test
    public void testResolve() {
        
        
        Record record = getRecord();
        List<Object> result = RecordValueResolver.resolve(record.data, "name");
        assertEquals(1, result.size());
        assertEquals("asdf", result.get(0));
        
        result = RecordValueResolver.resolve(record.data, "owner");
        assertEquals(2, result.size());
        
        result = RecordValueResolver.resolve(record.data, "owner.stuff");
        assertEquals(2, result.size());
        assertEquals("1", result.get(0));
        assertEquals("a", result.get(1));
        
        result = RecordValueResolver.resolve(record.data, "owner.more");
        assertEquals(2, result.size());
        assertEquals("2", result.get(0));
        assertEquals("b", result.get(1));
        
        result = RecordValueResolver.resolve(record.data, "owner[0].more");
        assertEquals(1, result.size());
        assertEquals("2", result.get(0));
        
        result = RecordValueResolver.resolve(record.data, "owner[0].address.city");
        assertEquals(1, result.size());
        assertEquals("austin", result.get(0));
        
        
        result = RecordValueResolver.resolve(record.data, "owner[1].address.city");
        assertEquals(1, result.size());
        assertEquals("dallas", result.get(0));
        
        //there are 2 owner records, each with one address
        result = RecordValueResolver.resolve(record.data, "owner.address[0].city");
        assertEquals(2, result.size());
        assertEquals("austin", result.get(0));
        assertEquals("dallas", result.get(1));
        
        result = RecordValueResolver.resolve(record.data, "owner.address[1].city");
        assertEquals(0, result.size());
        
        
    }
}
