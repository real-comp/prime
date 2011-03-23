package com.realcomp.data;

import java.io.Serializable;

/**
 *
 * @author krenfro
 */
public final class NullField extends Field<String> implements Serializable{
    
    private static final long serialVersionUID = 1L;
    protected String name;

    public NullField(){}

    public NullField(String name){
        this.name = name;
    }

    @Override
    public String getName(){
        return name;
    }

    @Override
    public void setName(String name){
        this.name = name;
    }

    @Override
    public DataType getType() {
        return DataType.NULL;
    }

    @Override
    public String getValue() {
        return null;
    }

    @Override
    public void setValue(String value) {
        throw new UnsupportedOperationException("Set not supported for NullField.");
    }

    @Override
    public Field get(String key) {
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }    
}
