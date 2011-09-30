package com.realcomp.data.conversion;


/**
 * 
 * @author krenfro
 */
@com.realcomp.data.annotation.Converter("prepend")
public class Prepend implements Converter {

    private String value = "";    
    

    public Prepend(){
    }

    public Prepend(String value){
        this.value = value;
    }

    @Override
    public Prepend copyOf(){
        return new Prepend(value);
    }
    
    @Override
    public String convert(String value) throws ConversionException{
        if (value == null)
            throw new IllegalArgumentException("value is null");

        return this.value.concat(value);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Prepend other = (Prepend) obj;
        if ((this.value == null) ? (other.value != null) : !this.value.equals(other.value))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }
    
}
