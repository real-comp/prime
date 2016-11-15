package com.realcomp.prime.conversion;

import com.realcomp.prime.Operation;
import com.realcomp.prime.record.Record;
import java.util.Objects;


@com.realcomp.prime.annotation.Converter("filterField")
public class FilterField implements RecordConverter{

    private String field = "";
    
    public FilterField(){
    }

    public FilterField(String field){
        this.field = field;
    }
    
    public String getField(){
        return field;
    }

    public void setField(String field){
        this.field = field;
    }
    
    
    @Override
    public Record convert(Record value) throws ConversionException{
        if (value != null){
            value.remove(field);
        }
        return value;
    }

    @Override
    public Operation copyOf(){
        return new FilterField(field);
    }

    @Override
    public int hashCode(){
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.field);
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
        final FilterField other = (FilterField) obj;
        if (!Objects.equals(this.field, other.field)){
            return false;
        }
        return true;
    }
    
    

}
