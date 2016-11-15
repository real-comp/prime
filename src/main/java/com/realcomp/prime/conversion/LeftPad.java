package com.realcomp.prime.conversion;

import org.apache.commons.lang.StringUtils;

import java.util.Objects;

/**
 *
 * Converts a value to a String (toString), and left pads the value with some character(s) to a specified length.
 * Default pad character is a single space.
 *
 */
@com.realcomp.prime.annotation.Converter("leftPad")
public class LeftPad extends SimpleConverter{

    private static final int USE_ORIGINAL_LENGTH = -1;
    private int length = USE_ORIGINAL_LENGTH;
    private String with = " ";

    public LeftPad(){
        super();
    }

    public LeftPad(int length){
        super();
        if (length < 0){
            throw new IllegalArgumentException("length < 0");
        }
        this.length = length;
    }

    @Override
    public Object convert(Object value) throws ConversionException{

        if (length == USE_ORIGINAL_LENGTH){
            return value;
        }
        else{
            return value == null ? null : StringUtils.leftPad(value.toString(), length, with).substring(0, length);
        }
    }

    @Override
    public LeftPad copyOf(){
        LeftPad copy = new LeftPad();
        copy.length = length;
        copy.with = with;
        return copy;
    }

    public int getLength(){
        return length;
    }

    public void setLength(int length){
        if (length < 0){
            throw new IllegalArgumentException("length < 0");
        }
        this.length = length;
    }

    public String getWith(){
        return with;
    }

    public void setWith(String with){
        this.with = with;
    }

    @Override
    public int hashCode(){
        int hash = 3;
        hash = 53 * hash + this.length;
        hash = 53 * hash + Objects.hashCode(this.with);
        return hash;
    }

    @Override
    public boolean equals(Object obj){
        if (obj == null){
            return false;
        }
        if (getClass() != obj.getClass()){
            return false;
        }
        final LeftPad other = (LeftPad) obj;
        if (this.length != other.length){
            return false;
        }
        if (!Objects.equals(this.with, other.with)){
            return false;
        }
        return true;
    }
}
