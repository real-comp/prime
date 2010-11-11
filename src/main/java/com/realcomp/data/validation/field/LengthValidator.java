
package com.realcomp.data.validation.field;

import com.realcomp.data.annotation.Validator;
import com.realcomp.data.validation.ValidationException;


/**
 *
 * @author krenfro
 */
@Validator("validateLength")
public class LengthValidator extends BaseFieldValidator {

    protected int min = 0;
    protected int max = Integer.MAX_VALUE;

    @Override
    public void validate(String value) throws ValidationException{
        if (value == null)
            throw new IllegalArgumentException("value is null");
        if (min > max)
            throw new IllegalStateException(String.format("min (%s) > max (%s)", min, max));

        int length = value.length();

        if (length < min){
            throw new ValidationException(
                    String.format("too short (min: %s)", min), value, getSeverity());
        }
        else if (length > max){
            throw new ValidationException(
                    String.format("too long (max: %s)", max), value, getSeverity());
        }
    }


    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        if (max < 0)
            throw new IllegalArgumentException("max < 0");
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        if (min < 0)
            throw new IllegalArgumentException("min < 0");
        this.min = min;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final LengthValidator other = (LengthValidator) obj;
        if (this.min != other.min)
            return false;
        if (this.max != other.max)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + this.min;
        hash = 61 * hash + this.max;
        return hash;
    }

}
