package com.realcomp.data.record;

import com.realcomp.data.DataType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Assembles values back into a map for a RecordKey.
 * 
 * @see RecordValueResolver
 * @author krenfro
 */
public class RecordValueAssembler {
    
    public Object assemble(Map<String,Object> map, RecordKey key, Object value) throws RecordValueException{
        if (map == null)
            throw new IllegalArgumentException("map is null");
        if (value == null)
            throw new IllegalArgumentException("value is null");
        if (key == null)
            throw new IllegalArgumentException("key is null");
        
        
        return assemble(map, buildKeySequence(key), value);
    }
    
    
    
    
    
    /**

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
     */
    
    /**
     * Builds the sequence that keys need to be resolved from the root Map. The root key will be at the
     * top of the stack.
     * 
     * @param key not null
     * @return 
     */
    private Stack<RecordKey> buildKeySequence(final RecordKey key){
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
    
    
    
    private void createDeepMap(Map<String,Object> data, List<RecordKey> keys) 
            throws RecordValueException{
        
        if (!keys.isEmpty()){
            List<RecordKey> workingKeys = new ArrayList<RecordKey>(keys);
            RecordKey key = workingKeys.remove(0);
            
            Object value = data.get(key.getName());
            List list = null;
            Map<String,Object> map = null;
            
            if (value == null){
                list = new ArrayList<Map>();
                ensureCapacity(list, key.isIndexed() ? key.getIndex() + 1 : 1);
                map = (Map) list.get(key.isIndexed() ? key.getIndex() : 0);
                data.put(key.getName(), list);
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
    
            
    private void ensureCapacity(List list, int minCapacity){
        if (list == null)
            list = new ArrayList();
        int diff = minCapacity - list.size();
        for (int x = 0; x < diff; x++){
            list.add(null);
        }
    }  
    
    private Object assemble(List list, Integer index, Object value){
        
        assert(index != null);        
        if (list == null)
        ensureCapacity(list, index + 1);
        return list.set(index, value);
    }
    
    private Object assemble(Map<String,Object> map, String name, Object value){
        
        assert(map != null);
        assert(name != null);
        return map.put(name, value);
    }
    
    
     private Object assemble(List list, Stack<RecordKey> keys, Object value) 
            throws RecordValueException{
        
        assert(list != null);
        assert(keys != null);
        
        RecordKey key = keys.pop();
        if (keys.isEmpty()){
            
        }
        
        
        
        Object previousValue = null;
        if (!keys.isEmpty()){
            
            Object temp = map.get(key.getName());
            DataType type = DataType.getDataType(temp);
            if (type == DataType.LIST){
                if (key.isIndexed()){
                    
            }
            else if (type == DataType.MAP){
                
            }
        }
        
        return previousValue;
    }
     }
        
        
    /**
      * If the key is not indexed, then value is simply returned. If key <i>is</i> indexed,
      * then a new List is created with the value at key.index.
      * 
      * @param key
      * @param value
      * @return value if key is not indexed, else a list that contains the value at key.index.
      */
    protected Object getObjectToInsert(RecordKey key, Object value){
        assert(key != null);
        Object result = value;
        if (key.isIndexed()){
            List list = new ArrayList();
            ensureCapacity(list, key.getIndex() + 1);
            list.set(key.getIndex(), value);
            result = list;
        }
        return result;
    }
     
        
    protected Object assemble(Map<String,Object> map, Stack<RecordKey> keys, Object value) 
            throws RecordValueException{
        
        assert(map != null);
        assert(value != null);
        assert(keys != null);
        assert(!keys.isEmpty());
        
        Object previous = null;
        RecordKey key = keys.pop();
        if (keys.isEmpty()){
            
            if (key.isIndexed()){
                List list = (List) map.get(key.getName());
                ensureCapacity(list, key.getIndex() + 1);
            }
            else{
                previous = map.put(key.getName(), value);
            }
        }
            //at last key.
            
        }
        else{
            
            
        }
        
        if (existing == null){
            existing = hydrate(key, value);
        }
        
        
        else{
            
        }
            
        
        if (!keys.isEmpty()){
            
            Object temp = map.get(key.getName());
            DataType type = DataType.getDataType(temp);
            if (type == DataType.LIST){
                if (key.isIndexed()){
                    
            }
            else if (type == DataType.MAP){
                
            }
        }
        
        return previous;
        /*
        else if (keys.size() > 1){
            RecordKey parentKey = RecordKey.compose(RecordKey.getParent(keys));
            RecordKey childKey = keys.get(keys.size() - 1);
            List<Object> existing = RecordValueResolver.resolve(data, parentKey);
            
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
                                new Object[]{value, childKey, existing.getClass().getName()}));
                }
                    
                Map map = (Map) existing.get(childKey.isIndexed() ? childKey.getIndex() : 0);
                map.put(childKey.getName(), value);
            }
        }
        else{            
            data.put(keys.pop().getName(), value);
        }
         * 
         */
    }
    
    
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
 
    
}
