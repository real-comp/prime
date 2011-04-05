package com.realcomp.data.view;

import com.realcomp.data.record.Record;

/**
 * A View that wraps a Record.
 *
 * @author krenfro
 */
public abstract class RecordView{

    protected Record record;

    public RecordView(Record record){
        if (record == null)
            throw new IllegalArgumentException("record is null");
        this.record = record;
    }
}
