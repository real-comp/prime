package com.realcomp.data.schema.xml;

/**
 *
 * @author krenfro
 */
public class DynamicPropertyException extends Exception{
    public DynamicPropertyException(){
        super();
    }

    public DynamicPropertyException(String message) {
        super(message);
    }

    public DynamicPropertyException(String message, Throwable cause) {
        super(message, cause);
    }

    public DynamicPropertyException(Throwable cause) {
        super(cause);
    }
}
