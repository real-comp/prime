package com.realcomp.data.conversion;

import com.realcomp.data.DataType;
import com.realcomp.data.Operation;
import java.util.List;

/**
 *
 * @author krenfro
 */
public interface Converter extends Operation{

    /**
     * @param value not null
     * @return the converted value, not null
     */
    Object convert(Object value) throws ConversionException;
    
    /**
     * 
     * @return List of DataTypes supported by this converter.
     */
    List<DataType> getSupportedTypes();
}
