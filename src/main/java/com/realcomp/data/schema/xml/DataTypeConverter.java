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
        DataType type = (DataType) object;
        switch(type){
            case DOUBLE:
                return "double";
            case FLOAT:
                return "float";
            case INTEGER:
                return "int";
            case LONG:
                return "long";
            case MAP:
                return "map";
            case LIST:
                return "list";
            case NULL:
                return "null";
        }

        return "string";
    }

    @Override
    public DataType fromString(String s){

        if (s == null)
            return DataType.STRING;
        else if(s.equalsIgnoreCase(""))
            return DataType.STRING;
        else if(s.equalsIgnoreCase("string"))
            return DataType.STRING;
        else if(s.equalsIgnoreCase("int"))
            return DataType.INTEGER;
        else if(s.equalsIgnoreCase("float"))
            return DataType.FLOAT;
        else if(s.equalsIgnoreCase("long"))
            return DataType.LONG;
        else if(s.equalsIgnoreCase("double"))
            return DataType.DOUBLE;
        else if(s.equalsIgnoreCase("map"))
            return DataType.MAP;
        else if(s.equalsIgnoreCase("list"))
            return DataType.LIST;
        else if(s.equalsIgnoreCase("null"))
            return DataType.NULL;

        return null;        
    }



}
