
package com.realcomp.data.schema;

import com.realcomp.data.validation.field.ForeignKey;

/**
 *
 * @author krenfro
 * * @deprecated Use the ForeignKey operation
 */
public class ForeignKeyField extends SchemaField{


    public ForeignKeyField(String name){
        super(name);
        addOperation(new ForeignKey());
    }

}
