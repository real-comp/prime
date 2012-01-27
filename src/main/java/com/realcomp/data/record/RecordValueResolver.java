package com.realcomp.data.record;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Finds a specified value in a Record.  The composite key can reference a value
 * arbitrarily deep within a Record.  This utility dives into the Record to find the value.
 * 
 * @see RecordValueAssembler
 * @author krenfro
 */
public class RecordValueResolver {
    
    public static List<Object> resolve(Record record, String key){
        return resolve(record.data, key);
    }
    
    /**
     * @param data
     * @param key Period delimited key
     * @return a list of Objects (DataTypes) referenced by the key
     */
    public static List<Object> resolve(Map<String,Object> data, String key){
        
        return resolve(data, RecordKey.parse(key));        
    }
    
    private static List<Object> resolve(Map<String,Object> map, List<RecordKey> keys){
        
        List<Object> retVal = new ArrayList<Object>();        
        
        if (keys.isEmpty()){
            retVal.add(map);
        }
        else{            
            RecordKey key = keys.get(0);
            Object value = map.get(key.getKey());

            if (value != null){
                List<RecordKey> workingKeys = new ArrayList<RecordKey>(keys);
                key = workingKeys.remove(0);
                if (List.class.isAssignableFrom(value.getClass())){                    
                    List<Map<String,Object>> list = (List<Map<String,Object>>) value;

                    if (key.isIndexed()){
                        if (list.size() > key.getIndex())
                            retVal.addAll(resolve(list.get(key.getIndex()), workingKeys));
                    }
                    else{
                        for (Map<String,Object> entry: list)
                            retVal.addAll(resolve(entry, workingKeys)); //recursion
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
