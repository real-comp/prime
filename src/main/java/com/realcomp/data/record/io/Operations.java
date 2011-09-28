package com.realcomp.data.record.io;

import com.realcomp.data.Operation;
import com.realcomp.data.conversion.MissingFieldException;
import com.realcomp.data.schema.FileSchema;
import com.realcomp.data.schema.SchemaField;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author krenfro
 */
public class Operations {
    
    
    /**
     * 
     * @param schema
     * @param field
     * @return All operations for a field, including any <i>before</i> and <i>after</i> operations.
     */
    public static List<Operation> getOperations(FileSchema schema, SchemaField field){

        List<Operation> operations = new ArrayList<Operation>();
        List<Operation> temp = schema.getBeforeOperations();
        if (temp != null)
            operations.addAll(temp);

        temp = field.getOperations();
        if (temp != null)
            operations.addAll(temp);

        temp = schema.getAfterOperations();
        if (temp != null)
            operations.addAll(temp);
        return operations;
    }


    
}
