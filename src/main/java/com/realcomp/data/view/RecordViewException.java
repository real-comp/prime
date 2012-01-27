package com.realcomp.data.view;

import com.realcomp.data.record.*;

public class RecordViewException extends Exception{
    
    public RecordViewException(){
        super();
    }

    public RecordViewException(String message) {
        super(message);
    }

    public RecordViewException(String message, Throwable cause) {
        super(message, cause);
    }

    public RecordViewException(Throwable cause) {
        super(cause);
    }
}
