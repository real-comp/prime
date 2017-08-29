package com.realcomp.prime.record.io;

import com.realcomp.prime.MultiFieldOperation;
import com.realcomp.prime.Operation;
import com.realcomp.prime.schema.Field;
import com.realcomp.prime.validation.Validator;
import com.realcomp.prime.validation.field.ForeignKey;
import com.realcomp.prime.validation.field.Key;

import java.util.Comparator;

public class FieldParsePlanComparator implements Comparator<Field>{


    /**
     * Determines if one of the Operations in a Field is a MultiFieldOperation.
     *
     * @param field
     * @return true if the Field contains a MultiFieldOperation.
     */
    private boolean hasMultiFieldOperation(Field field){
        if (field.getOperations() != null){
            for (Operation op : field.getOperations()){
                if (op instanceof MultiFieldOperation){
                    return true;
                }
            }
        }
        return false;
    }

    private int score(Field field){
        int score = 0;
        if (field != null){
            for (Operation op : field.getOperations()){
                if (op instanceof Key){
                    score -= 10;
                }
                else if (op instanceof ForeignKey){
                    score -= 9;
                }
                else if (op instanceof MultiFieldOperation){
                    score += 10;
                }
                else if (op instanceof Validator){
                    score++;
                }
            }
        }
        return score;
    }

    @Override
    public int compare(Field a, Field b){
        return Integer.compare(score(a), score(b));
    }
}
