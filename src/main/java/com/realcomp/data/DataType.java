package com.realcomp.data;

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
    LIST("list"),
    NULL("null");

    private String description;

    private DataType(String description){
        this.description = description;
    }

    public String getDescription(){
        return description;
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
