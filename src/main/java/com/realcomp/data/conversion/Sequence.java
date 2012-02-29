package com.realcomp.data.conversion;

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
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Sequence other = (Sequence) obj;
        if (this.start != other.start && (this.start == null || !this.start.equals(other.start)))
            return false;
        if (this.sequence != other.sequence && (this.sequence == null || !this.sequence.equals(other.sequence)))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (this.start != null ? this.start.hashCode() : 0);
        hash = 41 * hash + (this.sequence != null ? this.sequence.hashCode() : 0);
        return hash;
    }
}
