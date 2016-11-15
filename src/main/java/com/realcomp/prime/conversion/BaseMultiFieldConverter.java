package com.realcomp.prime.conversion;

import com.realcomp.prime.DataType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public abstract class BaseMultiFieldConverter implements MultiFieldConverter{

    protected List<DataType> supportedTypes;
    protected List<String> fieldNames;

    public BaseMultiFieldConverter(){
        fieldNames = new ArrayList<>();
        supportedTypes = new ArrayList<>();
        supportedTypes.add(DataType.STRING);
        supportedTypes.add(DataType.BOOLEAN);
        supportedTypes.add(DataType.INTEGER);
        supportedTypes.add(DataType.FLOAT);
        supportedTypes.add(DataType.DOUBLE);
        supportedTypes.add(DataType.LONG);
    }

    public BaseMultiFieldConverter(List<String> fieldNames){
        this();
        setFields(fieldNames);
    }

    @Override
    public List<String> getFields(){
        return fieldNames;
    }

    @Override
    public void setFields(List<String> fieldNames){
        if (fieldNames == null){
            throw new IllegalArgumentException("fieldNames is null");
        }
        this.fieldNames.clear();
        this.fieldNames.addAll(fieldNames);
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
    public boolean equals(Object obj){
        if (obj == null){
            return false;
        }
        if (getClass() != obj.getClass()){
            return false;
        }
        final BaseMultiFieldConverter other = (BaseMultiFieldConverter) obj;
        if (this.fieldNames != other.fieldNames && (this.fieldNames == null || !this.fieldNames.equals(other.fieldNames))){
            return false;
        }
        return true;
    }

    @Override
    public int hashCode(){
        int hash = 7;
        hash = 97 * hash + (this.fieldNames != null ? this.fieldNames.hashCode() : 0);
        return hash;
    }
}
