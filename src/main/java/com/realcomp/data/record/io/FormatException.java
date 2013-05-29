package com.realcomp.data.record.io;

import com.realcomp.data.schema.SchemaException;

/**
 *
 * @author krenfro
 */
public class FormatException extends SchemaException{

    public FormatException(){
        super();
    }

    public FormatException(String message){
        super(message);
    }

    public FormatException(String message, Throwable cause){
        super(message, cause);
    }

    public FormatException(Throwable cause){
        super(cause);
    }
}
