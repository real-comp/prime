package com.realcomp.data.conversion;

import com.realcomp.data.DataType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Divide, treating arguments as Doubles.
 *
 *
 * @author krenfro
 *
 */
@com.realcomp.data.annotation.Converter("generateUniqueId")
public class GenerateUniqueId extends SimpleConverter{

    protected long id = 1;

    public GenerateUniqueId(){
    }

    @Override
    public GenerateUniqueId copyOf(){
        GenerateUniqueId copy = new GenerateUniqueId();
        copy.id = id;
        return copy;
    }

    @Override
    public Object convert(Object value) throws ConversionException{
        return id++;
    }

    @Override
    public int hashCode(){
        int hash = 7;
        hash = 53 * hash + (int) (this.id ^ (this.id >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj){
        if (obj == null){
            return false;
        }
        if (getClass() != obj.getClass()){
            return false;
        }
        final GenerateUniqueId other = (GenerateUniqueId) obj;
        if (this.id != other.id){
            return false;
        }
        return true;
    }

}
