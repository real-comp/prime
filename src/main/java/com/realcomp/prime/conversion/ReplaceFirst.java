package com.realcomp.prime.conversion;

/**
 * Replaces the first occurrence of <i>regex</i> with <i>replacement</i>. Default <i>replacement</i> is the empty
 * string.
 *
 */
@com.realcomp.prime.annotation.Converter("replaceFirst")
public class ReplaceFirst extends SimpleConverter{

    private String regex = "";
    private String replacement = "";

    public ReplaceFirst(){
        super();
    }

    public ReplaceFirst(String regex, String replacement){
        super();
        this.regex = regex;
        this.replacement = replacement;
    }

    @Override
    public ReplaceFirst copyOf(){
        return new ReplaceFirst(regex, replacement);
    }

    @Override
    public Object convert(Object value) throws ConversionException{
        return value == null ? null : value.toString().replaceFirst(regex, replacement);
    }

    public String getRegex(){
        return regex;
    }

    public void setRegex(String regex){
        this.regex = regex;
    }

    public String getReplacement(){
        return replacement;
    }

    public void setReplacement(String replacement){
        this.replacement = replacement;
    }

    @Override
    public boolean equals(Object obj){
        if (obj == null){
            return false;
        }
        if (getClass() != obj.getClass()){
            return false;
        }
        final ReplaceFirst other = (ReplaceFirst) obj;
        if ((this.regex == null) ? (other.regex != null) : !this.regex.equals(other.regex)){
            return false;
        }
        return (this.replacement == null) ? (other.replacement == null) : this.replacement.equals(other.replacement);
    }

    @Override
    public int hashCode(){
        int hash = 3;
        hash = 31 * hash + (this.regex != null ? this.regex.hashCode() : 0);
        hash = 31 * hash + (this.replacement != null ? this.replacement.hashCode() : 0);
        return hash;
    }
}
