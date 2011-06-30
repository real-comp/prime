package com.realcomp.data.conversion;

/**
 * Special converter marks a field as having an alias.
 * A SchemaField can have multiple aliases.
 * No conversion of values is performed by this converter, it is simply used by the 
 * Record when resolving values.
 * 
 * @author krenfro
 */
@com.realcomp.data.annotation.Converter("alias")
public class Alias implements Converter{

    protected String name;
    
    @Override
    public String convert(String value) throws ConversionException{
        return value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Alias other = (Alias) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 61 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }
}
