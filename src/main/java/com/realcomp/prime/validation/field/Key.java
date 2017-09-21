package com.realcomp.prime.validation.field;

import com.realcomp.prime.annotation.Validator;
import com.realcomp.prime.validation.Severity;
import com.realcomp.prime.validation.ValidationException;

/**
 * Marks the value as a 'key' field in the record, with an optional index.
 * A key field <i>can</i> be empty.
 *
 * <p>
 * <h2>Key Indexes</h2>
 * The optional <i>index</i> property of a Key can be used to specify an ordering of the keys in a schema.
 *
 * For records with multiple keys, it may be important to specify an order so Records can be joined properly.
 * </p>
 *
 */
@Validator("key")
public class Key extends BaseFieldValidator{

    protected Integer index;

    public Key(){
        super();
        severity = Severity.HIGH;
    }

    @Override
    public Key copyOf(){
        Key copy = new Key();
        copy.setSeverity(severity);
        return copy;
    }

    public Integer getIndex(){
        return index;
    }

    public void setIndex(Integer index){
        this.index = index;
    }

    @Override
    public void validate(Object value) throws ValidationException{
    }

    @Override
    public int hashCode(){
        int hash = 3;
        hash = 73 * hash + (this.index != null ? this.index.hashCode() : 0);
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
        final Key other = (Key) obj;
        return this.index == other.index || (this.index != null && this.index.equals(other.index));
    }
}
