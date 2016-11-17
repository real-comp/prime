package com.realcomp.prime.validation.field;

import com.realcomp.prime.annotation.Validator;
import com.realcomp.prime.validation.ValidationException;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Validates the provided value as non-empty.
 *
 */
@Validator("validateRegex")
public class RegexValidator extends BaseFieldValidator{

    private static final Logger logger = Logger.getLogger(RegexValidator.class.getName());

    protected String regex = null;
    protected transient Pattern pattern = Pattern.compile(".*"); //default match anything
    protected Boolean inverse = null;

    public RegexValidator(){
    }

    public RegexValidator(String regex){
        if (regex == null){
            throw new IllegalArgumentException("regex is null");
        }
        this.regex = regex;
        this.pattern = Pattern.compile(regex);
    }

    @Override
    public void validate(Object value) throws ValidationException{
        if (value == null){
            throw new ValidationException.Builder().message("cannot validate null Object").build();
        }

        boolean matches = pattern.matcher(value.toString()).matches();
        if (inverse != null && inverse){
            matches = !matches;
        }
        if (!matches){
            throw new ValidationException.Builder()
                    .message(String.format("pattern [%s] did not match", pattern.pattern()))
                    .value(value)
                    .severity(getSeverity())
                    .build();
        }
    }

    @Override
    public RegexValidator copyOf(){
        RegexValidator copy = new RegexValidator();
        copy.setSeverity(severity);
        copy.setRegex(regex);
        copy.setInverse(inverse);
        return copy;
    }

    public String getRegex(){
        return regex == null ? ".*" : regex;
    }

    public void setRegex(String regex){
        if (regex == null){
            throw new IllegalArgumentException("regex is null");
        }
        this.regex = regex;
        this.pattern = Pattern.compile(regex);
    }

    public Boolean getInverse(){
        return inverse;
    }

    public void setInverse(Boolean inverse){
        this.inverse = inverse;
    }

    @Override
    public int hashCode(){
        int hash = 3;
        hash = 23 * hash + Objects.hashCode(this.regex);
        hash = 23 * hash + Objects.hashCode(this.inverse);
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
        final RegexValidator other = (RegexValidator) obj;
        if (!Objects.equals(this.regex, other.regex)){
            return false;
        }
        if (!Objects.equals(this.inverse, other.inverse)){
            return false;
        }
        return true;
    }
}
