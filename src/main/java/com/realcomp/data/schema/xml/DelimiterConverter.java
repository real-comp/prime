package com.realcomp.data.schema.xml;

import com.realcomp.data.DataType;
import com.realcomp.data.record.io.Delimiter;
import com.thoughtworks.xstream.converters.enums.EnumSingleValueConverter;

/**
 * Converter for the xStream XML serialization framework.
 * 
 * @author krenfro
 */
public class DelimiterConverter extends EnumSingleValueConverter{

    public DelimiterConverter(){
        super(Delimiter.class);
    }

    @Override
    public boolean canConvert(Class type){
        return type.isAssignableFrom(Delimiter.class);
    }


    @Override
    public String toString(Object object){
        return ((Delimiter) object).toString();
    }

    @Override
    public Delimiter fromString(String s){
        return Delimiter.parse(s);
    }

}
