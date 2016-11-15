package com.realcomp.prime.conversion;

import java.util.Objects;

/**
 * behaves like String.substring(begin, end) Bad index will return empty-string.
 *
 * @author krenfro
 */
@com.realcomp.prime.annotation.Converter("substring")
public class Substring extends SimpleConverter{

    protected Integer begin = 0;
    protected Integer end;

    public Substring(){
        super();
    }

    public Substring(Integer begin){
        super();
        this.begin = begin;
    }

    public Substring(Integer begin, Integer end){
        super();
        this.begin = begin;
        this.end = end;
    }

    @Override
    public Object convert(Object value) throws ConversionException{

        if (value == null){
            throw new IllegalArgumentException("value is null");
        }

        Object retVal = "";

        try{
            if (begin == null){
                begin = 0;
            }

            if (end == null || end <= begin){
                retVal = value.toString().substring(begin);
            }
            else{
                retVal = value.toString().substring(begin, end);
            }
        }
        catch (IndexOutOfBoundsException e){
        }

        return retVal;
    }

    @Override
    public Substring copyOf(){
        return new Substring(begin, end);
    }

    public Integer getBegin(){
        return begin;
    }

    public void setBegin(Integer begin){
        this.begin = begin;
    }

    public Integer getEnd(){
        return end;
    }

    public void setEnd(Integer end){
        this.end = end;
    }

    @Override
    public int hashCode(){
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.begin);
        hash = 37 * hash + Objects.hashCode(this.end);
        return hash;
    }

    @Override
    public boolean equals(Object obj){
        if (obj == null){
            return false;
        }
        if (getClass() != obj.getClass()){
            return false;
        }
        final Substring other = (Substring) obj;
        if (!Objects.equals(this.begin, other.begin)){
            return false;
        }
        if (!Objects.equals(this.end, other.end)){
            return false;
        }
        return true;
    }
}
