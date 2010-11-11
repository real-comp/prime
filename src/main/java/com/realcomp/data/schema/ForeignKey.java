package com.realcomp.data.schema;

/**
 *
 * @author krenfro
 */
public class ForeignKey extends Key{

    protected ForeignKey(){
    }

    public ForeignKey(String name){
        if (name == null)
            throw new IllegalArgumentException("name is null");
        this.name = name;
    }

}
