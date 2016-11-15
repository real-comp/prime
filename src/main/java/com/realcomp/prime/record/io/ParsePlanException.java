package com.realcomp.prime.record.io;

import com.realcomp.prime.schema.SchemaException;

public class ParsePlanException extends SchemaException{

    public ParsePlanException(){
        super();
    }

    public ParsePlanException(String message){
        super(message);
    }

    public ParsePlanException(String message, Throwable cause){
        super(message, cause);
    }

    public ParsePlanException(Throwable cause){
        super(cause);
    }
}
