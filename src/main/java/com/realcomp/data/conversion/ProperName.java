package com.realcomp.data.conversion;

import com.realcomp.names.Name;
import com.realcomp.names.NameParser;
import java.util.List;

/**
 * Parses a name, returning a clean up version. If it is an IndividualName,
 * then format is prefix first middle last suffix; else if CompanyName, then the cleaned 
 * company name is used.
 * 
 * <p>
 * Only one name is returned, so if multiple names are in the input field, that
 * information will be lost.  For example, "Jeanine" would be lost if formatting
 * "Renfro, Ryan Kyle & Jeanine"
 * </p>
 * 
 * @author krenfro
 */
@com.realcomp.data.annotation.Converter("properName")
public class ProperName implements Converter {
    
    private boolean lastNameFirst = true;
    
    @Override
    public String convert(String value) throws ConversionException{
        if (value == null)
            throw new IllegalArgumentException("value is null");

        List<Name> names = NameParser.parse(value, lastNameFirst);
        String retVal = value;        
        if (!names.isEmpty())
            retVal = names.get(0).toString();        
        return retVal;
    }
    
    @Override
    public ProperName copyOf(){
        ProperName copy = new ProperName();
        copy.setLastNameFirst(lastNameFirst);
        return copy;
    }

    public boolean isLastNameFirst() {
        return lastNameFirst;
    }

    public void setLastNameFirst(boolean lastNameFirst) {
        this.lastNameFirst = lastNameFirst;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final ProperName other = (ProperName) obj;
        if (this.lastNameFirst != other.lastNameFirst)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + (this.lastNameFirst ? 1 : 0);
        return hash;
    }
    
}
