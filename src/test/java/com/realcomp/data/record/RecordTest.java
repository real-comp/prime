package com.realcomp.data.record;

import java.util.Map.Entry;
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
public class RecordTest{

    public RecordTest(){
    }

    @Test
    public void testResolve(){
        Record record = new Record();
        List<Map<String, Object>> list = new ArrayList<>();

        Map<String, Object> owner = new HashMap<>();
        owner.put("stuff", "1");
        owner.put("more", "2");
        list.add(owner);

        record.put("name", "asdf");
        record.put("owner", list);

        List<Object> result = record.getAll("owner");
        assertTrue(result.equals(list));

        assertEquals("1", record.getAll("owner.stuff").get(0));
        assertEquals("2", record.getAll("owner.more").get(0));

        assertEquals(list, record.getAll("owner"));
        assertEquals("asdf", record.getAll("name").get(0));


        Map<String, Object> owner2 = new HashMap<>();
        owner2.put("stuff", "a");
        owner2.put("more", "b");
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
    public void testIndexedResolve(){
        Record record = new Record();
        List<Map<String, Object>> list = new ArrayList<>();

        Map<String, Object> owner = new HashMap<>();
        owner.put("stuff", "1");
        owner.put("more", "2");
        list.add(owner);

        Map<String, Object> owner2 = new HashMap<>();
        owner2.put("stuff", "a");
        owner2.put("more", "b");
        list.add(owner2);

        record.put("name", "asdf");
        record.put("owner", list);

        assertEquals(0, record.getAll("owner[2]").size());

        assertEquals("1", record.getAll("owner[0].stuff").get(0));
        assertEquals("a", record.getAll("owner[1].stuff").get(0));
        assertEquals("2", record.getAll("owner[0].more").get(0));
        assertEquals("b", record.getAll("owner[1].more").get(0));

        try{
            record.getAll("owner[0].stuff[0]");
            fail("should have thrown RecordKeyException. stuff is not a list");
        }
        catch (RecordKeyException ok){
        }


    }

    @Test
    public void testNestedMapResolution() throws RecordKeyException{

        Record record = new Record();

        Map<String, Object> child = new HashMap<>();
        child.put("first", "Kyle");
        child.put("last", "Renfro");
        record.put("name", child);
        record.put("id", 12345);

        assertEquals(12345, record.get("id"));

        Map<String, Object> result = (Map) record.get("name");
        assertEquals(child, result);

        assertEquals("Kyle", record.get("name.first"));
        assertEquals("Renfro", record.get("name.last"));
    }

    @Test
    public void testComplexRecord() throws RecordKeyException{

        Record record = new Record();
        Map<String, Object> owner = new HashMap<>();
        owner.put("first", "kyle");
        owner.put("last", "renfro");
        record.put("id", 12345);
        List<Map> owners = new ArrayList<>();
        owners.add(owner);

        owner = new HashMap<>();
        owner.put("first", "brandon");
        owner.put("last", "goering");
        owners.add(owner);

        record.put("owners", owners);

        assertEquals(12345, record.get("id"));
        assertEquals(owner, record.get("owners[1]"));
        assertEquals("goering", record.get("owners[1].last"));

        boolean found = false;
        for (Entry<String, Object> entry : record.entrySet()){
            if (entry.getKey().equals("owners[0].last")){
                found = true;
            }
        }
        assertTrue(found);

        for (Entry<String, Object> entry : record.entrySet()){
            owner = new HashMap<>();
            owner.put("first", "kyle");
            owner.put("last", "renfro");
            record.put("id", 12345);
            if (entry.getKey().equals("owners[0]")){
                assertEquals(owner, entry.getValue());
            }
        }
    }

    @Test
    public void testIndexedFields() throws RecordKeyException{

        Record record = new Record();
        record.put("id", 12345);

        Map<String, Object> owner = new HashMap<>();
        owner.put("first", "kyle");
        owner.put("last", "renfro");
        record.put("owners[0]", owner);

        
        assertTrue(record.get("owners") instanceof List);
        assertEquals(owner, record.get("owners[0]"));
        assertNull(record.get("owners[1]"));

        owner = new HashMap<>();
        owner.put("first", "brandon");
        owner.put("last", "goering");
        record.put("owners[1]", owner);

        assertEquals(owner, record.get("owners[1]"));
        assertEquals("goering", record.get("owners[1].last"));
        assertEquals("renfro", record.get("owners[0].last"));

    }


    @Test
    public void testSingleEntryList() throws RecordKeyException{

        Record record = new Record();
        record.put("id", 12345);
        List<Record> list = new ArrayList<>();
        Record entry = new Record();
        entry.put("first", "kyle");
        list.add(entry);
        record.put("list", list);

        assertTrue(record.get("list") instanceof List);
        assertEquals(1, ((List) record.get("list")).size());
    }



    @Test
    public void testSimpleMap(){

        Record record = new Record();
        List owners = new ArrayList();
        Map<String, Object> owner = new HashMap<>();
        owner.put("last", "RENFRO");
        owners.add(owner);
        record.put("owners", owners);

        owner = new HashMap<>();
        owner.put("last", "VRBA");
        owners.add(owner);

        //This is a record, so composite key resolution works.
        assertEquals("RENFRO", record.get("owners[0].last"));
        assertEquals("VRBA", record.get("owners[1].last"));

        //Flatten the record into a dumb map. Composite key resolution should still work.
        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<String, Object> entry : record.entrySet()){
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value != null){
                map.put(key, value);
            }
        }
        assertEquals("RENFRO", map.get("owners[0].last"));
        assertEquals("VRBA", record.get("owners[1].last"));

        //Turn the flattened map back into a Record.
        record = new Record(map);
        assertEquals("RENFRO", record.get("owners[0].last"));
        assertEquals("VRBA", record.get("owners[1].last"));

        //Treat the map as a simple map.  Composite key resolution will not work.
        map = record.asSimpleMap();
        assertNull(map.get("owners[0].last"));
        assertNull(map.get("owners[1].last"));



    }
}
