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
public class FirstName extends StringConverter {
    
    private boolean lastNameFirst = true;
    
    @Override
    public Object convert(Object value) throws ConversionException{
        
        String retVal = null;
        
        if (value != null){
            List<Name> names = NameParser.parse(value.toString(), lastNameFirst);
            retVal = "";
            if (!names.isEmpty()){
                Name name = names.get(0);
                if (name instanceof IndividualName){
                    retVal = ((IndividualName) name).getFirst();
                    if (retVal == null)
                        retVal = "";
                }
            }
        }
        
        return retVal;
    }

    public boolean isLastNameFirst() {
        return lastNameFirst;
    }

    public void setLastNameFirst(boolean lastNameFirst) {
        this.lastNameFirst = lastNameFirst;
    }
    
    @Override
    public FirstName copyOf(){
        FirstName copy = new FirstName();
        copy.setLastNameFirst(lastNameFirst);
        return copy;
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
        hash = 67 * hash + (this.lastNameFirst ? 1 : 0);
        return hash;
    }    
}
