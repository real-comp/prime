package com.realcomp.data.conversion;

/**
 *
 * @author krenfro
 */
@com.realcomp.data.annotation.Converter("lowerCase")
public class LowerCase implements Converter {

    @Override
    public String convert(String value) throws ConversionException{
        if (value == null)
            throw new IllegalArgumentException("value is null");

        return value.toLowerCase();
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof LowerCase);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }
}
