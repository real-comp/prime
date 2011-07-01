package com.realcomp.data.conversion;


/**
 * Rounds, treating the value as a Double, to the nearest Long.
 *
 * @see Math#round(double);
 * @author krenfro
 *
 */
@com.realcomp.data.annotation.Converter("round")
public class Round implements Converter {


    public Round(){
    }

    @Override
    public Round copyOf(){
        return new Round();
    }
        

    @Override
    public String convert(String value) throws ConversionException{
        if (value == null)
            throw new IllegalArgumentException("value is null");
        if (value.isEmpty())
            return value;
        
        return Long.toString(Math.round(Double.parseDouble(value)));
    }

}
