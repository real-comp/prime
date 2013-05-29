package com.realcomp.data.schema;

/**
 *
 * @author krenfro
 */
public class SchemaException extends Exception{

    public SchemaException(){
        super();
    }

    public SchemaException(String message){
        super(message);
    }

    public SchemaException(String message, Throwable cause){
        super(message, cause);
    }

    public SchemaException(Throwable cause){
        super(cause);
    }
}
