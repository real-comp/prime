package com.realcomp.data.record;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Finds a specified value in a Record.  The composite key can reference a value
 * arbitrarily deep within a Record.  This utility dives into the Record to find the value.
 * @author krenfro
 */
public class RecordValueResolver {
    
    
    /**
     * @param data
     * @param compositeKey Period delimited key
     * @return a list of Objects (DataTypes) referenced by the compositeKey
     */
    public static List<Object> resolve(Map<String,Object> data, String compositeKey){
        
        return resolve(data, RecordKey.parse(compositeKey));        
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
