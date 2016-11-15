package com.realcomp.prime.record;

import com.realcomp.prime.DataType;
import java.util.*;

/**
 * Assembles a value into a Map, creating any necessary parent elements specified by the key.
 *
 * @see RecordValueResolver
 */
public class RecordValueAssembler{

    private static final Map<String, Stack<RecordKey>> keyCache = new HashMap<>();

    public static Object assemble(Map<String, Object> map, String key, Object value){
        if (map == null){
            throw new IllegalArgumentException("map is null");
        }
        if (key == null){
            throw new IllegalArgumentException("key is null");
        }

        Object previous;
        Stack<RecordKey> recordKeySequence = keyCache.get(key);
        if (recordKeySequence == null){
            recordKeySequence = new RecordKey(key).buildKeySequence();
            keyCache.put(key, recordKeySequence);
        }

        if (recordKeySequence.size() == 1){
            //optimization - most processing will be non-indexed/non-composite
            RecordKey recordKey = recordKeySequence.peek();
            if (recordKey.isIndexed()){
                previous = assemble(map, (Stack<RecordKey>) recordKeySequence.clone(), value);
            }
            else{
                previous = map.put(recordKey.getName(), value);
            }
        }
        else{
            previous = assemble(map, (Stack<RecordKey>) recordKeySequence.clone(), value);
        }

        return previous;

    }

    /**
     * Create new List that contains a new Map at key.index and add it to the map at key.name
     *
     * @param map
     * @param key
     * @return
     */
    static Map<String, Object> createMissingEntry(Map<String, Object> map, RecordKey key){
        List list = new ArrayList();
        ensureCapacity(list, key.isIndexed() ? key.getIndex() + 1 : 1);
        Map<String, Object> entry = new HashMap<>();
        list.set(key.isIndexed() ? key.getIndex() : 0, entry);
        map.put(key.getName(), list);
        return entry;
    }

    @SuppressWarnings({"unchecked", "unchecked"})
    static Object assemble(Map<String, Object> map, Stack<RecordKey> keys, Object value){

        Object previous = null;
        if (!keys.isEmpty()){
            RecordKey key = keys.pop();
            Object current = map.get(key.getName());

            if (keys.isEmpty()){
                //this is the last (target) key. Set the value and return the previous result
                if (key.isIndexed()){
                    //indexed fields need to live in a list. Make sure one is available.
                    List list = null;
                    if (current == null){
                        list = new ArrayList();
                        ensureCapacity(list, key.getIndex() + 1);
                        previous = list.set(key.getIndex(), value);
                        map.put(key.getName(), list);
                    }
                    else if (DataType.getDataType(current) == DataType.LIST){
                        list = (List) current;
                        ensureCapacity(list, key.getIndex() + 1);
                        previous = list.set(key.getIndex(), value);
                    }
                    else{
                        throw new RecordKeyException("todo");
                    }
                }
                else{
                    previous = map.put(key.getName(), value);
                }
            }
            else if (current == null){
                //there is no value for the key. There is at least one more key in the sequence.
                //assume the default Map->List record structure is desired, create missing entries, and recurse
                current = createMissingEntry(map, key);
                previous = assemble((Map<String, Object>) current, keys, value); //recurse
            }
            else{
                //current has a value. I can handle this if current is a list or a map.
                DataType type = DataType.getDataType(current);
                if (type == DataType.LIST){
                    List list = (List) current;
                    if (list.size() <= 1 || key.isIndexed()){
                        ensureCapacity(list, key.isIndexed() ? key.getIndex() + 1 : 1);
                        current = (Map<String, Object>) list.get(key.isIndexed() ? key.getIndex() : 0);
                        if (current == null){
                            current = new HashMap<>();
                            list.set(key.isIndexed() ? key.getIndex() : 0, current);
                        }
                        previous = assemble((Map<String, Object>) current, keys, value); //recurse
                    }
                    else{
                        throw new RecordValueException(
                                "There is more than one value in the Record for [" + key + "]  "
                                + "Remove ambiguity by adding list indexes to key. (e.g., 'prop.imp_info[1].stuff').");
                    }
                }
                else if (type == DataType.MAP){
                    previous = assemble((Map<String, Object>) current, keys, value); //recurse
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
     * Optionally expand the list to the specified capacity. If the list is already at the capacity, this is a no-op.
     *
     * @param list
     * @param capacity
     */
    @SuppressWarnings("unchecked")
    static void ensureCapacity(List list, int capacity){
        if (list == null){
            list = new ArrayList();
        }
        int diff = capacity - list.size();
        for (int x = 0; x < diff; x++){
            list.add(null);
        }
    }
}
