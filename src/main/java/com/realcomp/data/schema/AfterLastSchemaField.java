package com.realcomp.data.schema;

import com.realcomp.data.DataType;

/**
 * Special virtual SchemaField used as a placeholder for operations that
 * need to be executed after all the schemas fields have been processed.
 * 
 * @author krenfro
 */
public final class AfterLastSchemaField extends SchemaField {

    public AfterLastSchemaField(){
        super("AFTER LAST", DataType.STRING, 0);
    }
}
