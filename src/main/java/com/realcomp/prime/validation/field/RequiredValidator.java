package com.realcomp.prime.validation.field;

import com.realcomp.prime.DataType;
import com.realcomp.prime.annotation.Validator;
import com.realcomp.prime.validation.ValidationException;
import java.util.List;
import java.util.Map;

/**
 * Validates the provided value is non-empty.
 *
 */
@Validator("required")
public class RequiredValidator extends BaseFieldValidator{

    @Override
    public void validate(Object value) throws ValidationException{
        if (value == null){
            throw new ValidationException.Builder().message("cannot validate null Object").build();
        }

        DataType type = DataType.getDataType(value);
        switch (type){
            case STRING:
                if (((String) value).isEmpty()){
                    throw new ValidationException.Builder().message("required").value("").severity(getSeverity()).build();
                }
                break;
            case MAP:
                if (((Map) value).isEmpty()){
                    throw new ValidationException.Builder().message("required").value("").severity(getSeverity()).build();
                }
                break;
            case LIST:
                if (((List) value).isEmpty()){
                    throw new ValidationException.Builder().message("required").value("").severity(getSeverity()).build();
                }
                break;
        }
    }

    @Override
    public RequiredValidator copyOf(){
        RequiredValidator copy = new RequiredValidator();
        copy.setSeverity(severity);
        return copy;
    }
}
