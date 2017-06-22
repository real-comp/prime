package com.realcomp.prime.validation;

import com.realcomp.prime.record.Record;

import java.util.Optional;

/**
 * <p>Validation failure writing a Record.</p>
 * Validation problem information along with the actual Record that had the issue.
 */
public class RecordValidationException extends ValidationException{

    private Optional<Record> record;

    public RecordValidationException(ValidationException original, Record record){
        super(original);
        this.record = Optional.ofNullable(record);
    }

    public Optional<Record> getRecord() {return record; }

    public void setRecord(Record record){
        this.record = Optional.ofNullable(record);
    }

}
