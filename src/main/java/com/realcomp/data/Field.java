package com.realcomp.data;

import java.io.Serializable;

/**
 *
 * @author krenfro
 */
public abstract class Field<T>  implements Serializable{

    public abstract String getName();

    public abstract void setName(String name);

    public abstract DataType getType();

    public abstract T getValue();

    public abstract void setValue(T value);

    public abstract Field get(String key);
}
