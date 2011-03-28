package com.realcomp.data.record;

import com.realcomp.data.schema.SchemaException;

/**
 *
 * @author krenfro
 */
public class ParsePlanException extends SchemaException{
    
    public ParsePlanException(){
        super();
    }

    public ParsePlanException(String message) {
        super(message);
    }

    public ParsePlanException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParsePlanException(Throwable cause) {
        super(cause);
    }
}
