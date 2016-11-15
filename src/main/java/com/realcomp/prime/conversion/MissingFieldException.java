package com.realcomp.prime.conversion;


public class MissingFieldException extends ConversionException{

    private static final long serialVersionUID = 1L;
    private String fieldName;

    public MissingFieldException(String fieldName){
        super(fieldName);
        this.fieldName = fieldName;
    }

    public MissingFieldException(String fieldName, Throwable cause){
        super(fieldName, cause);
        this.fieldName = fieldName;
    }

    public String getFieldName(){
        return fieldName;
    }

    public void setFieldName(String fieldName){
        this.fieldName = fieldName;
    }
}
