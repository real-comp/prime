package com.realcomp.data.conversion;

/**
 *
 * @author krenfro
 */
@com.realcomp.data.annotation.Converter("constant")
public class ConstantConverter extends SimpleConverter{

    protected Object value = "";
    
    public ConstantConverter(){
        super();
    }
    
    public ConstantConverter(Object value){
        super();
        this.value = value;
    }
    

    @Override
    public Object convert(Object value) throws ConversionException{

        if (value == null)
            throw new IllegalArgumentException("value is null");

        return this.value;
    }
    
    @Override
    public ConstantConverter copyOf(){
        return new ConstantConverter(value);
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final ConstantConverter other = (ConstantConverter) obj;
        if ((this.value == null) ? (other.value != null) : !this.value.equals(other.value))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }
}
