package com.realcomp.prime.conversion;

/**
 * emits the default value if the value is null or empty string
 * @author krenfro
 */
@com.realcomp.prime.annotation.Converter("default")
public class Default extends StringConverter implements NullValueConverter{

    private String value = "";

    public Default(){
    }

    public Default(String value){
        this.value = value;
    }

    @Override
    public Default copyOf(){
        return new Default(value);
    }

    @Override
    public Object convert(Object o) throws ConversionException{

        Object retVal = o;

        //special converter that allows null input
        if (o == null || o.toString().isEmpty()){
            retVal = value;
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
        final Default other = (Default) obj;
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
