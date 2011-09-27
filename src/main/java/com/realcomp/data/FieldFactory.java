package com.realcomp.data;

import com.realcomp.data.conversion.BooleanConverter;
import com.realcomp.data.conversion.ConversionException;

/**
 *
 * @author krenfro
 */
public class FieldFactory {

    private static BooleanConverter booleanConverter = new BooleanConverter();

    /**
     *
     * @param type
     * @param value
     * @return
     * @throws NumberFormatException if type is numeric and was unable to parse the value as that type
     */
    public static Object create(DataType type, String value) throws ConversionException{

        return type.coerce(value);
        
        Object result = null;
        try{
            switch(type){
                case STRING:
                    result = value;
                    break;
                case INTEGER:
                    if (!value.isEmpty())
                        result = Integer.parseInt(value);
                    break;
                case FLOAT:
                    if (!value.isEmpty())
                        result = Float.parseFloat(value);
                    break;
                case LONG:
                    if (!value.isEmpty())
                        result = Long.parseLong(value);
                    break;
                case DOUBLE:
                    if (!value.isEmpty())
                        result = Double.parseDouble(value);
                    break;
                case BOOLEAN:
                    if (!value.isEmpty())
                        result = Boolean.parseBoolean(booleanConverter.convert(value));
                    break;
                default:
                    throw new ConversionException(
                        String.format("Unable to convert [%s] to type [%s]", value, type));
            }
        }
        catch(NumberFormatException nfe){
            throw new ConversionException(
                    String.format("Unable to convert [%s] to type [%s]", value, type));
        }

        return result;
    }


}
