
package com.realcomp.data.schema;

import com.realcomp.data.DataType;
import com.realcomp.data.schema.SchemaField;
import com.realcomp.data.validation.field.Key;

/**
 *
 * @author krenfro
 */
public class KeyField extends SchemaField{

    public KeyField(String name){
        super(name);
        addOperation(new Key());
    }

}
