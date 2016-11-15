package com.realcomp.prime.record;

public class RecordValueException extends RuntimeException{

    public RecordValueException(){
        super();
    }

    public RecordValueException(String message){
        super(message);
    }

    public RecordValueException(String message, Throwable cause){
        super(message, cause);
    }

    public RecordValueException(Throwable cause){
        super(cause);
    }
}
