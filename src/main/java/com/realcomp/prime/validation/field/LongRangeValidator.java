package com.realcomp.prime.validation.field;

import com.realcomp.prime.DataType;
import com.realcomp.prime.annotation.Validator;
import com.realcomp.prime.conversion.ConversionException;
import com.realcomp.prime.validation.ValidationException;


@Validator("validateLongRange")
public class LongRangeValidator extends BaseFieldValidator{

    protected long min = Long.MIN_VALUE;
    protected long max = Long.MAX_VALUE;

    @Override
    public void validate(Object value) throws ValidationException{

        if (value == null){
            throw new ValidationException.Builder().message("cannot validate null Object").severity(severity).build();
        }
        if (min > max){
            throw new ValidationException.Builder().message(String.format("schema problem: min (%s) > max (%s)", min, max)).build();
        }

        try{
            Long coerced = (Long) DataType.LONG.coerce(value);

            if (coerced < min){
                throw new ValidationException.Builder()
                        .message(String.format("less than minimum value of %s", min))
                        .value(value)
                        .severity(severity)
                        .build();
            }
            else if (coerced > max){
                throw new ValidationException.Builder()
                        .message(String.format("greater than maximum value of %s", max))
                                .value(value)
                                .severity(severity).build();
            }
        }
        catch (ConversionException ex){
            throw new ValidationException.Builder().message(ex.getMessage()).value(value).severity(severity).build();
        }
    }

    @Override
    public LongRangeValidator copyOf(){
        LongRangeValidator copy = new LongRangeValidator();
        copy.setSeverity(severity);
        copy.min = min;
        copy.max = max;
        return copy;
    }

    protected long parseLong(String value){

        String s = value.trim();
        //remove leading zeros
        while (s.length() > 1 & s.startsWith("0")){
            s = s.substring(1);
        }
        return Long.parseLong(s);
    }

    public long getMax(){
        return max;
    }

    public void setMax(long max){
        this.max = max;
    }

    public long getMin(){
        return min;
    }

    public void setMin(long min){
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
        final LongRangeValidator other = (LongRangeValidator) obj;
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
        int hash = 3;
        hash = 79 * hash + (int) (this.min ^ (this.min >>> 32));
        hash = 79 * hash + (int) (this.max ^ (this.max >>> 32));
        return hash;
    }
}
