package com.realcomp.data.view;

import com.realcomp.data.record.Record;

/**
 *
 * @author krenfro
 */
public class BaseRecordView implements RecordView{
    
    protected Record record;

    public BaseRecordView(Record record){
        if (record == null)
            throw new IllegalArgumentException("record is null");
        this.record = record;
    }
}
