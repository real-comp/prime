package com.realcomp.prime;

import com.realcomp.prime.schema.AfterLastField;
import com.realcomp.prime.schema.BeforeFirstField;
import com.realcomp.prime.schema.Field;
import com.realcomp.prime.schema.Schema;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Operations{

    /**
     *
     * @param schema not null
     * @param field not null
     * @return All operations for a field, including any <i>before</i> and <i>after</i> operations. never null
     */
    public static List<Operation> getOperations(Schema schema, Field field){

        Objects.requireNonNull(schema);
        Objects.requireNonNull(field);

        List<Operation> operations = new ArrayList<>();
        List<Operation> fieldOps = field.getOperations();

        if (field instanceof AfterLastField){
            if (schema.getAfterLastOperations() != null){
                operations.addAll(schema.getAfterLastOperations());
            }
            if (fieldOps != null){
                operations.addAll(fieldOps);
            }
        }
        else if (field instanceof BeforeFirstField){
            if (schema.getBeforeFirstOperations() != null){
                operations.addAll(schema.getBeforeFirstOperations());
            }
            if (fieldOps != null){
                operations.addAll(fieldOps);
            }
        }
        else{
            if (schema.getBeforeOperations() != null){
                operations.addAll(schema.getBeforeOperations());
            }
            if (fieldOps != null){
                operations.addAll(fieldOps);
            }
            if (schema.getAfterOperations() != null){
                operations.addAll(schema.getAfterOperations());
            }
        }
        return operations;
    }
}
