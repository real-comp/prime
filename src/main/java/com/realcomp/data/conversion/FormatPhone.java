package com.realcomp.data.conversion;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author krenfro
 */
@com.realcomp.data.annotation.Converter("formatPhone")
public class FormatPhone extends SimpleConverter {

    
    @Override
    public Object convert(Object value) throws ConversionException{
        if (value == null)
            throw new IllegalArgumentException("value is null");
        
        Character[] digits = getDigits(value.toString());
        StringBuilder phone = new StringBuilder();
        if (digits.length == 11){
            phone.append("(").append(digits[1]).append(digits[2]).append(digits[3]).append(") ");
            phone.append(digits[4]).append(digits[5]).append(digits[6]).append("-");
            phone.append(digits[7]).append(digits[8]).append(digits[9]).append(digits[10]);
        }
        else if (digits.length == 10){
            phone.append("(").append(digits[0]).append(digits[1]).append(digits[2]).append(") ");
            phone.append(digits[3]).append(digits[4]).append(digits[5]).append("-");
            phone.append(digits[6]).append(digits[7]).append(digits[8]).append(digits[9]);
        }
        else if (digits.length == 7){
            phone.append(digits[0]).append(digits[1]).append(digits[2]).append("-");
            phone.append(digits[3]).append(digits[4]).append(digits[5]).append(digits[6]);
        }
        else{
            phone.append(value);
        }
                
        return phone.toString();
    }
    
    
    private Character[] getDigits(String phone){
        List<Character> digits = new ArrayList<Character>();
        
        for (char c: phone.toCharArray()){
            if (c >= '0' && c <= '9')
                digits.add(c);
        }
        
        return digits.toArray(new Character[digits.size()]);
    }

    @Override
    public FormatPhone copyOf() {
        return new FormatPhone();
    }
    
    
    @Override
    public boolean equals(Object other) {
        return (other instanceof FormatPhone);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    
    
}
