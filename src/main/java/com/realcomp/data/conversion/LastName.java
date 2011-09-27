package com.realcomp.data.conversion;

import com.realcomp.names.CompanyName;
import com.realcomp.names.IndividualName;
import com.realcomp.names.Name;
import com.realcomp.names.NameFormatter;
import com.realcomp.names.NameParser;
import java.util.List;

/**
 * Parses a name, returning the 'last name' if the name is and instance of 
 * IndividualName; else return the cleaned up company name.
 * 
 * @author krenfro
 */
@com.realcomp.data.annotation.Converter("lastName")
public class LastName extends SimpleConverter {

    
    @Override
    public Object convert(Object value) throws ConversionException{
        if (value == null)
            throw new IllegalArgumentException("value is null");

        String retVal = value.toString();        
        List<Name> names = NameParser.parse(retVal);
        
        if (!names.isEmpty()){
            Name name = names.get(0);
            if (name instanceof IndividualName){
                retVal = ((IndividualName) name).getLast();
            }
            else{
                retVal = ((CompanyName) name).toString();
            }
        }
        
        return retVal == null ? "" : retVal;
    }
    
    @Override
    public LastName copyOf(){
        return new LastName();
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof LastName);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }
}
