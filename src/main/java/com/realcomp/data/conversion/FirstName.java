package com.realcomp.data.conversion;

import com.realcomp.names.IndividualName;
import com.realcomp.names.Name;
import com.realcomp.names.NameParser;
import java.util.List;

/**
 * Parses a name, returning the 'first name' if the name is and instance of 
 * IndividualName; else ""
 * 
 * @author krenfro
 */
@com.realcomp.data.annotation.Converter("firstName")
public class FirstName implements Converter {

    private boolean lastNameFirst = true;
    
    @Override
    public String convert(String value) throws ConversionException{
        if (value == null)
            throw new IllegalArgumentException("value is null");

        List<Name> names = NameParser.parse(value, lastNameFirst);
        String retVal = value;        
        if (!names.isEmpty()){
            Name name = names.get(0);
            if (name instanceof IndividualName){
                retVal = ((IndividualName) name).getFirst();
            }
            else{
                retVal = "";
            }
        }
        
        return retVal == null ? "" : retVal;
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
        final FirstName other = (FirstName) obj;
        if (this.lastNameFirst != other.lastNameFirst)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + (this.lastNameFirst ? 1 : 0);
        return hash;
    }
    
    @Override
    public FirstName copyOf(){
        FirstName copy = new FirstName();
        copy.setLastNameFirst(lastNameFirst);
        return copy;
    }
    
}
