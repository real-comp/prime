package com.realcomp.data.view;

import com.realcomp.data.record.Record;


/**
 * A view converts a Record to/from an arbitrary type.
 *
 * @author krenfro
 */
public interface RecordView<T>{

    /**
     * 
     * @param record may be null
     * @return  an instance of T, or null if record is null
     */
    T fromRecord(Record record);
    
    /**
     * @param type the object to convert
     * @return T converted to a Record, or null if type is null
     */
    Record toRecord(T type);
    
    boolean supports(Class clazz);
}
