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
        
        switch (DataType.getDataType(value)){
            
            case DOUBLE:
                return DataType.DOUBLE.coerce(value);
        }
        if (value instanceof Number){
            
            if (value instanceof Double){
                
            }
            else if (value instanceof Float){
                return Math.round((Float) value);
            }
            else if (value instanceof Long){
                return value;
            }
            else if (value instanceof Integer){
                return value;
            }
            else{
                throw new ConversionException("Unable to round data of type: " + value.getClass().getName());
            }
        }
        else{
            return Long.toString(Math.round(Double.parseDouble(value.toString())));
        }
        
        
    }

}
