package com.realcomp.prime.conversion;

import com.realcomp.prime.DataType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Rounds, treating the value as a Double, to the nearest Long.
 *
 * @see Math#round(double);
 * @author krenfro
 *
 */
@com.realcomp.prime.annotation.Converter("round")
public class Round implements Converter{

    protected List<DataType> supportedTypes;

    public Round(){
        supportedTypes = new ArrayList<DataType>();
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
    public Round copyOf(){
        return new Round();
    }

    @Override
    public Object convert(Object value) throws ConversionException{

        if (value == null){
            return null;
        }
        else if (value.toString().isEmpty()){
            return value;
        }
        else{
            return Math.round((Double) DataType.DOUBLE.coerce(value));
        }
    }

    @Override
    public boolean equals(Object other){
        return (other instanceof Round);
    }

    @Override
    public int hashCode(){
        int hash = 7;
        return hash;
    }
}
