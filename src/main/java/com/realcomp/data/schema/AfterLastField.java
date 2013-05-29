package com.realcomp.data.schema;

import com.realcomp.data.DataType;

/**
 * Special virtual Field used as a placeholder for operations that need to be executed after all the schemas fields have
 * been processed.
 *
 * @author krenfro
 */
public final class AfterLastField extends Field{

    public AfterLastField(){
        super("AFTER LAST", DataType.STRING, 0);
    }
}
