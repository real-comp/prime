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

    public static boolean assemble(Record record, String key, List<Object> values){
        return assemble(record.data, key, values);
    }
    
    public static boolean assemble(Map<String,Object> data, String key, List<Object> values){
        return assemble(data, RecordKey.parse(key), values);
    }
    
    
    /**
     * 
     * @param data
     * @param keys send all but the last key
     * @return 
     */
    private static List<Map<String,Object>> getLeaves(Map<String,Object> data, List<RecordKey> keys){
        
        List<Map<String,Object>> leaves = new ArrayList<Map<String,Object>>();
        if (keys.isEmpty()){
            leaves.add(data);
        }
        else{
            RecordKey currentKey = keys.remove(0);
            List<Map<String,Object>> list = (List<Map<String,Object>>) data.get(currentKey.getKey());
            
            if (list == null)
                list = new ArrayList<Map<String,Object>>();
            
            if (currentKey.isIndexed()){
                ensureCapacity(list, currentKey.getIndex() + 1);
                leaves.addAll(getLeaves(list.get(currentKey.getIndex()), new ArrayList<RecordKey>(keys)));
            }
            else{
                ensureCapacity(list, 1);
                leaves.addAll(getLeaves(list.get(0), new ArrayList<RecordKey>(keys)));
            }
        }
        
        return leaves;
    }
    
    private static void ensureCapacity(List<Map<String,Object>> list, int minCapacity){
        int diff = minCapacity - list.size();
        for (int x = 0; x < diff; x++){
            list.add(new HashMap<String,Object>());
        }
    }
    
    public static boolean assemble(Map<String,Object> data, List<RecordKey> keys, List<Object> values){

        //need like a getLeaves() method that returns a list of maps for each value?
        
        if (keys.isEmpty() && values.isEmpty()){
            return true; //done
        }
        else if (keys.isEmpty() && values.isEmpty()){
            //nowhere to set remaining values
            return false;
        }
        else{
            RecordKey finalKey = keys.remove(keys.size() - 1);
            List<Map<String,Object>> leaves = getLeaves(data, keys);
            
            
            RecordKey currentKey = keys.remove(0);
            Object incomingValue = values.get(0);
            DataType incomingType = DataType.getDataType(incomingValue);            
            Object existingValue = data.get(currentKey.getKey());
            
            if (existingValue == null){
                //need to create an arbitrarily deep structure in data to hold the values.
                
                
                switch (incomingType){
                    case MAP:
                        List<Map<String,Object>> newList = new ArrayList<Map<String,Object>>();
                        newList.add((Map<String,Object>) incomingValue);
                        data.put(currentKey.getKey(), newList);
                        break;
                    case LIST:
                        data.put(currentKey.getKey(), incomingValue);
                        break;
                    default:
                        data.put(currentKey.getKey(), incomingValue);                        
                        break;
                }
                values.remove(0);
            }
            
            if (value == null){
                value = new ArrayList<Map<String,Object>>();
                data.put(current.getKey(), value);
            }
            else{
                
            }
            
            if (current.isIndexed()){
                
            }
            else if (keys.isEmpty() && values.size() == 1){
                
            }
            else{
                return assemble(data, keys, values);
            }
        }
    }
    
    private static List<Object> resolve(Map<String,Object> map, List<RecordKey> keys){
        
        List<Object> retVal = new ArrayList<Object>();        
        
        if (keys.isEmpty()){
            retVal.add(map);
        }
        else{            
            RecordKey key = keys.remove(0);
            Object value = map.get(key.getKey());

            if (value != null){
                if (List.class.isAssignableFrom(value.getClass())){                    
                    List<Map<String,Object>> list = (List<Map<String,Object>>) value;

                    if (key.isIndexed()){
                        if (list.size() > key.getIndex())
                            retVal.addAll(resolve(list.get(key.getIndex()), new ArrayList<RecordKey>(keys)));
                    }
                    else{
                        for (Map<String,Object> entry: list)
                            retVal.addAll(resolve(entry, new ArrayList<RecordKey>(keys)));
                    }
                }
                else{
                    retVal.add(value);
                }
            }
        }
        
        return retVal;
    }
    
}
