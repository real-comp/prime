package com.realcomp.prime.view;

import com.realcomp.prime.record.Record;

/**
 * A view converts a Record to/from an arbitrary type.
 *
 */
public interface RecordView<T>{

    /**
     *
     * @param record may be null
     * @return an instance of T, or null if record is null
     * @throws RecordViewException if there was a problem converting the record
     */
    T fromRecord(Record record) throws RecordViewException;

    /**
     * @param type the object to convert
     * @return T converted to a Record, or null if type is null
     * @throws RecordViewException if there was a problem converting the record.
     */
    Record toRecord(T type) throws RecordViewException;

    boolean supports(Class clazz);
}
