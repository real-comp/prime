
package com.realcomp.data.validation.file;

import com.realcomp.data.validation.field.*;
import com.realcomp.data.annotation.Validator;
import com.realcomp.data.validation.ValidationException;

/**
 * Validates the provided value as non-empty.
 * 
 * @author krenfro
 */
@Validator("validateRecordCount")
public class RecordCountValidator extends BaseFieldValidator {

    protected long min = Long.MIN_VALUE;
    protected long max = Long.MAX_VALUE;

    
    @Override
    public void validate(String value) throws ValidationException{


        if (value == null)
            throw new IllegalArgumentException("value is null");
        if (min > max)
            throw new IllegalStateException(String.format("min (%s) > max (%s)", min, max));

        try{
            long parsed = parseLong(value);
            if (parsed < min){
                throw new ValidationException(
                        String.format(
                            "There were fewer records than the minimum of %s", min),
                            value,
                            getSeverity());
            }
            else if(parsed > max){
                throw new ValidationException(
                        String.format(
                            "There were more records than the maximum value of %s", max),
                            value,
                            getSeverity());
            }
        }
        catch(NumberFormatException ex){
            throw new ValidationException(ex.getMessage(), value, getSeverity());
        }
    }



    protected long parseLong(String value){

        String s = value.trim();
        //remove leading zeros
        while (s.length() > 1 & s.startsWith("0"))
            s = s.substring(1);
        return Long.parseLong(s);
    }

    public long getMax() {
        return max;
    }

    public void setMax(long max) {
        this.max = max;
    }

    public long getMin() {
        return min;
    }

    public void setMin(long min) {
        this.min = min;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final RecordCountValidator other = (RecordCountValidator) obj;
        if (this.min != other.min)
            return false;
        if (this.max != other.max)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + (int) (this.min ^ (this.min >>> 32));
        hash = 47 * hash + (int) (this.max ^ (this.max >>> 32));
        return hash;
    }

}
