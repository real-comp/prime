package com.realcomp.data;

/**
 *
 * @author krenfro
 */
public abstract class Field<T>{

    public abstract String getName();

    public abstract void setName(String name);

    public abstract DataType getType();

    public abstract T getValue();

    public abstract void setValue(T value);

    public abstract Field get(String key);
}
