
package com.realcomp.data.validation.field;

import com.realcomp.data.annotation.Validator;
import com.realcomp.data.validation.ValidationException;

/**
 * Validates the provided value as non-empty.
 * 
 * @author krenfro
 */
@Validator("required")
public class RequiredValidator extends BaseFieldValidator {

    @Override
    public void validate(String value) throws ValidationException{
        if (value == null)
            throw new IllegalArgumentException("value is null");
        if (value.isEmpty())
            throw new ValidationException("required", "", getSeverity());
    }

}
