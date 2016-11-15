package com.realcomp.prime.conversion;

/**
 *
 * @author krenfro
 */
@com.realcomp.prime.annotation.Converter("prepend")
public class Prepend extends SimpleConverter{

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
    public Object convert(Object o) throws ConversionException{

        Object retVal = value;

        //special converter that allows null input
        if (o != null){
            retVal = value.concat(o.toString());
        }

        return retVal;
    }

    public String getValue(){
        return value;
    }

    public void setValue(String value){
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
        final Prepend other = (Prepend) obj;
        if ((this.value == null) ? (other.value != null) : !this.value.equals(other.value)){
            return false;
        }
        return true;
    }

    @Override
    public int hashCode(){
        int hash = 5;
        hash = 47 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }
}
