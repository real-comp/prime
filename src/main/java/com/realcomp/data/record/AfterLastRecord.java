package com.realcomp.data.record;

/**
 * Special marker Record indicating that the processing is after the last Record.
 * @author krenfro
 */
public final class AfterLastRecord extends Record {

    public AfterLastRecord(){
        super();
    }


    @Override
    public String toString(){
        return "AFTER LAST RECORD";
    }
}
