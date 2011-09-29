package com.realcomp.data.conversion;


/**
 * behaves like String.substring(begin, end)
 * Bad index will return empty-string.
 * 
 * @author krenfro
 */
@com.realcomp.data.annotation.Converter("substring")
public class Substring implements Converter {

    protected Integer begin = 0;
    protected Integer end;
    
    public Substring(){
    }
    
    public Substring(Integer begin){
        this.begin = begin;
    }
    
    public Substring(Integer begin, Integer end){
        this.begin = begin;
        this.end = end;
    }
    
    @Override
    public String convert(String value) throws ConversionException{
        if (value == null)
            throw new IllegalArgumentException("value is null");

        try{
            if (begin == null)
                begin = 0;
            
            if (end == null || end <= begin)
                return value.substring(begin);
            else
                return value.substring(begin, end);
        }
        catch(IndexOutOfBoundsException e){
            return "";
        }
        
    }

    @Override
    public Substring copyOf(){
        return new Substring(begin, end);
    }

    public Integer getBegin() {
        return begin;
    }

    public void setBegin(Integer begin) {
        this.begin = begin;
    }

    public Integer getEnd() {
        return end;
    }

    public void setEnd(Integer end) {
        this.end = end;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Substring other = (Substring) obj;
        if (this.begin != other.begin && (this.begin == null || !this.begin.equals(other.begin)))
            return false;
        if (this.end != other.end && (this.end == null || !this.end.equals(other.end)))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.begin != null ? this.begin.hashCode() : 0);
        hash = 97 * hash + (this.end != null ? this.end.hashCode() : 0);
        return hash;
    }
}
