package com.realcomp.data.record;

import com.realcomp.data.DataType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Assembles values back into a Record for a key.
 * 
 * @see RecordValueResolver
 * @author krenfro
 */
public class RecordValueAssembler {

    public static void assemble(Record record, String key, Object value) throws RecordValueException{
        
        if (record == null)
            throw new IllegalArgumentException("record is null");        
        if (value == null)
            throw new IllegalArgumentException("value is null");
        
        assemble(record.data, key, value);
    }    
    
    public static void assemble(Map<String,Object> data, String key, Object value) throws RecordValueException{     
        
        if (data == null)
            throw new IllegalArgumentException("data is null");        
        if (value == null)
            throw new IllegalArgumentException("value is null");
        
        assemble(data, RecordKey.parse(key), value);         
    }
    
     public static void assemble(Record record, String key, List values) throws RecordValueException{
        
        if (record == null)
            throw new IllegalArgumentException("record is null");        
        if (values == null)
            throw new IllegalArgumentException("values is null");
        
        assemble(record.data, key, values);
    }    
    
    public static void assemble(Map<String,Object> data, String key, List values) throws RecordValueException{     
        
        if (data == null)
            throw new IllegalArgumentException("data is null");        
        if (values == null)
            throw new IllegalArgumentException("values is null");
        
        assemble(data, RecordKey.parse(key), values);         
    }
    
    
    
    private static void createDeepMap(Map<String,Object> data, List<RecordKey> keys) 
            throws RecordValueException{
        
        if (!keys.isEmpty()){
            List<RecordKey> workingKeys = new ArrayList<RecordKey>(keys);
            RecordKey key = workingKeys.remove(0);
            
            Object value = data.get(key.getKey());
            List list = null;
            Map<String,Object> map = null;
            
            if (value == null){
                list = new ArrayList<Map>();
                ensureCapacity(list, key.isIndexed() ? key.getIndex() + 1 : 1);
                map = (Map) list.get(key.isIndexed() ? key.getIndex() : 0);
                data.put(key.getKey(), list);
                createDeepMap(map, workingKeys);
            }
            else if (DataType.getDataType(value) == DataType.LIST) {

                list = (List) value;                
                if (list.size() <= 1 || key.isIndexed()){
                    ensureCapacity(list, key.isIndexed() ? key.getIndex() + 1 : 1);
                    map = (Map) list.get(key.isIndexed() ? key.getIndex() : 0);
                    createDeepMap(map, workingKeys);
                }
                else{
                    throw new RecordValueException(
                            "There is more than one value in the Record for [" + key + "].  "
                            + "Remove ambiguity by adding list indexes to key. (e.g., 'prop.imp_info[1].stuff').");
                }
            }
            else{
                throw new RecordValueException(
                        String.format("The object at key [%s] is a [%s] not a List as expected.",
                                      new Object[]{key, list.getClass().getName()}));
            }
        }    
    }
    
    
    
            
    private static void ensureCapacity(List<Map<String,Object>> list, int minCapacity){
        int diff = minCapacity - list.size();
        for (int x = 0; x < diff; x++){
            list.add(new HashMap<String,Object>());
        }
    }
        
    private static void assemble(Map<String,Object> data, List<RecordKey> keys, Object value) 
            throws RecordValueException{
        
        assert(data != null);
        assert(value != null);
        assert(keys != null);
        
        
        if (keys.isEmpty()){
            return; //done
        }
        else if (keys.size() > 1){
            String allButLastKey = RecordKey.toKey(keys.subList(0, keys.size() - 1));
            RecordKey lastKey = keys.get(keys.size() - 1);
            List<Object> existing = RecordValueResolver.resolve(data, allButLastKey);
            
            if (existing.size() != 1){
                //since I only have one value, I can reliably infer the structure of the Record from the keys.
                createDeepMap(data, keys.subList(0, keys.size() - 1));
                assemble(data, keys, value); //recursion
            }
            else{                    
                if (DataType.getDataType(existing.get(0)) != DataType.MAP){
                    throw new RecordValueException(
                            String.format(
                                "Non-standard Record structure encountered.  "
                                 + "Unable to assemble value [%s] into the Record because I am expecting the object "
                                 + "at [%s] to be a Map, but instead it is a(n) [%s].  "
                                 + "Note: This is a valid Record, but I cannot assemble values into it reliably.",
                                new Object[]{value, lastKey, existing.getClass().getName()}));
                }
                    
                Map map = (Map) existing.get(lastKey.isIndexed() ? lastKey.getIndex() : 0);
                map.put(lastKey.getKey(), value);
            }
        }
        else{            
            data.put(keys.get(0).getKey(), value);
        }
    }
    
    
    private static void assemble(Map<String,Object> data, List<RecordKey> keys, List values)
        throws RecordValueException{
        
        assert(data != null);
        assert(values != null);
        assert(keys != null);
        
        if (keys.isEmpty()){
            return; //done
        }
        else if (keys.size() > 1){
            String allButLastKey = RecordKey.toKey(keys.subList(0, keys.size() - 1));
            RecordKey lastKey = keys.get(keys.size() - 1);
            List<Object> existing = null;
            
            try{
                existing = RecordValueResolver.resolve(data, allButLastKey);
            }
            catch(ClassCastException wrongClass){
                throw new RecordValueException(
                        String.format(
                            "Non-standard Record structure encountered.  "
                             + "Unable to assemble the value into the Record because I am expecting the "
                             + "object at [%s] to be a Map.  "
                             + "Note: This is a valid Record, but I cannot assemble values into it reliably.", lastKey),
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
                            new Object[]{values.size(), existing.size(), allButLastKey, allButLastKey}));
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
                                    new Object[]{values.get(x), lastKey, existing.getClass().getName()}));
                    }
                    
                    Map map = (Map) existing.get(x);
                    map.put(lastKey.getKey(), values.get(x));
                }
            }
        }
        else if (keys.size() == 1 && values.size() == 1){
            data.put(keys.get(0).getKey(), values.get(0));
        }
        else{            
            data.put(keys.get(0).getKey(), values);
        }
    }
 
    
}
