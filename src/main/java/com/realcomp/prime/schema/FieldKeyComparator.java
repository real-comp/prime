package com.realcomp.prime.schema;

import com.realcomp.prime.Operation;
import com.realcomp.prime.validation.field.Key;
import java.util.Comparator;

/**
 * Orders Fields by their <i>key</i>'s index. Non-indexed keys are equal, and are stably sorted after indexed key
 * fields.
 *
 */
public class FieldKeyComparator implements Comparator<Field>{

    @Override
    public int compare(Field a, Field b){
        return compare(getKey(a), getKey(b));
    }

    protected Key getKey(Field field){
        Key key = null;
        for (Operation op : field.getOperations()){
            if (op instanceof com.realcomp.prime.validation.field.Key){
                key = (Key) op;
            }
        }
        return key;
    }

    protected int compare(Key a, Key b){

        int result;
        if (a == null && b == null){
            result = 0;
        }
        else if (a == null){
            result = 1;
        }
        else if (b == null){
            result = -1;
        }
        else{
            Integer indexA = a.getIndex();
            Integer indexB = a.getIndex();
            if (indexA == null && indexB == null){
                result = 0;
            }
            else if (indexA == null){
                result = 1;
            }
            else if (indexB == null){
                result = -1;
            }
            else{
                result = indexA.compareTo(indexB);
            }
        }

        return result;
    }
}
