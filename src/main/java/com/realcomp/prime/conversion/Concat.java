package com.realcomp.prime.conversion;

import com.realcomp.prime.record.Record;
import com.realcomp.prime.record.RecordKeyException;
import java.util.List;

/**
 *
 * @author krenfro
 */
@com.realcomp.prime.annotation.Converter("concat")
public class Concat extends BaseMultiFieldConverter implements NullValueConverter{

    private String delimiter = "";

    public Concat(){
        super();
    }

    public Concat(List<String> fieldNames){
        super(fieldNames);
    }

    @Override
    public Object convert(Object value, Record record) throws ConversionException{

        if (record == null){
            throw new IllegalArgumentException("record is null");
        }

        //special converter that allows null input
        if (value == null){
            value = "";
        }

        String retVal = "".concat(value.toString());
        boolean needDelimiter = false;
        for (String fieldName : fieldNames){

            if (needDelimiter){
                retVal = retVal.concat(delimiter);
            }
            needDelimiter = true;

            try{
                Object temp = record.get(fieldName);
                if (temp != null){
                    retVal = retVal.concat(temp.toString());
                }
            }
            catch (RecordKeyException ex){
                throw new MissingFieldException(fieldName, ex);
            }
        }
        return retVal;
    }

    @Override
    public Concat copyOf(){
        Concat copy = new Concat();
        copy.delimiter = delimiter;
        copy.setFields(fieldNames);
        return copy;
    }

    public String getDelimiter(){
        return delimiter;
    }

    public void setDelimiter(String delimiter){
        this.delimiter = delimiter;
    }

    @Override
    public boolean equals(Object obj){
        if (obj == null){
            return false;
        }
        if (getClass() != obj.getClass()){
            return false;
        }
        final Concat other = (Concat) obj;
        if ((this.delimiter == null) ? (other.delimiter != null) : !this.delimiter.equals(other.delimiter)){
            return false;
        }

        return super.equals(obj);
    }

    @Override
    public int hashCode(){
        int hash = super.hashCode();
        hash = 11 * hash + (this.delimiter != null ? this.delimiter.hashCode() : 0);
        return hash;
    }
}
