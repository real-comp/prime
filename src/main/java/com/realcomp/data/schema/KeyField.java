
package com.realcomp.data.schema;

import com.realcomp.data.validation.field.Key;

/**
 *
 * @author krenfro
 * @deprecated Use the Key operation
 */
public class KeyField extends SchemaField{

    public KeyField(String name){
        super(name);
        addOperation(new Key());
    }
}
