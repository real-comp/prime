
package com.realcomp.data;

/**
 *
 * @author krenfro
 */
public class LongField extends Field<Long>{

    protected final DataType type = DataType.LONG;
    protected Long value;
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
        return type;
    }

    @Override
    public Long getValue() {
        return value;
    }

    @Override
    public void setValue(Long value) {
        if (value == null)
            throw new IllegalArgumentException("value is null");
        this.value = value;
    }

    @Override
    public Field get(String key) {
        throw new UnsupportedOperationException("Not supported for Field type [Long].");
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final LongField other = (LongField) obj;
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
