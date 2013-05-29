package com.realcomp.data.conversion;

/**
 * @author krenfro
 */
public class ConversionException extends Exception{

    private static final long serialVersionUID = -6194444803112488817L;

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
}
