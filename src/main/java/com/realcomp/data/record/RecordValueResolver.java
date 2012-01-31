package com.realcomp.data.record;

import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Finds a specified value in a Record.  A composite RecordKey can reference a value
 * arbitrarily deep within a Record. 
 * 
 * @see RecordValueAssembler
 * @author krenfro
 */
public class RecordValueResolver {
    /*
    public static Object resolve(Record record, String key){
        return resolve(record.data, new RecordKey(key));
    }
     * 
     */
    
    public static Object resolve(Map<String,Object> map, String key){
        return resolve(map, new RecordKey(key));
    }    
   /*
    static Object resolve(Record record, RecordKey key){
        return resolve(record.data, key);
    }
     * 
     */
     
    static Object resolve(Map<String,Object> map, RecordKey key){
        return resolve(map, key.buildKeySequence());
    }
    
     
    @SuppressWarnings("unchecked")
    private static Object resolve(Map<String,Object> map, Stack<RecordKey> sequence){
        
        Object result = null;
        
        if (!sequence.isEmpty()){
            RecordKey key = sequence.pop();
            Object value = map.get(key.getName());
            if (value != null){
                if (List.class.isAssignableFrom(value.getClass())){                    
                    List<Map<String,Object>> list = (List<Map<String,Object>>) value;
                    if (key.isIndexed() || list.size() == 1){
                        try{
                            //allow single entry lists to be resolved without an index.
                            result = resolve(list.get(key.isIndexed() ? key.getIndex() : 0), sequence);
                        }
                        catch(IndexOutOfBoundsException ex){
                            result = null;
                        }                            
                    }
                    else if (sequence.isEmpty()){
                        result = list;
                    }
                    else if (list.isEmpty()){
                        result = null;
                    }
                    else{
                        throw new RecordKeyException(
                            String.format("Ambiguous key [%s] references a list of size [%s]", key, list.size()));
                    }
                }
                else if (Map.class.isAssignableFrom(value.getClass())){                    
                    if (key.isIndexed()){
                        throw new RecordKeyException(
                                String.format("RecordKey [%s] is indexed, but does not reference a List", key));
                    }                    
                    result = resolve((Map<String,Object>) value, sequence);
                }
                else{                    
                    if (key.isIndexed()){
                        throw new RecordKeyException(
                                String.format("RecordKey [%s] is indexed, but does not reference a List", key));
                    }
                    result = value;
                }
            }
        }
        else{
            result = map;
        }
        
        return result;
    }
    
}
