package com.realcomp.prime.conversion;


@com.realcomp.prime.annotation.Converter("append")
public class Append extends SimpleConverter implements NullValueConverter{

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
    public Object convert(Object o) throws ConversionException{

        Object retVal = value;

        //special converter that allows null input
        if (o != null){
            retVal = o.toString().concat(value);
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
        final Append other = (Append) obj;
        return (this.value == null) ? (other.value == null) : this.value.equals(other.value);
    }

    @Override
    public int hashCode(){
        int hash = 5;
        hash = 47 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }
}
