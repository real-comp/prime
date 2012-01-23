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
public class Multiply implements Converter {

    protected List<DataType> supportedTypes;
    protected Double factor;
    
    public Multiply(){
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
    public Multiply copyOf(){
        return new Multiply();
    }

    public Double getFactor() {
        return factor;
    }

    public void setFactor(Double factor) {
        this.factor = factor;
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
            return (Double) DataType.DOUBLE.coerce(value) * factor;
        }
    }

    
    @Override
    public boolean equals(Object other) {
        return (other instanceof Multiply);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }
    
}
