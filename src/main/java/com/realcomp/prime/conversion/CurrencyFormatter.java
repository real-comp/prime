package com.realcomp.prime.conversion;

import com.realcomp.prime.DataType;
import com.realcomp.prime.Operation;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


@com.realcomp.prime.annotation.Converter("currency")
public class CurrencyFormatter implements Converter{

    private List<DataType> supportedTypes;
    private NumberFormat formatter;
    private int maxFractionDigits;
    private int minFractionDigits;


    public CurrencyFormatter(){
        this(2, 2);
    }

    public CurrencyFormatter(int minFractionDigits, int maxFractionDigits){
        formatter = NumberFormat.getCurrencyInstance(Locale.US);
        formatter.setMaximumFractionDigits(maxFractionDigits);
        formatter.setMinimumFractionDigits(minFractionDigits);
        supportedTypes = new ArrayList<>();
        supportedTypes.add(DataType.STRING);
        supportedTypes.add(DataType.INTEGER);
        supportedTypes.add(DataType.FLOAT);
        supportedTypes.add(DataType.DOUBLE);
        supportedTypes.add(DataType.LONG);
    }


    public List<DataType> getSupportedTypes(){
        return Collections.unmodifiableList(supportedTypes);
    }

    public int getMaxFractionDigits(){
        return maxFractionDigits;
    }

    public void setMaxFractionDigits(int maxFractionDigits){
        this.maxFractionDigits = maxFractionDigits;
        formatter.setMaximumFractionDigits(maxFractionDigits);
    }

    public int getMinFractionDigits(){
        return minFractionDigits;
    }

    public void setMinFractionDigits(int minFractionDigits){
        this.minFractionDigits = minFractionDigits;
        formatter.setMinimumFractionDigits(minFractionDigits);
    }

    public Object convert(Object value) throws ConversionException{
        if (value == null || value.toString().isEmpty()){
            return value;
        }

        try{
            Double amount = (Double) DataType.DOUBLE.coerce(value);
            return formatter.format(amount);
        }
        catch(IllegalArgumentException ex){
            throw new ConversionException(ex);
        }
    }

    @Override
    public Operation copyOf(){
        return new CurrencyFormatter(minFractionDigits, maxFractionDigits);
    }
}
