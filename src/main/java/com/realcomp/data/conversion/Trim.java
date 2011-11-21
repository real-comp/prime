package com.realcomp.data.conversion;

/**
 *
 * @author krenfro
 */
@com.realcomp.data.annotation.Converter("trim")
public class Trim extends SimpleConverter {

    @Override
    public Object convert(Object value) throws ConversionException{
        return value == null ? null : value.toString().trim();
    }

    
    @Override
    public Trim copyOf(){
        return new Trim();
    }
    
    @Override
    public boolean equals(Object other) {
        return (other instanceof Trim);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }
}
