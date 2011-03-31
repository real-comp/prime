package com.realcomp.data;

import com.realcomp.data.conversion.BooleanConverter;
import com.realcomp.data.conversion.ConversionException;

/**
 *
 * @author krenfro
 */
public class FieldFactory {

    private static BooleanConverter booleanConverter = new BooleanConverter();

    public static Field create(DataType type){

        switch(type){
            case STRING: return new StringField();
            case INTEGER: return new IntegerField();
            case FLOAT: return new FloatField();
            case LONG: return new LongField();
            case DOUBLE: return new DoubleField();
            case BOOLEAN: return new BooleanField();
            case MAP: return new MapField();
            case LIST: return new ListField();
            case NULL: return new NullField();
        }

        throw new IllegalArgumentException("unhandled DataType: " + type);
    }

    /**
     *
     * @param type
     * @param value
     * @return
     * @throws NumberFormatException if type is numeric and was unable to parse the value as that type
     */
    public static Field create(DataType type, String value) throws ConversionException{

        Field f = create(type);
        try{
            switch(type){
                case STRING:
                    f.setValue(value);
                    return f;
                case INTEGER:
                    if (!value.isEmpty())
                        f.setValue(Integer.parseInt(value));
                    return f;
                case FLOAT:
                    if (!value.isEmpty())
                        f.setValue(Float.parseFloat(value));
                    return f;
                case LONG:
                    if (!value.isEmpty())
                        f.setValue(Long.parseLong(value));
                    return f;
                case DOUBLE:
                    if (!value.isEmpty())
                        f.setValue(Double.parseDouble(value));
                    return f;
                case BOOLEAN:
                    if (!value.isEmpty())
                        f.setValue(Boolean.parseBoolean(booleanConverter.convert(value)));
                    return f;
                case NULL:
                    return f;
            }
        }
        catch(NumberFormatException nfe){
            throw new ConversionException(
                    String.format("Unable to convert [%s] to type [%s]", value, type));
        }

        throw new ConversionException(
                String.format("Unable to convert [%s] to type [%s]", value, type));
    }


}
