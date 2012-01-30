package com.realcomp.data.record;

import com.realcomp.data.DataType;
import java.util.AbstractMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Returns all entries (key/value pairs) in a Record.  The order of the entries is not defined. 
 * 
 * @author krenfro
 */
public class RecordEntries {
    
     public static Set<Map.Entry<String,Object>> getEntries(Map<String,Object> map){
        
        Set<Map.Entry<String,Object>> entries = new HashSet<Map.Entry<String,Object>>(); 
        
        for (Map.Entry<String,Object> entry: map.entrySet()){
            
            String fieldName = entry.getKey();
            Object value = entry.getValue();
            if (value != null){
                DataType type = DataType.getDataType(value);
                if (type == DataType.LIST){
                    entries.addAll(getEntries((List) value, fieldName));
                }
                else if (type == DataType.MAP){
                    entries.addAll(getEntries((Map) value, fieldName + "."));
                }
                else{
                    entries.add(new AbstractMap.SimpleEntry(fieldName, value));
                }
            }
            else{
                entries.add(new AbstractMap.SimpleEntry(fieldName, value));
            }
        }
        
        return entries;
    }
     
    protected static Set<Map.Entry<String,Object>> getEntries(List list, String prefix){
        
        Set<Map.Entry<String,Object>> entries = new HashSet<Map.Entry<String,Object>>();        
        if (list != null){
            for (int index = 0; index < list.size(); index++){                
                Object value = list.get(index);
                if (value != null){
                    DataType type = DataType.getDataType(value);
                    if (type == DataType.LIST){
                        entries.addAll(getEntries((List) value, String.format("%s[%s]", prefix, index)));
                    }
                    else if (type == DataType.MAP){
                        entries.addAll(getEntries((Map) value, String.format("%s[%s].", prefix, index)));
                    }
                    else{
                        entries.add(new AbstractMap.SimpleEntry(prefix, value));
                    }
                }
                else{
                    entries.add(new AbstractMap.SimpleEntry(prefix, value));
                }
            }
        }
        return entries;
    }
    
    protected static Set<Map.Entry<String,Object>> getEntries(Map<String,Object> map, String prefix){
        
        Set<Map.Entry<String,Object>> entries = new HashSet<Map.Entry<String,Object>>(); 
        
        for (Map.Entry<String,Object> entry: map.entrySet()){            
            String fieldName = prefix.concat(entry.getKey());
            Object value = entry.getValue();
            if (value != null){
                DataType type = DataType.getDataType(value);
                if (type == DataType.LIST){
                    entries.addAll(getEntries((List) value, fieldName));
                }
                else if (type == DataType.MAP){
                    entries.addAll(getEntries((Map) value, fieldName + "."));
                }
                else{
                    entries.add(new AbstractMap.SimpleEntry(fieldName, value));
                }
            }
            else{
                entries.add(new AbstractMap.SimpleEntry(fieldName, value));
            }
        }
        
        return entries;
    }    
}
