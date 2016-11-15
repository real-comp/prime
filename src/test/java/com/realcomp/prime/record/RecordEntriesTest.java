package com.realcomp.prime.record;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.Map.Entry;
import java.util.Map;
import java.util.HashMap;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 */
public class RecordEntriesTest{

    public RecordEntriesTest(){
    }

    @Test
    public void testEntries(){

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("s1", "1");
        map.put("s2", "2");
        map.put("i1", 1);

        Set<Entry<String, Object>> entries = RecordEntries.getEntries(map);
        assertEquals(3, entries.size());

        List<String> list = new ArrayList<String>();
        map.put("list", list);

        entries = RecordEntries.getEntries(map);
        assertEquals(4, entries.size());

        Map<String, Object> entriesAsMap = asMap(entries);
        assertEquals("1", entriesAsMap.get("s1"));
        assertEquals("2", entriesAsMap.get("s2"));
        assertEquals(1, entriesAsMap.get("i1"));
        assertEquals(list, entriesAsMap.get("list"));


        //this test is important, I added an entry to the list, but the total number of entries in the map should
        // not change.
        list.add("listEntry1");
        entries = RecordEntries.getEntries(map);
        assertEquals(4, entries.size());
        assertEquals(list, entriesAsMap.get("list"));

        entriesAsMap = asMap(entries);
        assertEquals("1", entriesAsMap.get("s1"));
        assertEquals("2", entriesAsMap.get("s2"));
        assertEquals(1, entriesAsMap.get("i1"));
        assertEquals(list, entriesAsMap.get("list"));
        assertEquals("listEntry1", ((List) entriesAsMap.get("list")).get(0));
        assertNull(entriesAsMap.get("list[0]"));  //these are entries, not a record. this should not resolve.


        //add one more entry to the list, the number of entries should increase.
        list.add("listEntry2");
        entries = RecordEntries.getEntries(map);
        assertEquals(4, entries.size()); //number of entries should not change
        assertEquals(list, entriesAsMap.get("list"));
        assertEquals("listEntry1", ((List) entriesAsMap.get("list")).get(0));
        assertEquals("listEntry2", ((List) entriesAsMap.get("list")).get(1));
        assertNull(entriesAsMap.get("list[0]"));  //these are entries, not a record. this should not resolve.
        assertNull(entriesAsMap.get("list[1]"));  //these are entries, not a record. this should not resolve.

        //add a map
        Map<String, Object> nestedMap = new HashMap<String, Object>();
        nestedMap.put("s1", "1");
        nestedMap.put("s2", "2");
        nestedMap.put("i1", 1);
        List<Object> nestedList = new ArrayList<Object>();
        nestedList.add("listEntry1");
        nestedList.add("listEntry2");
        nestedMap.put("list", nestedList);

        map.put("map", nestedMap);
        entries = RecordEntries.getEntries(map);
        assertEquals(8, entries.size());

        entriesAsMap = asMap(entries);
        assertEquals("1", entriesAsMap.get("s1"));
        assertEquals("2", entriesAsMap.get("s2"));
        assertEquals(1, entriesAsMap.get("i1"));
        assertEquals(list, entriesAsMap.get("list"));
        //test nested map
        assertEquals("1", entriesAsMap.get("map.s1"));
        assertEquals("2", entriesAsMap.get("map.s2"));
        assertEquals(1, entriesAsMap.get("map.i1"));
        assertEquals(nestedList, entriesAsMap.get("map.list"));

    }

    private static Map<String, Object> asMap(Set<Entry<String, Object>> entries){

        Map<String, Object> flattened = new HashMap<String, Object>();
        for (Entry<String, Object> entry : entries){
            flattened.put(entry.getKey(), entry.getValue());
        }
        return flattened;
    }
}
