package com.realcomp.data.conversion;

import com.realcomp.names.CompanyNameRecognizer;
import com.realcomp.names.Name;
import com.realcomp.names.NameParser;
import java.util.List;

/**
 * Parses a name, returning the 'first name' if the name is and instance of
 * IndividualName; else ""
 *
 * @author krenfro
 */
@com.realcomp.data.annotation.Converter("isCompanyName")
public class IsCompanyName extends StringConverter {

    private boolean lastNameFirst = true;
    private boolean recognizeTrusts = true;

    @Override
    public Object convert(Object value) throws ConversionException{

        Boolean isCompany = null;

        if (value != null){
            isCompany = CompanyNameRecognizer.recognizes(value.toString());
        }

        return isCompany;
    }

    public boolean isLastNameFirst() {
        return lastNameFirst;
    }

    public void setLastNameFirst(boolean lastNameFirst) {
        this.lastNameFirst = lastNameFirst;
    }

    public boolean isRecognizeTrusts(){
        return recognizeTrusts;
    }

    public void setRecognizeTrusts(boolean recognizeTrusts){
        this.recognizeTrusts = recognizeTrusts;
    }

    @Override
    public IsCompanyName copyOf(){
        IsCompanyName copy = new IsCompanyName();
        copy.setLastNameFirst(lastNameFirst);
        copy.setRecognizeTrusts(recognizeTrusts);
        return copy;
    }

    @Override
    public int hashCode(){
        int hash = 3;
        hash = 13 * hash + (this.lastNameFirst ? 1 : 0);
        hash = 13 * hash + (this.recognizeTrusts ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj){
        if (obj == null){
            return false;
        }
        if (getClass() != obj.getClass()){
            return false;
        }
        final IsCompanyName other = (IsCompanyName) obj;
        if (this.lastNameFirst != other.lastNameFirst){
            return false;
        }
        if (this.recognizeTrusts != other.recognizeTrusts){
            return false;
        }
        return true;
    }
}
