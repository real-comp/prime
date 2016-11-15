package com.realcomp.prime.record;

import com.realcomp.prime.DataType;
import java.util.AbstractMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Returns all (leaf) entries in a Record. The order of the entries is not defined.
 *
 *
 */
class RecordEntries{

    private static final Logger logger = Logger.getLogger(RecordEntries.class.getName());

    static Set<Map.Entry<String, Object>> getEntries(Map<String, Object> map){

        Set<Map.Entry<String, Object>> entries = new HashSet<Map.Entry<String, Object>>();

        for (Map.Entry<String, Object> entry : map.entrySet()){
            String fieldName = entry.getKey();
            Object value = entry.getValue();
            if (isLeaf(value)){
                entries.add(new AbstractMap.SimpleEntry(fieldName, value));
            }
            else{
                DataType type = DataType.getDataType(value);
                if (type == DataType.LIST){
                    entries.addAll(getEntries((List) value, fieldName));
                }
                else if (type == DataType.MAP){
                    entries.addAll(getEntries((Map) value, fieldName + "."));
                }
            }
        }

        return entries;
    }

    private static Set<Map.Entry<String, Object>> getEntries(List list, String prefix){

        assert (!isLeaf(list));

        Set<Map.Entry<String, Object>> entries = new HashSet<Map.Entry<String, Object>>();
        if (list != null){
            for (int index = 0; index < list.size(); index++){
                Object value = list.get(index);
                if (isLeaf(value)){
                    entries.add(new AbstractMap.SimpleEntry(String.format("%s[%s]", prefix, index), value));
                }
                else{
                    DataType type = DataType.getDataType(value);
                    if (type == DataType.LIST){
                        Set<Map.Entry<String, Object>> temp =
                                getEntries((List) value, String.format("%s[%s]", prefix, index));
                        entries.addAll(temp);
                    }
                    else if (type == DataType.MAP){
                        Set<Map.Entry<String, Object>> temp =
                                getEntries((Map) value, String.format("%s[%s].", prefix, index));
                        entries.addAll(temp);
                    }
                }
            }
        }
        return entries;
    }

    private static Set<Map.Entry<String, Object>> getEntries(Map<String, Object> map, String prefix){

        Set<Map.Entry<String, Object>> entries = new HashSet<Map.Entry<String, Object>>();

        for (Map.Entry<String, Object> entry : map.entrySet()){
            String fieldName = prefix.concat(entry.getKey());
            Object value = entry.getValue();
            if (isLeaf(value)){
                entries.add(new AbstractMap.SimpleEntry(fieldName, value));
            }
            else{
                DataType type = DataType.getDataType(value);
                if (type == DataType.LIST){
                    entries.addAll(getEntries((List) value, fieldName));
                }
                else if (type == DataType.MAP){
                    entries.addAll(getEntries((Map) value, fieldName + "."));
                }
            }
        }

        return entries;
    }

    /**
     * Determines if the value represents a leaf value in the record.
     * <p>
     * <b>Non</b>-Leaf values are:
     * <ul>
     * <li>non-empty maps</li>
     * <li>lists that contain maps or lists</li>
     * </ul>
     * </p>
     *
     * @param value
     * @return
     */
    private static boolean isLeaf(Object value){

        boolean leaf = true;
        if (value != null){
            DataType type = DataType.getDataType(value);
            if (type == DataType.LIST){
                for (Object entry : (List) value){
                    if (entry != null){
                        DataType entryType = DataType.getDataType(entry);
                        if (entryType == DataType.LIST || entryType == DataType.MAP){
                            leaf = false;
                            break;
                        }
                    }
                }
            }
            else if (type == DataType.MAP){
                leaf = ((Map) value).isEmpty();
            }
        }
        return leaf;
    }
}
