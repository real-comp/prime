package com.realcomp.data.record;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * @see RecordValueResolver
 * @author krenfro
 */
public class RecordMultiValueResolver{

    /*
     * public static List<Object> resolve(Record record, String key){ return resolve(record.data, new RecordKey(key)); }
     *
     */
    public static List<Object> resolve(Map<String, Object> map, String key){
        return resolve(map, new RecordKey(key));
    }

    /*
     * static List<Object> resolve(Record record, RecordKey key){ return resolve(record.data, key); }
     *
     */
    static List<Object> resolve(Map<String, Object> map, RecordKey key){
        return resolve(map, key.buildKeySequence());
    }

    @SuppressWarnings("unchecked")
    private static List<Object> resolve(Map<String, Object> map, Stack<RecordKey> sequence){

        List<Object> result = new ArrayList<Object>();

        if (!sequence.isEmpty()){
            RecordKey key = sequence.pop();
            Object value = map.get(key.getName());
            if (value != null){
                if (List.class.isAssignableFrom(value.getClass())){
                    List<Map<String, Object>> list = (List<Map<String, Object>>) value;
                    if (key.isIndexed()){
                        try{
                            result.addAll(resolve(list.get(key.getIndex()), sequence));
                        }
                        catch (IndexOutOfBoundsException ex){
                        }
                    }
                    else if (sequence.isEmpty()){
                        result.addAll(list);
                    }
                    else{
                        for (Map<String, Object> entry : list){
                            result.addAll(resolve(entry, (Stack<RecordKey>) sequence.clone()));
                        }
                    }
                }
                else if (Map.class.isAssignableFrom(value.getClass())){
                    if (key.isIndexed()){
                        throw new RecordKeyException(
                                String.format("RecordKey [%s] is indexed, but does not reference a List", key));
                    }
                    result.addAll(resolve((Map<String, Object>) value, sequence));
                }
                else{
                    if (key.isIndexed()){
                        throw new RecordKeyException(
                                String.format("RecordKey [%s] is indexed, but does not reference a List", key));
                    }
                    result.add(value);
                }
            }
        }
        else{
            result.add(map);
        }

        return result;
    }
}
