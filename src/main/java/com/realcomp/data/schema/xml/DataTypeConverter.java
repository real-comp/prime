package com.realcomp.data.schema.xml;

import com.realcomp.data.DataType;
import com.thoughtworks.xstream.converters.enums.EnumSingleValueConverter;

/**
 * Converter for the xStream XML serialization framework.
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
        return ((DataType) object).getDescription();

    }

    @Override
    public DataType fromString(String s){
        return DataType.parse(s);
    }



}
