package com.realcomp.prime.validation.field;

import com.realcomp.prime.DataType;
import com.realcomp.prime.annotation.Validator;
import com.realcomp.prime.conversion.ConversionException;
import com.realcomp.prime.validation.ValidationException;


@Validator("validateDoubleRange")
public class DoubleRangeValidator extends BaseFieldValidator{

    protected double min = Double.MAX_VALUE * -1d;
    protected double max = Double.MAX_VALUE;

    @Override
    public DoubleRangeValidator copyOf(){
        DoubleRangeValidator copy = new DoubleRangeValidator();
        copy.setSeverity(severity);
        copy.min = min;
        copy.max = max;
        return copy;
    }

    @Override
    public void validate(Object value) throws ValidationException{
        if (value == null){
            throw new ValidationException.Builder().message("cannot validate null Object").build();
        }
        if (min > max){
            throw new ValidationException.Builder()
                    .message(String.format("schema problem: min (%s) > max (%s)", min, max))
                    .build();
        }

        try{
            Double coerced = (Double) DataType.DOUBLE.coerce(value);
            if (Double.compare(coerced, min) < 0d){
                throw new ValidationException.Builder()
                        .message(String.format("less than minimum value of %s", min))
                        .value(value)
                        .severity(getSeverity())
                        .build();
            }
            else if (Double.compare(coerced, max) > 0d){
                throw new ValidationException.Builder()
                        .message(String.format("greater than maximum value of %s", max))
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

    public double getMax(){
        return max;
    }

    public void setMax(double max){
        this.max = max;
    }

    public double getMin(){
        return min;
    }

    public void setMin(double min){
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
        final DoubleRangeValidator other = (DoubleRangeValidator) obj;
        if (Double.doubleToLongBits(this.min) != Double.doubleToLongBits(other.min)){
            return false;
        }
        if (Double.doubleToLongBits(this.max) != Double.doubleToLongBits(other.max)){
            return false;
        }
        return true;
    }

    @Override
    public int hashCode(){
        int hash = 7;
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.min) ^ (Double.doubleToLongBits(this.min) >>> 32));
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.max) ^ (Double.doubleToLongBits(this.max) >>> 32));
        return hash;
    }
}
