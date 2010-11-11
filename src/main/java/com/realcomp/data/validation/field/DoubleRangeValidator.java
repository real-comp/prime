package com.realcomp.data.validation.field;

import com.realcomp.data.annotation.Validator;
import com.realcomp.data.validation.ValidationException;


/**
 *
 * @author krenfro
 */
@Validator("validateDoubleRange")
public class DoubleRangeValidator extends BaseFieldValidator {

    protected double min = Double.MAX_VALUE * -1d;
    protected double max = Double.MAX_VALUE;

    @Override
    public void validate(String value) throws ValidationException{
        if (value == null)
            throw new IllegalArgumentException("value is null");
        if (min > max)
            throw new IllegalStateException(String.format("min (%s) > max (%s)", min, max));

        try{
            double parsed = parseDouble(value);
            if (Double.compare(parsed, min) < 0d){
                throw new ValidationException(
                        String.format("less than minimum value of %s", min), 
                        value,
                        getSeverity());
            }
            else if(Double.compare(parsed, max) > 0d){
                throw new ValidationException(
                        String.format("greater than maximum value of %s", max), 
                        value,
                        getSeverity());
            }
        }
        catch(NumberFormatException ex){
            throw new ValidationException(ex.getMessage(), value, getSeverity());
        }
    }

    protected double parseDouble(String value){

        String s = value.trim();
        //remove leading zeros
        while (s.length() > 1 & s.startsWith("0"))
            s = s.substring(1);
        if (s.startsWith("\\."))
            s = "0".concat(s);
        return Double.parseDouble(s);
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final DoubleRangeValidator other = (DoubleRangeValidator) obj;
        if (Double.doubleToLongBits(this.min) != Double.doubleToLongBits(other.min))
            return false;
        if (Double.doubleToLongBits(this.max) != Double.doubleToLongBits(other.max))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.min) ^ (Double.doubleToLongBits(this.min) >>> 32));
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.max) ^ (Double.doubleToLongBits(this.max) >>> 32));
        return hash;
    }

}
