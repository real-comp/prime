package com.realcomp.data.conversion;

import java.util.Objects;

/**
 *
 * @author krenfro
 */
@com.realcomp.data.annotation.Converter("sequence")
public class Sequence extends SimpleConverter implements NullValueConverter{

    private Long start = null;
    private Long sequence = null;

    @Override
    public Object convert(Object value) throws ConversionException {

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
        copy.sequence = sequence;
        return copy;
    }

    public Long getSequence() {
        return sequence;
    }

    public void setSequence(Long sequence) {
        this.sequence = sequence;
    }

    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    @Override
    public int hashCode(){
        int hash = 5;
        hash = 83 * hash + Objects.hashCode(this.start);
        hash = 83 * hash + Objects.hashCode(this.sequence);
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
        if (!Objects.equals(this.sequence, other.sequence)){
            return false;
        }
        return true;
    }

}
