package com.realcomp.data.conversion;

import com.realcomp.data.DataType;


/**
 * Rounds, treating the value as a Double, to the nearest Long.
 *
 * @see Math#round(double);
 * @author krenfro
 *
 */
@com.realcomp.data.annotation.Converter("round")
public class Round extends SimpleConverter {


    public Round(){
    }

    @Override
    public Round copyOf(){
        return new Round();
    }
        

    @Override
    public Object convert(Object value) throws ConversionException{
        
        if (value == null)
            throw new IllegalArgumentException("value is null");
        if (value.toString().isEmpty())
            return value;
        
        return Math.round((Double) DataType.DOUBLE.coerce(value));
    }

}
