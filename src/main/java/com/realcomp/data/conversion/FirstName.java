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
public class FirstName extends SimpleConverter {

    
    @Override
    public Object convert(Object value) throws ConversionException{
        if (value == null)
            throw new IllegalArgumentException("value is null");

        String retVal = value.toString();        
        List<Name> names = NameParser.parse(retVal);
        
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
    
    @Override
    public FirstName copyOf(){
        return new FirstName();
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof FirstName);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }
}
