package com.realcomp.data.conversion;

import org.apache.commons.lang.StringUtils;

/**
 *
 * @deprecated use the more useful leftPad and rightPad converters instead of this
 * @author krenfro
 */
@com.realcomp.data.annotation.Converter("resize")
public class Resize extends SimpleConverter{

    private static final int USE_ORIGINAL_LENGTH = -1;
    private int length = USE_ORIGINAL_LENGTH;

    public Resize(){
        super();
    }

    public Resize(int length){
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
            return value == null ? null : StringUtils.rightPad(value.toString(), length).substring(0, length);
        }
    }

    @Override
    public Resize copyOf(){
        Resize copy = new Resize();
        copy.length = length;
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

    @Override
    public boolean equals(Object obj){
        if (obj == null){
            return false;
        }
        if (getClass() != obj.getClass()){
            return false;
        }
        final Resize other = (Resize) obj;
        if (this.length != other.length){
            return false;
        }
        return true;
    }

    @Override
    public int hashCode(){
        int hash = 7;
        hash = 37 * hash + this.length;
        return hash;
    }
}
