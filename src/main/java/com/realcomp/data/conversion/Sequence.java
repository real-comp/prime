package com.realcomp.data.conversion;

import java.util.Objects;

/**
 * Uses static variable to hold the sequence number.  This will cause problems if multiple
 * sequences are used in a schema.
 *
 * @author krenfro
 */
@com.realcomp.data.annotation.Converter("sequence")
public class Sequence extends SimpleConverter implements NullValueConverter{

    private Long start = null;
    private static Long sequence = null;

    @Override
    public Object convert(Object value) throws ConversionException{

        if (sequence == null){
            sequence = start == null ? 1l : start;
        }

        Object result = sequence;
        sequence++;
        return result;
    }

    @Override
    public Sequence copyOf(){
        Sequence copy = new Sequence();
        copy.start = start;
        return copy;
    }


    public Long getStart(){
        return start;
    }

    public void setStart(Long start){
        this.start = start;
    }

    @Override
    public int hashCode(){
        int hash = 5;
        hash = 83 * hash + Objects.hashCode(this.start);
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
        final Sequence other = (Sequence) obj;
        if (!Objects.equals(this.start, other.start)){
            return false;
        }
        return true;
    }
}
