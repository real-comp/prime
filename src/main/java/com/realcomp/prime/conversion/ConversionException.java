package com.realcomp.prime.conversion;


import com.google.common.base.Optional;
import com.realcomp.prime.record.Record;

public class ConversionException extends Exception{

    private static final long serialVersionUID = -6194444803112488817L;

    private Record record;

    public ConversionException(){
        super();
    }

    public ConversionException(String message){
        super(message);
    }

    public ConversionException(String message, Throwable cause){
        super(message, cause);
    }

    public ConversionException(Throwable cause){
        super(cause);
    }

    public Optional<Record> getRecord(){
        return Optional.fromNullable(record);
    }

    public void setRecord(Record record){
        this.record = record;
    }

}
