package com.realcomp.data.record;

import com.realcomp.data.DataType;
import java.util.*;

/**
 * Assembles a value into a Map, creating any necessary parent elements specified by the key.
 * 
 * @see RecordValueResolver
 * @author krenfro
 */
public class RecordValueAssembler {
    
     public static Object assemble(Record record, String key, Object value) throws RecordValueException{
        if (record == null)
            throw new IllegalArgumentException("record is null");
        if (key == null)
            throw new IllegalArgumentException("key is null");
        
        return assemble(record.data, new RecordKey(key), value);
    }
    
    public static Object assemble(Map<String,Object> map, String key, Object value) throws RecordValueException{
        if (map == null)
            throw new IllegalArgumentException("map is null");
        if (key == null)
            throw new IllegalArgumentException("key is null");
        
        
        return assemble(map, new RecordKey(key), value);
    }
    
    static Object assemble(Record record, RecordKey key, Object value) throws RecordValueException{
        if (record == null)
            throw new IllegalArgumentException("record is null");
        if (key == null)
            throw new IllegalArgumentException("key is null");
        
        return assemble(record.data, key.buildKeySequence(), value);
    }
    
    static Object assemble(Map<String,Object> map, RecordKey key, Object value) throws RecordValueException{
        if (map == null)
            throw new IllegalArgumentException("map is null");
        if (key == null)
            throw new IllegalArgumentException("key is null");
                
        return assemble(map, key.buildKeySequence(), value);
    }
    
    
    
    /**
     * Create new List that contains a new Map at key.index and add it to the map at key.name
     * 
     * @param map
     * @param key
     * @return 
     */
    static Map<String,Object> createMissingEntry(Map<String,Object> map, RecordKey key){        
        List list = new ArrayList();
        ensureCapacity(list, key.isIndexed() ? key.getIndex() + 1 : 1);
        Map<String,Object> entry = new HashMap<String,Object>();
        list.set(key.isIndexed() ? key.getIndex() : 0, entry);
        map.put(key.getName(), list);
        return entry;        
    }
    
    
    @SuppressWarnings({"unchecked", "unchecked"})
    static Object assemble(Map<String,Object> map, Stack<RecordKey> keys, Object value) throws RecordValueException{
        
        Object previous = null;
        if (!keys.isEmpty()){
            RecordKey key = keys.pop();
            Object current = map.get(key.getName());
            
            if (keys.isEmpty()){
                //this is the last (target) key. Set the value and return the previous result
                previous = map.put(key.getName(), value);
            }
            else if (current == null){
                //there is no value for the key. There is at least one more key in the sequence.
                //assume the default Map->List record structure is desired, create missing entries, and recurse
                current = createMissingEntry(map, key);
                previous = assemble((Map<String,Object>) current, keys, value); //recurse
            }
            else{
                //current has a value. I can handle this if current is a list or a map.
                DataType type = DataType.getDataType(current);
                if (type == DataType.LIST){
                    List list = (List) current;
                    if (list.size() <= 1 || key.isIndexed()){
                        ensureCapacity(list, key.isIndexed() ? key.getIndex() + 1 : 1);
                        current = (Map<String,Object>) list.get(key.isIndexed() ? key.getIndex() : 0);
                        if (current == null){
                            current = new HashMap<String,Object>();
                            list.set(key.isIndexed() ? key.getIndex() : 0, current);
                        }
                        previous = assemble((Map<String,Object>) current, keys, value); //recurse
                    }
                    else{
                        throw new RecordValueException(
                                "There is more than one value in the Record for [" + key + "]  "
                                + "Remove ambiguity by adding list indexes to key. (e.g., 'prop.imp_info[1].stuff').");
                    }
                }
                else if (type == DataType.MAP){
                    previous = assemble((Map<String,Object>) current, keys, value); //recurse
                }
                else{
                    throw new RecordValueException(
                            String.format("The value at key [%s] is a [%s] not a List as expected.",
                                        new Object[]{key, type}));
                }
            }
        }
        
        return previous;
    }
        
    
    
    /**
     * Optionally expand the list to the specified capacity.
     * If the list is already at the capacity, this is a no-op.
     * 
     * @param list
     * @param capacity 
     */
    @SuppressWarnings("unchecked")
    static void ensureCapacity(List list, int capacity){
        if (list == null)
            list = new ArrayList();
        int diff = capacity - list.size();
        for (int x = 0; x < diff; x++){
            list.add(null);
        }
    }  
    
}
