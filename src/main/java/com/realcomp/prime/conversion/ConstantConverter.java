package com.realcomp.prime.conversion;

@com.realcomp.prime.annotation.Converter("constant")
public class ConstantConverter extends SimpleConverter{

    private Object value = "";

    public ConstantConverter(){
        super();
    }

    public ConstantConverter(Object value){
        super();
        this.value = value;
    }

    @Override
    public Object convert(Object value) throws ConversionException{

        return this.value;
    }

    @Override
    public ConstantConverter copyOf(){
        return new ConstantConverter(value);
    }

    public Object getValue(){
        return value;
    }

    public void setValue(Object value){
        this.value = value;
    }

    @Override
    public boolean equals(Object obj){
        if (obj == null){
            return false;
        }
        if (getClass() != obj.getClass()){
            return false;
        }
        final ConstantConverter other = (ConstantConverter) obj;
        return (this.value == null) ? (other.value == null) : this.value.equals(other.value);
    }

    @Override
    public int hashCode(){
        int hash = 3;
        hash = 97 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }
}
