package com.realcomp.data;

import com.realcomp.data.conversion.BooleanConverter;
import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.conversion.RemoveLeading;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The set of types supported in the realcomp-data data model.
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
    
    private BooleanConverter booleanConverter;
    private RemoveLeading removeLeadingZeros;
    private String description;

    private DataType(String description) {
        this.description = description;
        booleanConverter = new BooleanConverter();
        removeLeadingZeros = new RemoveLeading("0");
    }

    public String getDescription() {
        return description;
    }

    /**
     * 
     * @param value
     * @return the DataType for the specified value
     * @throws IllegalArgumentException if the DataType for the value could not be determined.
     */
    public static DataType getDataType(Object value) {

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

    /**
     * 
     * @param value not null
     * @return value, converted to an instance of <i>this</i> DataType
     * @throws ConversionException 
     */
    public Object coerce(Object value) throws ConversionException {

        if (value == null)
            throw new IllegalArgumentException("value is null");

        Object result = null;
        
        switch (this) {
            case STRING:
                result = coerceToString(value);
                break;
            case INTEGER:
                result = coerceToInteger(value);
                break;
            case FLOAT:
                result = coerceToFloat(value);
                break;
            case LONG:
                result = coerceToLong(value);
                break;
            case DOUBLE:
                result = coerceToDouble(value);
                break;
            case BOOLEAN:
                result = coerceToBoolean(value);
                break;
            case LIST:
                result = coerceToList(value);
                break;
            case MAP:
                result = coerceToMap(value);
                break;
            default:
                throw new IllegalStateException("Unhandled DataType: " + this.toString());
        }

        return result;
    }

    private String coerceToString(Object value) {
        return value.toString();
    }

    /**
     * Convert the value to an Integer. If the value is a String, and empty, then default 0 is returned.
     * Leading zeros are supported.
     * @param value
     * @return
     * @throws ConversionException 
     */
    private Integer coerceToInteger(Object value) throws ConversionException {

        Integer result = null;
        
        try {
            switch (DataType.getDataType(value)) {
                case STRING:
                    String s = (String) removeLeadingZeros.convert(value.toString());
                    result = ((Double) Double.parseDouble(s.isEmpty() ? "0" : s)).intValue();
                    break;
                case INTEGER:
                    result = (Integer) value;
                    break;
                case FLOAT:
                    result = ((Float) value).intValue();
                    break;
                case LONG:
                    result = ((Long) value).intValue();
                    break;
                case DOUBLE:
                    result = ((Double) value).intValue();
                    break;
                case BOOLEAN:
                    result = ((Boolean) value) ? 1 : 0;
                    break;
                case LIST:
                    if (((List) value).size() == 1)
                        result = coerceToInteger(((List) value).get(0));
                    break;
            }
        } 
        catch (NumberFormatException nfe) {
            throw new ConversionException(
                    String.format("Unable to coerce [%s] of type [%s] to type [%s]",
                    value, DataType.getDataType(value), INTEGER));
        }

        if (result == null)
            throw new ConversionException(
                    String.format("Unable to coerce [%s] of type [%s] to type [%s]",
                    value, DataType.getDataType(value), INTEGER));

        return result;
    }

    /**
     * Convert the value to a Long. If value is a String, and empty, then the default 0 is returned.
     * Leading zeros are supported.
     * @param value
     * @return
     * @throws ConversionException 
     */
    private Long coerceToLong(Object value) throws ConversionException {

        Long result = null;
        try {
            switch (DataType.getDataType(value)) {
                case STRING:                    
                    String s = (String) removeLeadingZeros.convert(value.toString());
                    result = ((Double) Double.parseDouble(s.isEmpty() ? "0" : s)).longValue();
                    break;
                case INTEGER:
                    result = ((Integer) value).longValue();
                    break;
                case FLOAT:
                    result = ((Float) value).longValue();
                    break;
                case LONG:
                    result = (Long) value;
                    break;
                case DOUBLE:
                    result = ((Double) value).longValue();
                    break;
                case BOOLEAN:
                    result = ((Boolean) value) ? 1l : 0l;
                    break;
                case LIST:
                    if (((List) value).size() == 1)
                        result = coerceToLong(((List) value).get(0));
                    break;
            }
        } 
        catch (NumberFormatException nfe) {
            throw new ConversionException(
                    String.format("Unable to coerce [%s] of type [%s] to type [%s]",
                    value, DataType.getDataType(value), LONG));
        }

        if (result == null){
            throw new ConversionException(
                    String.format("Unable to coerce [%s] of type [%s] to type [%s]",
                    value, DataType.getDataType(value), LONG));
        }
        
        return result;
    }

    @SuppressWarnings("unchecked")
    private List coerceToList(Object value) throws ConversionException {
        switch (DataType.getDataType(value)) {
            case LIST:
                return (List) value;
            case MAP:
                throw new ConversionException(
                        String.format("Unable to coerce [%s] of type [%s] to type [%s]",
                        value, DataType.getDataType(value), LIST));
            default:
                List list = new ArrayList();
                list.add(value);
                return list;
        }
    }

    private Map coerceToMap(Object value) throws ConversionException {

        if (DataType.getDataType(value) == MAP)
            return (Map) value;

        throw new ConversionException(
                String.format("Unable to coerce [%s] of type [%s] to type [%s]",
                value, DataType.getDataType(value), MAP));

    }

    /**
     * Convert the value to a Float.  If values is a String, and empty, then the default 0 is
     * returned. Leading zeros are supported.
     * 
     * @param value
     * @return
     * @throws ConversionException 
     */
    private Float coerceToFloat(Object value) throws ConversionException {

        Float result = null;
        try {
            switch (DataType.getDataType(value)) {
                case STRING:                    
                    String s = (String) removeLeadingZeros.convert(value.toString());
                    result = ((Float) Float.parseFloat(s.isEmpty() ? "0" : s));
                    break;
                case INTEGER:
                    result = ((Integer) value).floatValue();
                    break;
                case FLOAT:
                    result = (Float) value;
                    break;
                case LONG:
                    result = ((Long) value).floatValue();
                    break;
                case DOUBLE:
                    result = ((Double) value).floatValue();
                    break;
                case BOOLEAN:
                    result = ((Boolean) value) ? 1f : 0f;
                    break;
                case LIST:
                    if (((List) value).size() == 1)
                        result = coerceToFloat(((List) value).get(0));
                    break;
            }
        } 
        catch (NumberFormatException nfe) {
            throw new ConversionException(
                    String.format("Unable to coerce [%s] of type [%s] to type [%s]",
                    value, DataType.getDataType(value), FLOAT));
        }

        if (result == null){
            throw new ConversionException(
                    String.format("Unable to coerce [%s] of type [%s] to type [%s]",
                    value, DataType.getDataType(value), FLOAT));
        }
        
        return result;
    }

    /**
     * Convert the value to a Double.  If the value is a String, and empty, then the default 0 is
     * returned. Leading zeros are supported.
     * 
     * @param value
     * @return
     * @throws ConversionException 
     */
    private Double coerceToDouble(Object value) throws ConversionException {
        
        Double result = null;

        try {
            switch (DataType.getDataType(value)) {
                case STRING:                    
                    String s = (String) removeLeadingZeros.convert(value.toString());
                    result = Double.parseDouble(s.isEmpty() ? "0" : s);
                    break;
                case INTEGER:
                    result = ((Integer) value).doubleValue();
                    break;
                case FLOAT:
                    result = ((Float) value).doubleValue();
                    break;
                case LONG:
                    result = ((Long) value).doubleValue();
                    break;
                case DOUBLE:
                    result = (Double) value;
                    break;
                case BOOLEAN:
                    result = ((Boolean) value) ? 1d : 0d;
                    break;
                case LIST:
                    if (((List) value).size() == 1)
                        result = coerceToDouble(((List) value).get(0));
                    break;
            }
        } catch (NumberFormatException nfe) {
            throw new ConversionException(
                    String.format("Unable to coerce [%s] of type [%s] to type [%s]",
                    value, DataType.getDataType(value), DOUBLE));
        }

        if (result == null){
            throw new ConversionException(
                    String.format("Unable to coerce [%s] of type [%s] to type [%s]",
                    value, DataType.getDataType(value), DOUBLE));
        }
        return result;
    }

    /**
     * Converts the value to a Boolean.  If the value is a String, and empty, a default
     * value of Boolean.FALSE is returned.
     * 
     * @param value Must be one of the supported DataTypes
     * @return
     * @throws ConversionException 
     */
    private Boolean coerceToBoolean(Object value) throws ConversionException {

        switch (DataType.getDataType(value)) {
            case STRING:
                return (Boolean) booleanConverter.convert(
                        value.toString().isEmpty() ? "FALSE" : value.toString());
            case INTEGER:
                return (Integer) value == 1 ? Boolean.TRUE : Boolean.FALSE;
            case FLOAT:
                return ((Float) value).compareTo(1f) == 0 ? Boolean.TRUE : Boolean.FALSE;
            case LONG:
                return ((Long) value).compareTo(1l) == 0 ? Boolean.TRUE : Boolean.FALSE;
            case DOUBLE:
                return ((Double) value).compareTo(1d) == 0 ? Boolean.TRUE : Boolean.FALSE;
            case BOOLEAN:
                return (Boolean) value;
            case LIST:
                if (((List) value).size() == 1)
                    return coerceToBoolean(((List) value).get(0));
        }

        throw new ConversionException(
                String.format("Unable to coerce [%s] of type [%s] to type [%s]",
                value, DataType.getDataType(value), BOOLEAN));
    }

    
    /**
     * Parses a DataType description into a DataType.  Comparison is case-insensitive.
     * 
     * @param description The description of a DataType (e.g., "integer")
     * @return the DataType for the description, or String if description is null or empty-string
     */
    public static DataType parse(String description) {

        if (description == null)
            return DataType.STRING;
        else if (description.equalsIgnoreCase(""))
            return DataType.STRING;

        for (DataType d : values()) {
            if (d.getDescription().equalsIgnoreCase(description))
                return d;
        }

        throw new IllegalArgumentException("Unable to convert " + description + " to a DataType");
    }
}
