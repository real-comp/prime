package com.realcomp.prime.conversion;

import java.util.regex.Pattern;

/**
 * Replaces all occurrences of <i>regex</i> with <i>replacement</i>. Default <i>replacement</i> is the empty string.
 * Multiple passes will be performed until the regex is not found.
 *
 */
@com.realcomp.prime.annotation.Converter("replace")
public class Replace extends SimpleConverter{

    private String regex = "";
    private String replacement = "";
    private Pattern pattern = null;
    private boolean emptyPattern = true;

    public Replace(){
        super();
        pattern = Pattern.compile(regex);
        emptyPattern = regex.equals("");
    }

    public Replace(String regex, String replacement){
        super();
        this.regex = regex;
        this.replacement = replacement;
        pattern = Pattern.compile(regex);
        emptyPattern = regex.equals("");
    }

    @Override
    public Replace copyOf(){
        Replace copy = new Replace(regex, replacement);
        return copy;
    }

    @Override
    public Object convert(Object value) throws ConversionException{

        Object retVal = value;

        if (value != null){
            String s = value.toString();
            if (emptyPattern){
                if (pattern.matcher(s).matches()){
                    s = replacement;
                }
            }
            else{
                if (pattern.matcher(s).find()){
                    s = s.replaceAll(regex, replacement);
                }
            }
            retVal = s;
        }

        return retVal;
    }

    public String getRegex(){
        return regex;
    }

    public void setRegex(String regex){
        this.regex = regex;
        pattern = Pattern.compile(regex);
        emptyPattern = regex.equals("");
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
        final Replace other = (Replace) obj;
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
