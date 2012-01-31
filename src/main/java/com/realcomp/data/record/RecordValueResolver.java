package com.realcomp.data.record;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Finds a specified value in a Record.  A composite RecordKey can reference a value
 * arbitrarily deep within a Record.  This utility dives into the Record to find the value.
 * 
 * @see RecordValueAssembler
 * @author krenfro
 */
public class RecordValueResolver {
    
    
    public static List<Object> resolve(Record record, String key){
        
        return resolve(record.data, new RecordKey(key));
    }
    
    public static List<Object> resolve(Record record, RecordKey key){
        
        return resolve(record.data, key);
    }
    
    public static List<Object> resolve(Map<String,Object> map, String key){
        
        return resolve(map, new RecordKey(key));
    }    
    
    public static List<Object> resolve(Map<String,Object> map, RecordKey key){
        
        return resolve(map, buildKeySequence(key));
    }
    
    /**
     * Builds the sequence that keys need to be resolved from the root Map. 
     * The root key will be at the top of the stack, and <i>key</i> will be at the bottom.
     * 
     * @param key not null
     * @return 
     */
    private static Stack<RecordKey> buildKeySequence(final RecordKey key){
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
    
    @SuppressWarnings("unchecked")
    private static List<Object> resolve(Map<String,Object> map, Stack<RecordKey> sequence){
        List<Object> result = null;
        
        if (!sequence.isEmpty()){
            RecordKey key = sequence.pop();
            Object value = map.get(key.getName());
            if (value != null){
                result = new ArrayList<Object>();
                if (List.class.isAssignableFrom(value.getClass())){                    
                    List<Map<String,Object>> list = (List<Map<String,Object>>) value;
                    if (key.isIndexed()){
                        if (list.size() > key.getIndex())
                            result.addAll(resolve(list.get(key.getIndex()), sequence));
                        else
                            result = null;
                    }
                    else if (sequence.isEmpty()){
                        result.addAll(list);
                    }
                    else{
                        for (Map<String,Object> entry: list){
                            List<Object> temp = resolve(entry, (Stack<RecordKey>) sequence.clone()); //recursion
                            if (temp == null)
                                result = null;
                            else
                                result.addAll(temp);
                        }
                    }
                }
                else if (Map.class.isAssignableFrom(value.getClass())){                    
                    if (key.isIndexed())
                        throw new RecordKeyException(
                                "RecordKey [" + key + "] is indexed, but does not reference a List");
                    
                    result.addAll(resolve((Map<String,Object>) value, sequence));
                }
                else{                    
                    if (key.isIndexed())
                        throw new RecordKeyException(
                                "RecordKey [" + key + "] is indexed, but does not reference a List");
                    
                    result.add(value);
                }
            }
        }
        else{
            result = new ArrayList<Object>();
            result.add(map);
        }
        
        return result;
    }
    
}
