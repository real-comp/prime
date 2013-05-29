package com.realcomp.data.conversion;

/**
 *
 * @author krenfro
 */
@com.realcomp.data.annotation.Converter("lowerCase")
public class LowerCase extends StringConverter{

    @Override
    public Object convert(Object value) throws ConversionException{

        return value == null ? null : value.toString().toLowerCase();
    }

    @Override
    public LowerCase copyOf(){
        return new LowerCase();
    }

    @Override
    public boolean equals(Object other){
        return (other instanceof LowerCase);
    }

    @Override
    public int hashCode(){
        int hash = 7;
        return hash;
    }
}
