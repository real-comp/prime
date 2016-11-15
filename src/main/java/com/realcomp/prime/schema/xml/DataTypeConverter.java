package com.realcomp.prime.schema.xml;

import com.realcomp.prime.DataType;
import com.thoughtworks.xstream.converters.enums.EnumSingleValueConverter;

/**
 * Converter for the xStream XML serialization framework. Default DataType = STRING
 *
 * @author krenfro
 */
public class DataTypeConverter extends EnumSingleValueConverter{

    public DataTypeConverter(){
        super(DataType.class);
    }

    @Override
    public boolean canConvert(Class type){
        return type.isAssignableFrom(DataType.class);
    }

    @Override
    public String toString(Object object){
        if (object == null || object.equals(DataType.STRING)){
            return null;
        }
        else{
            return ((DataType) object).getDescription();
        }
    }

    @Override
    public DataType fromString(String s){
        return s == null ? DataType.STRING : DataType.parse(s);
    }
}
