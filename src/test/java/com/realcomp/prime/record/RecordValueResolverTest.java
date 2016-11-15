package com.realcomp.prime.record;

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
public class RecordValueResolverTest{

    public RecordValueResolverTest(){
    }

    public Record getRecord(){

        Record record = new Record();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        Map<String, Object> owner = new HashMap<String, Object>();
        owner.put("stuff", "1");
        owner.put("more", "2");

        List<Map<String, Object>> addresses = new ArrayList<Map<String, Object>>();
        Map<String, Object> address = new HashMap<String, Object>();
        address.put("city", "austin");
        addresses.add(address);
        owner.put("address", addresses);


        Map<String, Object> owner2 = new HashMap<String, Object>();
        owner2.put("stuff", "a");
        owner2.put("more", "b");

        addresses = new ArrayList<Map<String, Object>>();
        address = new HashMap<String, Object>();
        address.put("city", "dallas");
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
    public void testResolve(){

        Record record = getRecord();
        Object result = RecordValueResolver.resolve(record, "name");
        assertEquals("asdf", result);

        result = RecordValueResolver.resolve(record, "owner");
        assertEquals(2, ((List) result).size());

        try{
            result = RecordValueResolver.resolve(record, "owner.stuff");
            fail("should have thrown RecordKeyException");
        }
        catch (RecordKeyException expected){
        }

        try{
            result = RecordValueResolver.resolve(record, "owner.more");
            fail("should have thrown RecordKeyException");
        }
        catch (RecordKeyException expected){
        }


        result = RecordValueResolver.resolve(record, "owner[0].more");
        assertEquals("2", result);

        result = RecordValueResolver.resolve(record, "owner[0].address.city");
        assertEquals("austin", result);


        result = RecordValueResolver.resolve(record, "owner[1].address.city");
        assertEquals("dallas", result);



    }
}
