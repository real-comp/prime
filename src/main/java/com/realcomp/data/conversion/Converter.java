package com.realcomp.data.conversion;

import com.realcomp.data.DataType;
import com.realcomp.data.Operation;
import java.util.Collection;
import java.util.List;

/**
 * An Operation that converts the value of an Object in some way.
 * The Object must be a supported DataType.
 * 
 * @author krenfro
 */
public interface Converter extends Operation{

    /**
     * @param value not null
     * @return the converted value, not null
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
