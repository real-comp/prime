package com.realcomp.data.conversion;

import com.realcomp.data.DataType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Multiply, treating arguments as Doubles.
 *
 *
 * @author krenfro
 *
 */
@com.realcomp.data.annotation.Converter("multiply")
public class Multiply implements Converter{

    protected List<DataType> supportedTypes;
    protected Double factor = 1d;
    protected Double defaultValue;

    public Multiply(){
        supportedTypes = new ArrayList<>();
        supportedTypes.add(DataType.STRING);
        supportedTypes.add(DataType.INTEGER);
        supportedTypes.add(DataType.FLOAT);
        supportedTypes.add(DataType.DOUBLE);
        supportedTypes.add(DataType.LONG);
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
    public Multiply copyOf(){
        Multiply copy = new Multiply();
        copy.setFactor(factor);
        copy.setDefault(defaultValue);
        return copy;
    }

    public Double getFactor(){
        return factor;
    }

    public void setFactor(Double factor){
        this.factor = factor;
    }

    public Double getDefault(){
        return defaultValue;
    }

    public void setDefault(Double defaultValue){
        this.defaultValue = defaultValue;
    }

    @Override
    public Object convert(Object value) throws ConversionException{

        Object retVal = value;
        if (value != null){
            if (value.toString().isEmpty() && defaultValue != null){
                retVal = defaultValue * factor;
            }
            else{
                retVal = (Double) DataType.DOUBLE.coerce(value) * factor;
            }
        }
        return retVal;
    }

    @Override
    public boolean equals(Object other){
        return (other instanceof Multiply);
    }

    @Override
    public int hashCode(){
        int hash = 7;
        return hash;
    }
}
