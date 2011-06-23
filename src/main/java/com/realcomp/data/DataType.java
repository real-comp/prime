package com.realcomp.data;

import java.util.List;
import java.util.Map;

/**
 *
 * @author krenfro
 */
public enum DataType {

    STRING("string"), 
    INTEGER("int"),
    FLOAT("float"),
    LONG("long"),
    DOUBLE("double"),
    BOOLEAN("boolean"),
    MAP("map"),
    LIST("list");

    private String description;

    private DataType(String description){
        this.description = description;
    }

    public String getDescription(){
        return description;
    }

    public static DataType get(Object value){

        if (value == null)
            throw new IllegalArgumentException("value is null");

        if (value.getClass().isAssignableFrom(String.class))
            return STRING;
        else if(value.getClass().isAssignableFrom(Integer.class))
            return INTEGER;
        else if(value.getClass().isAssignableFrom(Float.class))
            return FLOAT;
        else if(value.getClass().isAssignableFrom(Long.class))
            return LONG;
        else if(value.getClass().isAssignableFrom(Double.class))
            return DOUBLE;
        else if(value.getClass().isAssignableFrom(Boolean.class))
            return BOOLEAN;
        else if(value.getClass().isAssignableFrom(List.class))
            return LIST;
        else if(value.getClass().isAssignableFrom(Map.class))
            return MAP;

        throw new IllegalArgumentException(
                "Unable to determine DataType for class: " + value.getClass());
    }

    public static DataType parse(String s){

        if (s == null)
            return DataType.STRING;
        else if (s.equalsIgnoreCase(""))
            return DataType.STRING;

        for (DataType d: values()){
            if (d.getDescription().equalsIgnoreCase(s))
                return d;
        }

        throw new IllegalArgumentException("Unable to convert " + s + " to a DataType");
    }

}
