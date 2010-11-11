package com.realcomp.data.schema;

/**
 *
 * @author krenfro
 */
public class Key {

    protected String name;

    protected Key(){
    }

    public Key(String name){
        if (name == null)
            throw new IllegalArgumentException("name is null");
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null)
            throw new IllegalArgumentException("name is null");
        
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Key other = (Key) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

}
