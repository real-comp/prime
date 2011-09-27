package com.realcomp.data.conversion;

import com.realcomp.data.DataType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A Converter for all DataTypes except Map and List
 * @author krenfro
 */
public abstract class SimpleConverter implements Converter{

    protected List<DataType> supportedTypes;

    public SimpleConverter(){
        supportedTypes = new ArrayList<DataType>();
        supportedTypes.add(DataType.STRING);
        supportedTypes.add(DataType.BOOLEAN);
        supportedTypes.add(DataType.INTEGER);
        supportedTypes.add(DataType.FLOAT);
        supportedTypes.add(DataType.DOUBLE);
        supportedTypes.add(DataType.LONG);        
    }

    @Override
    public abstract Object convert(Object value) throws ConversionException;
    
    /**
     * 
     * @return List of DataTypes supported by this converter. All Types except Map and List
     */
    @Override
    public List<DataType> getSupportedTypes(){
        return Collections.unmodifiableList(supportedTypes);
    }
}
