package com.realcomp.data.view;

import com.realcomp.data.record.Record;


/**
 * A view that wraps a Record.
 *
 * @author krenfro
 */
public interface RecordView{

    Record getRecord();
    
    void setRecord(Record record);
}
