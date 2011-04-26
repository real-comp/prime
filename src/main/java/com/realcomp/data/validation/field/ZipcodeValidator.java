
package com.realcomp.data.validation.field;

import com.realcomp.data.annotation.Validator;
import com.realcomp.data.validation.ValidationException;
import java.util.regex.Pattern;

/**
 * Validates a 5 or 10 digit zipcode.
 * 
 * @author krenfro
 */
@Validator("validateZipcode")
public class ZipcodeValidator extends BaseFieldValidator {

    protected transient Pattern pattern = Pattern.compile("[0-9]{5}(-[0-9]{4})?"); 

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
}
