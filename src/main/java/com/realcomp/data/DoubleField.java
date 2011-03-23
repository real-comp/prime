
package com.realcomp.data;

import java.io.Serializable;

/**
 *
 * @author krenfro
 */
public class DoubleField extends Field<Double> implements Serializable{
    
    private static final long serialVersionUID = 1L;

    protected transient final DataType type = DataType.DOUBLE;
    protected Double value;
    protected String name;

    protected DoubleField(){
    }

    public DoubleField(Double value){
        if (value == null)
            throw new IllegalArgumentException("value is null");
        this.value = value;
    }

    public DoubleField(String name, Double value){
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
    public Double getValue() {
        return value;
    }

    @Override
    public void setValue(Double value) {
        if (value == null)
            throw new IllegalArgumentException("value is null");
        this.value = value;
    }
    
    @Override
    public Field get(String key) {
        throw new UnsupportedOperationException("Not supported for Field type [Double].");
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final DoubleField other = (DoubleField) obj;
        if (this.type != other.type)
            return false;
        if (this.value != other.value && (this.value == null || !this.value.equals(other.value)))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 67 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }    
}
