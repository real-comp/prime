
package com.realcomp.data.validation.field;

import com.realcomp.data.annotation.Validator;

/**
 * Marks the value as a 'foreign key'.
 * 
 * @author krenfro
 */
@Validator("foreignKey")
public class ForeignKey extends Key {

    protected String name;

    public ForeignKey(){
    }
    
    public ForeignKey(String name){
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public ForeignKey copyOf() {
        return new ForeignKey(name);
    }
     

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final ForeignKey other = (ForeignKey) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }
}
