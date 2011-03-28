package com.realcomp.data.conversion;

/**
 *
 * @author krenfro
 */
public class MissingFieldException extends ConversionException{

    private String fieldName;

    public MissingFieldException(String fieldName){
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
}
