package com.realcomp.prime.schema;

import com.realcomp.prime.DataType;

/**
 * Special virtual Field used as a placeholder for operations that need to be executed before any of the schemas fields
 * have been processed.
 *
 */
public final class BeforeFirstField extends Field{

    public BeforeFirstField(){
        super("BEFORE FIRST", DataType.STRING, 0);
    }
}
