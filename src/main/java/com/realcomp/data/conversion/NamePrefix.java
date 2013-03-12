package com.realcomp.data.conversion;

import com.realcomp.names.Name;
import com.realcomp.names.NameParser;
import java.util.List;

/**
 * Parses a name, returning the name prefix if the name is and instance of IndividualName; else ""
 *
 * @author krenfro
 */
@com.realcomp.data.annotation.Converter("namePrefix")
public class NamePrefix extends StringConverter {

    private boolean lastNameFirst = true;


    @Override
    public Object convert(Object value) throws ConversionException{

        String retVal = null;
        if (value != null){
            retVal = "";
            List<Name> names = NameParser.parse(value.toString(), lastNameFirst);
            if (!names.isEmpty()){
                Name name = names.get(0);
                if (!name.isCompanyName()){
                    retVal = name.getPrefix();
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
    public NamePrefix copyOf(){
        NamePrefix copy = new NamePrefix();
        copy.setLastNameFirst(lastNameFirst);
        return copy;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final NamePrefix other = (NamePrefix) obj;
        if (this.lastNameFirst != other.lastNameFirst)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.lastNameFirst ? 1 : 0);
        return hash;
    }
}
