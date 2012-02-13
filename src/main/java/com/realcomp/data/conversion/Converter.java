package com.realcomp.data.conversion;

import com.realcomp.data.DataType;
import com.realcomp.data.Operation;
import java.util.Collection;

/**
 * An Operation that converts the value of an Object in some way.
 * The Object must be a supported DataType.
 * 
 * @author krenfro
 */
public interface Converter extends Operation{

    /**
     * In the normal case, if a converter is asked to convert a null value, it should return null.
     * There are special converters (Concat, Constant) that do allow a null input.  This is
     * necessary so new fields can be created based on the data in other fields.
     * 
     * @param value the value to convert. may be null
     * @return the converted value
     * @throws ConversionException if the value is not a supported DataType, or there 
     *          was a problem performing the conversion.
     * @throws IllegalArgumentException if value is null
     */
    Object convert(Object value) throws ConversionException;
    
    /**
     * @return the DataTypes supported by this converter.
     */
    Collection<DataType> getSupportedTypes();
}
