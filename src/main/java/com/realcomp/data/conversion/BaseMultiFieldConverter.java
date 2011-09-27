package com.realcomp.data.conversion;

import com.realcomp.data.DataType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author krenfro
 */
public abstract class BaseMultiFieldConverter implements MultiFieldConverter{

    
    protected List<DataType> supportedTypes;

    public BaseMultiFieldConverter(){
        supportedTypes = new ArrayList<DataType>();
        supportedTypes.add(DataType.STRING);
        supportedTypes.add(DataType.BOOLEAN);
        supportedTypes.add(DataType.INTEGER);
        supportedTypes.add(DataType.FLOAT);
        supportedTypes.add(DataType.DOUBLE);
        supportedTypes.add(DataType.LONG);        
    }
    
    protected List<String> fieldNames;

    @Override
    public List<String> getFields() {
        return fieldNames;
    }

    @Override
    public void setFields(List<String> fieldNames) {
        this.fieldNames = fieldNames;
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
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final BaseMultiFieldConverter other = (BaseMultiFieldConverter) obj;
        if (this.fieldNames != other.fieldNames && (this.fieldNames == null || !this.fieldNames.equals(other.fieldNames)))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.fieldNames != null ? this.fieldNames.hashCode() : 0);
        return hash;
    }
}
