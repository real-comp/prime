package com.realcomp.data.conversion;

import com.realcomp.data.DataType;
import com.realcomp.data.Operation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A Converter for the STRING data DataType
 *
 * @author krenfro
 */
public class StringConverter implements Converter{

    protected List<DataType> supportedTypes;

    public StringConverter(){
        supportedTypes = new ArrayList<DataType>();
        supportedTypes.add(DataType.STRING);
    }

    @Override
    public Object convert(Object value) throws ConversionException{
        return value == null ? null : value.toString();
    }

    /**
     *
     * @return List of DataTypes supported by this converter. All Types except Map and List
     */
    @Override
    public List<DataType> getSupportedTypes(){
        return Collections.unmodifiableList(supportedTypes);
    }

    @Override
    public Operation copyOf(){
        return new StringConverter();
    }

    @Override
    public int hashCode(){
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.supportedTypes);
        return hash;
    }

    @Override
    public boolean equals(Object obj){
        if (obj == null){
            return false;
        }
        if (getClass() != obj.getClass()){
            return false;
        }
        final StringConverter other = (StringConverter) obj;
        if (!Objects.equals(this.supportedTypes, other.supportedTypes)){
            return false;
        }
        return true;
    }
}
