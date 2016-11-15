package com.realcomp.prime.validation.field;

import com.realcomp.prime.DataType;
import com.realcomp.prime.annotation.Validator;
import com.realcomp.prime.conversion.ConversionException;
import com.realcomp.prime.validation.ValidationException;
import java.util.regex.Pattern;

/**
 * Validates a 5 or 10 digit zipcode.
 *
 */
@Validator("validateZipcode")
public class ZipcodeValidator extends BaseFieldValidator{

    protected transient Pattern pattern = Pattern.compile("[0-9]{5}(-[0-9]{4})?");

    @Override
    public void validate(Object value) throws ValidationException{

        if (value == null){
            throw new ValidationException("cannot validate null Object");
        }

        try{
            String coerced = (String) DataType.STRING.coerce(value);
            if (!pattern.matcher(coerced).matches()){
                throw new ValidationException(
                        String.format("did not match pattern %s", pattern.pattern()),
                        value,
                        getSeverity());
            }
        }
        catch (ConversionException ex){
            throw new ValidationException(
                    ex.getMessage(), value, getSeverity());
        }
    }

    @Override
    public ZipcodeValidator copyOf(){
        ZipcodeValidator copy = new ZipcodeValidator();
        copy.setSeverity(severity);
        return copy;
    }
}
