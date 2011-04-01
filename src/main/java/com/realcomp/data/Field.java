package com.realcomp.data;

/**
 *
 * @author krenfro
 */
public abstract class Field<T>{

    protected String name;

    public String getName(){
        return name;
    }

    /**
     * @param name the name of the Field; not null
     */
    public void setName(String name){
        if (name == null)
            throw new IllegalArgumentException("name is null");
        this.name = name;
    }

    /**
     * @return the DataType for this Field
     */
    public abstract DataType getType();

    public abstract T getValue();

    public abstract void setValue(T value);
}
