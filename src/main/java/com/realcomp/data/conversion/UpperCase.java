package com.realcomp.data.conversion;

/**
 *
 * @author krenfro
 */
@com.realcomp.data.annotation.Converter("upperCase")
public class UpperCase extends StringConverter{

    @Override
    public Object convert(Object value) throws ConversionException{
        return value == null ? null : value.toString().toUpperCase();
    }

    @Override
    public UpperCase copyOf(){
        return new UpperCase();
    }

    @Override
    public boolean equals(Object other){
        return (other instanceof UpperCase);
    }

    @Override
    public int hashCode(){
        int hash = 7;
        return hash;
    }
}
