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

        if (String.class.isAssignableFrom(value.getClass()))
            return STRING;
        else if (Integer.class.isAssignableFrom(value.getClass()))
            return INTEGER;
        else if (Float.class.isAssignableFrom(value.getClass()))
            return FLOAT;
        else if (Long.class.isAssignableFrom(value.getClass()))
            return LONG;
        else if (Double.class.isAssignableFrom(value.getClass()))
            return DOUBLE;
        else if (Boolean.class.isAssignableFrom(value.getClass()))
            return BOOLEAN;
        else if (List.class.isAssignableFrom(value.getClass()))
            return LIST;
        else if (Map.class.isAssignableFrom(value.getClass()))
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
