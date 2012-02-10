package com.realcomp.data;

import com.realcomp.data.schema.AfterLastField;
import com.realcomp.data.schema.BeforeFirstField;
import com.realcomp.data.schema.Schema;
import com.realcomp.data.schema.Field;
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
    public static List<Operation> getOperations(Schema schema, Field field){

        List<Operation> operations = new ArrayList<Operation>();
        
        if (field instanceof AfterLastField){
            if (schema.getAfterLastOperations() != null)
                operations.addAll(schema.getAfterLastOperations());
        }
        else if (field instanceof BeforeFirstField){
            if (schema.getBeforeFirstOperations() != null)
                operations.addAll(schema.getBeforeFirstOperations());
        }
        else{
            List<Operation> temp = schema.getBeforeOperations();
            if (temp != null)
                operations.addAll(temp);

            temp = field.getOperations();
            if (temp != null)
                operations.addAll(temp);

            temp = schema.getAfterOperations();
            if (temp != null)
                operations.addAll(temp);
        }
        return operations;
    }


    
}
