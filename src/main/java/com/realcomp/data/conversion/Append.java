package com.realcomp.data.conversion;


/**
 * 
 * @author krenfro
 */
@com.realcomp.data.annotation.Converter("append")
public class Append implements Converter {

    private String value = "";    
    

    public Append(){
    }

    public Append(String value){
        this.value = value;
    }

    @Override
    public Append copyOf(){
        return new Append(value);
    }
    
    @Override
    public String convert(String value) throws ConversionException{
        if (value == null)
            throw new IllegalArgumentException("value is null");

        return value.concat(this.value);
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
        final Append other = (Append) obj;
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
