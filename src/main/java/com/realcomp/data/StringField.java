package com.realcomp.data;

import java.io.Serializable;

/**
 *
 * @author krenfro
 */
public class StringField extends Field<String> implements Serializable{

    private static final long serialVersionUID = 1L;

    protected static final DataType type = DataType.STRING;
    protected String value;
    protected String name;

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
    public String getName(){
        return name;
    }

    @Override
    public void setName(String name){
        this.name = name;
    }

    @Override
    public DataType getType() {
        return type;
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
    public Field get(String key) {
        throw new UnsupportedOperationException("Not supported for Field type [String].");
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
