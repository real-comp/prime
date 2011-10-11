package com.realcomp.data.conversion;

import com.realcomp.names.CompanyName;
import com.realcomp.names.IndividualName;
import com.realcomp.names.Name;
import com.realcomp.names.NameFormatter;
import com.realcomp.names.NameParser;
import java.util.List;

/**
 * Parses a name, returning a clean up version. If it is an IndividualName,
 * then format is last, prefix first middle, suffix; else if CompanyName, then the cleaned 
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
@com.realcomp.data.annotation.Converter("lastNameFirst")
public class LastNameFirst implements Converter {

    private boolean lastNameFirst = true;
    
    
    @Override
    public String convert(String value) throws ConversionException{
        if (value == null)
            throw new IllegalArgumentException("value is null");

        List<Name> names = NameParser.parse(value, lastNameFirst);
        String retVal = value;        
        if (!names.isEmpty())
            retVal = NameFormatter.getLastNameFirst(names.get(0));
        return retVal;
    }
    
    @Override
    public LastNameFirst copyOf(){
        LastNameFirst copy = new LastNameFirst();
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
        final LastNameFirst other = (LastNameFirst) obj;
        if (this.lastNameFirst != other.lastNameFirst)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.lastNameFirst ? 1 : 0);
        return hash;
    }
    
    

}
