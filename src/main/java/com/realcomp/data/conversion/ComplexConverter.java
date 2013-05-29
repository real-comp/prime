package com.realcomp.data.conversion;

import com.realcomp.data.DataType;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A Converter for all DataTypes
 *
 * @author krenfro
 */
public abstract class ComplexConverter implements Converter{

    protected List<DataType> supportedTypes;

    public ComplexConverter(){
        supportedTypes = Arrays.asList(DataType.values());
    }

    @Override
    public abstract Object convert(Object value) throws ConversionException;

    /**
     *
     * @return List of all DataTypes.
     */
    @Override
    public List<DataType> getSupportedTypes(){
        return Collections.unmodifiableList(supportedTypes);
    }
}
