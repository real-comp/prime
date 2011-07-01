package com.realcomp.data.conversion;


/**
 * Replaces all occurrences of <i>regex</i> with <i>replacement</i>.
 * Default <i>replacement</i> is the empty string.
 *
 * @author krenfro
 */
@com.realcomp.data.annotation.Converter("replace")
public class Replace implements Converter {

    private String regex = "";    
    private String replacement = "";

    public Replace(){
    }

    public Replace(String regex, String replacement){
        this.regex = regex;
        this.replacement = replacement;
    }

    @Override
    public Replace copyOf(){
        Replace copy = new Replace();
        copy.setRegex(regex);
        copy.setReplacement(replacement);
        return copy;
    }
    
    @Override
    public String convert(String value) throws ConversionException{
        if (value == null)
            throw new IllegalArgumentException("value is null");

        return value.replaceAll(regex, replacement);
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public String getReplacement() {
        return replacement;
    }

    public void setReplacement(String replacement) {
        this.replacement = replacement;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Replace other = (Replace) obj;
        if ((this.regex == null) ? (other.regex != null) : !this.regex.equals(other.regex))
            return false;
        if ((this.replacement == null) ? (other.replacement != null) : !this.replacement.equals(other.replacement))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 31 * hash + (this.regex != null ? this.regex.hashCode() : 0);
        hash = 31 * hash + (this.replacement != null ? this.replacement.hashCode() : 0);
        return hash;
    }
}
