
package com.realcomp.data.validation.field;

import com.realcomp.data.DataType;
import com.realcomp.data.annotation.Validator;
import com.realcomp.data.validation.ValidationException;
import java.util.List;
import java.util.Map;

/**
 * Validates the provided value is non-empty.
 * 
 * @author krenfro
 */
@Validator("required")
public class RequiredValidator extends BaseFieldValidator {

    @Override
    public void validate(Object value) throws ValidationException{
        if (value == null)
            throw new IllegalArgumentException("value is null");
        
        DataType type = DataType.getDataType(value);
        switch(type){
            case STRING:
                if ( ((String) value).isEmpty())
                    throw new ValidationException("required", "", getSeverity());
                break;
            case MAP:
                if ( ((Map) value).isEmpty())
                    throw new ValidationException("required", "", getSeverity());
                break;
            case LIST:
                if ( ((List) value).isEmpty())
                    throw new ValidationException("required", "", getSeverity());
                break;
        }
            
    }
    
    @Override
    public RequiredValidator copyOf(){
        return new RequiredValidator();
    }
}
