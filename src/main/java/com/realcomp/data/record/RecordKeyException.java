package com.realcomp.data.record;

public class RecordKeyException extends IllegalArgumentException{
    
    public RecordKeyException(){
        super();
    }

    public RecordKeyException(String message) {
        super(message);
    }

    public RecordKeyException(String message, Throwable cause) {
        super(message, cause);
    }

    public RecordKeyException(Throwable cause) {
        super(cause);
    }
}
