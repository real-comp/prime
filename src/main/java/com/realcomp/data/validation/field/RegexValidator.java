
package com.realcomp.data.validation.field;

import com.realcomp.data.annotation.Validator;
import com.realcomp.data.validation.ValidationException;
import java.util.regex.Pattern;

/**
 * Validates the provided value as non-empty.
 * 
 * @author krenfro
 */
@Validator("validateRegex")
public class RegexValidator extends BaseFieldValidator {

    protected String regex = null;
    protected transient Pattern pattern = Pattern.compile(".*"); //default match anything

    @Override
    public void validate(String value) throws ValidationException{
        if (value == null)
            throw new IllegalArgumentException("value is null");

        if (!pattern.matcher(value).matches()){
            throw new ValidationException(
                    String.format("did not match pattern %s", pattern.pattern()), 
                    value,
                    getSeverity());
        }
    }    
    
    @Override
    public RegexValidator copyOf(){
        RegexValidator copy = new RegexValidator();
        copy.setRegex(regex);
        return copy;
    }

    public String getRegex() {
        return regex == null ? ".*" : regex;
    }

    public void setRegex(String regex) {
        if (regex == null)
            throw new IllegalArgumentException("regex is null");
        this.regex = regex;
        this.pattern = Pattern.compile(regex);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final RegexValidator other = (RegexValidator) obj;
        if (this.regex != other.regex && (this.regex == null || !this.regex.equals(other.regex)))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + (this.regex != null ? this.regex.hashCode() : 0);
        return hash;
    }


}
