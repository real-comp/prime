
package com.realcomp.data;

import java.io.Serializable;

/**
 *
 * @author krenfro
 */
public class IntegerField extends Field<Integer> implements Serializable{

    private static final long serialVersionUID = 1L;

    protected Integer value;

    protected IntegerField(){
    }

    public IntegerField(Integer value){
        if (value == null)
            throw new IllegalArgumentException("value is null");
        this.value = value;
    }

    public IntegerField(String name, Integer value){
        this(value);
        this.name = name;
    }


    @Override
    public DataType getType() {
        return DataType.INTEGER;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public void setValue(Integer value) {
        if (value == null)
            throw new IllegalArgumentException("value is null");
        this.value = value;
    }

    @Override
    public String toString(){
        return value.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final IntegerField other = (IntegerField) obj;
        if (this.value != other.value && (this.value == null || !this.value.equals(other.value)))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }
}
