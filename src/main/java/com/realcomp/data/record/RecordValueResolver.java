package com.realcomp.data.record;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author krenfro
 */
public class RecordValueResolver {
    
    
    public static List<Object> resolve(Map<String,Object> data, String key){
        
        return resolve(data, IndexedRecordKey.parse(key));        
    }
    
    private static List<Object> resolve(Map<String,Object> map, List<IndexedRecordKey> keys){
        
        List<Object> retVal = new ArrayList<Object>();        
        
        if (keys.isEmpty()){
            retVal.add(map);
        }
        else{            
            IndexedRecordKey key = keys.remove(0);
            Object value = map.get(key.getKey());

            if (value != null){
                if (List.class.isAssignableFrom(value.getClass())){                    
                    List<Map<String,Object>> list = (List<Map<String,Object>>) value;

                    if (key.isIndexed()){
                        if (list.size() > key.getIndex())
                            retVal.addAll(resolve(list.get(key.getIndex()), new ArrayList<IndexedRecordKey>(keys)));
                    }
                    else{
                        for (Map<String,Object> entry: list)
                            retVal.addAll(resolve(entry, new ArrayList<IndexedRecordKey>(keys)));
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
