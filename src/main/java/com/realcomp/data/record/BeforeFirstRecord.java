package com.realcomp.data.record;

/**
* Special marker Record indicating that the processing is before the first Record.
 * @author krenfro
 */
public final class BeforeFirstRecord extends Record {

    public BeforeFirstRecord(){
        super();
    }


    @Override
    public String toString(){
        return "BEFORE FIRST RECORD";
    }
}
