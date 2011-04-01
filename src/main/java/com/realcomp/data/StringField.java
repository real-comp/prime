package com.realcomp.data;

import java.io.Serializable;

/**
 *
 * @author krenfro
 */
public class StringField extends Field<String> implements Serializable{

    private static final long serialVersionUID = 1L;

    protected String value;

    protected StringField(){
    }

    public StringField(String value){
        if (value == null)
            throw new IllegalArgumentException("value is null");
        this.value = value;
    }

    public StringField(String name, String value){
        this(value);
        this.name = name;
    }

    @Override
    public DataType getType() {
        return DataType.STRING;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        if (value == null)
            throw new IllegalArgumentException("value is null");
        this.value = value;
    }

    @Override
    public String toString(){
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final StringField other = (StringField) obj;
        if ((this.value == null) ? (other.value != null) : !this.value.equals(other.value))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }
}
