package com.realcomp.data.record;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Returns all values contained in a Record.  The order of the values is not defined. 
 * 
 * @see RecordEntries
 * @author krenfro
 */
public class RecordValues {
    
    /**
     * 
     * @param record not null
     * @return all values for all keys (including composite keys) for the Record. May contain multiple nulls.
     */
    public static List<Object> getValues(Record record){        
              
        List<Object> values = new ArrayList<Object>();
        Iterator<Map.Entry<String,Object>> itr  = RecordEntries.getEntries(record).iterator();
        while (itr.hasNext()){
            values.add(itr.next().getValue());
        }
        
        return values;
    }
    
}
