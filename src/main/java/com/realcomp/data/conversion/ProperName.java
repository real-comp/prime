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
public class ProperName extends SimpleConverter {
    
    @Override
    public Object convert(Object value) throws ConversionException{
        if (value == null)
            throw new IllegalArgumentException("value is null");

        String retVal = value.toString();        
        List<Name> names = NameParser.parse(retVal);        
        if (!names.isEmpty())
            retVal = names.get(0).toString();        
        return retVal;
    }
    
    @Override
    public ProperName copyOf(){
        return new ProperName();
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof ProperName);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }
}
