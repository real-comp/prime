package com.realcomp.data.schema;

import com.realcomp.data.DataType;

/**
 * Special virtual Field used as a placeholder for operations that
 * need to be executed before any of the schemas fields have been processed.
 * 
 * @author krenfro
 */
public final class BeforeFirstField extends Field {

    public BeforeFirstField(){
        super("BEFORE FIRST", DataType.STRING, 0);
    }
}
