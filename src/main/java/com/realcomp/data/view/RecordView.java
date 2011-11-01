package com.realcomp.data.view;

import com.realcomp.data.record.Record;


/**
 * A view converts a Record to/from an arbitrary type.
 *
 * @author krenfro
 */
public interface RecordView<T>{

    T fromRecord(Record record);
    
    Record toRecord(T type);
    
    boolean supports(Class clazz);
}
