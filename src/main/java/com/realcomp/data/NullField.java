package com.realcomp.data;

/**
 *
 * @author krenfro
 */
public final class NullField extends Field<String>{

    protected String name;

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
}
