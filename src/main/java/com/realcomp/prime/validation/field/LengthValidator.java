package com.realcomp.prime.validation.field;

import com.realcomp.prime.DataType;
import com.realcomp.prime.annotation.Validator;
import com.realcomp.prime.conversion.ConversionException;
import com.realcomp.prime.validation.ValidationException;

@Validator("validateLength")
public class LengthValidator extends BaseFieldValidator{

    protected int min = 0;
    protected int max = Integer.MAX_VALUE;

    @Override
    public void validate(Object value) throws ValidationException{
        if (value == null){
            throw new ValidationException.Builder().message("cannot validate null Object").build();
        }
        if (min > max){
            throw new ValidationException.Builder()
                .message(String.format("Schema problem: min (%s) > max (%s)", min, max)).build();
        }

        try{
            String coerced = (String) DataType.STRING.coerce(value);
            int length = coerced.length();

            if (length < min){
                throw new ValidationException.Builder()
                        .message(String.format("too short (min: %s)", min))
                        .value(value)
                        .severity(getSeverity())
                        .build();
            }
            else if (length > max){
                throw new ValidationException.Builder()
                        .message(String.format("too long (max: %s)", max))
                        .value(value)
                        .severity(getSeverity())
                        .build();
            }
        }
        catch (ConversionException ex){
            throw new ValidationException.Builder()
                .message(ex.getMessage())
                .cause(ex)
                .value(value)
                .severity(getSeverity())
                .build();
        }
    }

    @Override
    public LengthValidator copyOf(){
        LengthValidator copy = new LengthValidator();
        copy.setSeverity(severity);
        copy.min = min;
        copy.max = max;
        return copy;
    }

    public int getMax(){
        return max;
    }

    public void setMax(int max){
        if (max < 0){
            throw new IllegalArgumentException("max < 0");
        }
        this.max = max;
    }

    public int getMin(){
        return min;
    }

    public void setMin(int min){
        if (min < 0){
            throw new IllegalArgumentException("min < 0");
        }
        this.min = min;
    }

    @Override
    public boolean equals(Object obj){
        if (obj == null){
            return false;
        }
        if (getClass() != obj.getClass()){
            return false;
        }
        final LengthValidator other = (LengthValidator) obj;
        if (this.min != other.min){
            return false;
        }
        if (this.max != other.max){
            return false;
        }
        return true;
    }

    @Override
    public int hashCode(){
        int hash = 7;
        hash = 61 * hash + this.min;
        hash = 61 * hash + this.max;
        return hash;
    }
}
