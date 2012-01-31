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
        if (value == null)
            throw new IllegalArgumentException("value is null");
        if (key == null)
            throw new IllegalArgumentException("key is null");
        
        return assemble(record.data, new RecordKey(key), value);
    }
    
     public static Object assemble(Record record, RecordKey key, Object value) throws RecordValueException{
        if (record == null)
            throw new IllegalArgumentException("record is null");
        if (value == null)
            throw new IllegalArgumentException("value is null");
        if (key == null)
            throw new IllegalArgumentException("key is null");
        
        return assemble(record.data, buildKeySequence(key), value);
    }
    
    public static Object assemble(Map<String,Object> map, String key, Object value) throws RecordValueException{
        if (map == null)
            throw new IllegalArgumentException("map is null");
        if (value == null)
            throw new IllegalArgumentException("value is null");
        if (key == null)
            throw new IllegalArgumentException("key is null");
        
        
        return assemble(map, new RecordKey(key), value);
    }
    
    
    public static Object assemble(Map<String,Object> map, RecordKey key, Object value) throws RecordValueException{
        if (map == null)
            throw new IllegalArgumentException("map is null");
        if (value == null)
            throw new IllegalArgumentException("value is null");
        if (key == null)
            throw new IllegalArgumentException("key is null");
        
        
        return assemble(map, buildKeySequence(key), value);
    }
    
    
    
    /**
     * Create new List that contains a new Map at key.index and add it to the map at key.name
     * 
     * @param map
     * @param key
     * @return 
     */
    protected static Map<String,Object> createMissingEntry(Map<String,Object> map, RecordKey key){        
        List list = new ArrayList();
        ensureCapacity(list, key.isIndexed() ? key.getIndex() + 1 : 1);
        Map<String,Object> entry = new HashMap<String,Object>();
        list.set(key.isIndexed() ? key.getIndex() : 0, entry);
        map.put(key.getName(), list);
        return entry;        
    }
    
    
    @SuppressWarnings({"unchecked", "unchecked"})
    protected static Object assemble(Map<String,Object> map, Stack<RecordKey> keys, Object value) throws RecordValueException{
        
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
     * Builds the sequence that keys need to be resolved from the root Map. 
     * The root key will be at the top of the stack.
     * 
     * @param key not null
     * @return 
     */
    protected static Stack<RecordKey> buildKeySequence(final RecordKey key){
        assert(key != null);
        Stack<RecordKey> sequence = new Stack<RecordKey>();
        RecordKey current = key;
        sequence.push(current);
        while (current.hasParent()){
            current = current.getParent();
            sequence.push(current);            
        }
        assert(!sequence.isEmpty());
        return sequence;
    }
    
    
    
    /**
     * Optionally expand the list to the specified capacity.
     * If the list is already at the capacity, this is a no-op.
     * 
     * @param list
     * @param capacity 
     */
    @SuppressWarnings("unchecked")
    protected static void ensureCapacity(List list, int capacity){
        if (list == null)
            list = new ArrayList();
        int diff = capacity - list.size();
        for (int x = 0; x < diff; x++){
            list.add(null);
        }
    }  
    
    /*
    
    private static void assemble(Map<String,Object> data, Stack<RecordKey> keys, List values)
        throws RecordValueException{
        
        assert(data != null);
        assert(values != null);
        assert(keys != null);
        
        if (keys.isEmpty()){
            return; //done
        }
        else if (keys.size() > 1){            
            String parentKey = RecordKey.compose(RecordKey.getParent(keys));
            RecordKey childKey = keys.get(keys.size() - 1);
            List<Object> existing = null;
            
            try{
                existing = RecordValueResolver.resolve(data, parentKey);
            }
            catch(ClassCastException wrongClass){
                throw new RecordValueException(
                        String.format(
                            "Non-standard Record structure encountered.  "
                             + "Unable to assemble the value into the Record because I am expecting the "
                             + "object at [%s] to be a Map.  "
                             + "Note: This is a valid Record, but I cannot assemble values into it reliably.", childKey),
                        wrongClass);
            }
            
            if (existing.isEmpty()){
                //there is no existing value for they keys, so I can reliably add the values, creating a new map 
                //to hold each value.
                for (int x = 0; x < values.size(); x++){
                    RecordKey secondToLastKey = keys.get(keys.size() - 2);
                    secondToLastKey.setIndex(x);                   
                    assemble(data, keys, values.get(x));
                }
            }
            else if (existing.size() != values.size()){
                throw new RecordValueException(
                        String.format( 
                            "Unable to assemble [%s] values into the Record because there are [%s] Maps at key [%s].  "
                            + "The number of values must match the number of entries at key [%s] for this type of "
                            + "assembly to be reliable.",
                            new Object[]{values.size(), existing.size(), parentKey, parentKey}));
            }
            else{
                for (int x = 0; x < values.size(); x++){
                    
                    if (DataType.getDataType(existing.get(x)) != DataType.MAP){
                        throw new RecordValueException(
                                String.format(
                                    "Non-standard Record structure encountered.  "
                                     + "Unable to assemble value [%s] into the Record because I am expecting the "
                                     + "object at [%] to be a Map, but instead it is a(n) [%s].  "
                                     + "Note: This is a valid Record, but I cannot assemble values into it reliably.",
                                    new Object[]{values.get(x), childKey, existing.getClass().getName()}));
                    }
                    
                    Map map = (Map) existing.get(x);
                    map.put(childKey.getName(), values.get(x));
                }
            }
        }
        else if (keys.size() == 1 && values.size() == 1){
            data.put(keys.get(0).getName(), values.get(0));
        }
        else{            
            data.put(keys.get(0).getName(), values);
        }
    }
 
    */
    
}
