package com.realcomp.prime.conversion;

/**
 * Remove any leading characters that match a value.
 * Simply a Replace converter with "^(" + value + ")+"as the regex and "" as the replacement.
 *
 * @author krenfro
 */
@com.realcomp.prime.annotation.Converter("removeLeading")
public class RemoveLeading extends SimpleConverter{

    private String value;
    private Replace replaceConverter;

    public RemoveLeading(){
        super();
    }

    public RemoveLeading(String value){
        super();
        this.value = value;
        replaceConverter = new Replace("^(" + value + ")+", "");
    }

    @Override
    public RemoveLeading copyOf(){
        return new RemoveLeading(value);
    }

    @Override
    public Object convert(Object value) throws ConversionException{

        Object retVal = value;
        if (value != null && replaceConverter != null){
            retVal = replaceConverter.convert(value);
        }
        return retVal;
    }

    public String getValue(){
        return value;
    }

    public void setValue(String value){
        this.value = value;
        replaceConverter = new Replace("^(" + value + ")+", "");

    }

    @Override
    public boolean equals(Object obj){
        if (obj == null){
            return false;
        }
        if (getClass() != obj.getClass()){
            return false;
        }
        final RemoveLeading other = (RemoveLeading) obj;
        if ((this.value == null) ? (other.value != null) : !this.value.equals(other.value)){
            return false;
        }
        return true;
    }

    @Override
    public int hashCode(){
        int hash = 7;
        hash = 17 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }
}
