package com.realcomp.data.conversion;

import com.realcomp.data.Operation;

/**
 *
 * @author krenfro
 */
@com.realcomp.data.annotation.Converter("integer")
public class IntegerConverter extends SimpleConverter{
    
     /**
     * 
     * @param value
     * @return an Integer
     * @throws ConversionException 
     */
    @Override
    public Object convert(Object value) throws ConversionException{

        Integer result = null;        
        if (value != null){
            
            if (value instanceof Number){
                result = ((Number) value).intValue();
            }
            else{
                try{
                    result = ((Double) Double.parseDouble(value.toString())).intValue();
                }
                catch(NumberFormatException ex){
                }
            }
            
            if (result == null)
                throw new ConversionException("Unable to convert [" + value + "] to an integer value");
        }
        
        return result;
    }

    @Override
    public Operation copyOf() {
        return new IntegerConverter();
    }
 
}
