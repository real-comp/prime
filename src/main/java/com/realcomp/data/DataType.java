package com.realcomp.data;

import com.realcomp.data.conversion.BooleanConverter;
import com.realcomp.data.conversion.ConversionException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The constrained set of types supported in the realcomp-data data model.
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
    private String description;

    private DataType(String description) {
        this.description = description;
        booleanConverter = new BooleanConverter();
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

        switch (this) {
            case STRING:
                return coerceToString(value);
            case INTEGER:
                return coerceToInteger(value);
            case FLOAT:
                return coerceToFloat(value);
            case LONG:
                return coerceToLong(value);
            case DOUBLE:
                return coerceToDouble(value);
            case BOOLEAN:
                return coerceToBoolean(value);
            case LIST:
                return coerceToList(value);
            case MAP:
                return coerceToMap(value);
        }

        return null;
    }

    private String coerceToString(Object value) {
        return value.toString();
    }

    /**
     * Convert the value to an Integer. If the value is a String, and empty, then default 0 is returned.
     * @param value
     * @return
     * @throws ConversionException 
     */
    private Integer coerceToInteger(Object value) throws ConversionException {

        try {
            switch (DataType.getDataType(value)) {
                case STRING:
                    return ((Double) Double.parseDouble(value.toString().isEmpty() ? "0" : value.toString())).intValue();
                case INTEGER:
                    return (Integer) value;
                case FLOAT:
                    return ((Float) value).intValue();
                case LONG:
                    return ((Long) value).intValue();
                case DOUBLE:
                    return ((Double) value).intValue();
                case BOOLEAN:
                    return ((Boolean) value) ? 1 : 0;
                case LIST:
                    if (((List) value).size() == 1)
                        return coerceToInteger(((List) value).get(0));
            }
        } catch (NumberFormatException nfe) {
            throw new ConversionException(
                    String.format("Unable to coerce [%s] of type [%s] to type [%s]",
                    value, DataType.getDataType(value), INTEGER));
        }

        throw new ConversionException(
                String.format("Unable to coerce [%s] of type [%s] to type [%s]",
                value, DataType.getDataType(value), INTEGER));

    }

    /**
     * Convert the value to a Long. If value is a String, and empty, then the default 0 is returned.
     * @param value
     * @return
     * @throws ConversionException 
     */
    private Long coerceToLong(Object value) throws ConversionException {

        try {
            switch (DataType.getDataType(value)) {
                case STRING:
                    return Long.parseLong(value.toString().isEmpty() ? "0" : value.toString());
                case INTEGER:
                    return ((Integer) value).longValue();
                case FLOAT:
                    return ((Float) value).longValue();
                case LONG:
                    return (Long) value;
                case DOUBLE:
                    return ((Double) value).longValue();
                case BOOLEAN:
                    return ((Boolean) value) ? 1l : 0l;
                case LIST:
                    if (((List) value).size() == 1)
                        return coerceToLong(((List) value).get(0));
            }
        } catch (NumberFormatException nfe) {
            throw new ConversionException(
                    String.format("Unable to coerce [%s] of type [%s] to type [%s]",
                    value, DataType.getDataType(value), LONG));
        }

        throw new ConversionException(
                String.format("Unable to coerce [%s] of type [%s] to type [%s]",
                value, DataType.getDataType(value), LONG));

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
     * returned.
     * 
     * @param value
     * @return
     * @throws ConversionException 
     */
    private Float coerceToFloat(Object value) throws ConversionException {

        try {
            switch (DataType.getDataType(value)) {
                case STRING:
                    return Float.parseFloat(value.toString().isEmpty() ? "0" : value.toString());
                case INTEGER:
                    return ((Integer) value).floatValue();
                case FLOAT:
                    return (Float) value;
                case LONG:
                    return ((Long) value).floatValue();
                case DOUBLE:
                    return ((Double) value).floatValue();
                case BOOLEAN:
                    return ((Boolean) value) ? 1f : 0f;
                case LIST:
                    if (((List) value).size() == 1)
                        return coerceToFloat(((List) value).get(0));
            }
        } catch (NumberFormatException nfe) {
            throw new ConversionException(
                    String.format("Unable to coerce [%s] of type [%s] to type [%s]",
                    value, DataType.getDataType(value), FLOAT));
        }

        throw new ConversionException(
                String.format("Unable to coerce [%s] of type [%s] to type [%s]",
                value, DataType.getDataType(value), FLOAT));
    }

    /**
     * Convert the value to a Double.  If the value is a String, and empty, then the default 0 is
     * returned.
     * 
     * @param value
     * @return
     * @throws ConversionException 
     */
    private Double coerceToDouble(Object value) throws ConversionException {

        try {
            switch (DataType.getDataType(value)) {
                case STRING:
                    return Double.parseDouble(value.toString().isEmpty() ? "0" : value.toString());
                case INTEGER:
                    return ((Integer) value).doubleValue();
                case FLOAT:
                    return ((Float) value).doubleValue();
                case LONG:
                    return ((Long) value).doubleValue();
                case DOUBLE:
                    return (Double) value;
                case BOOLEAN:
                    return ((Boolean) value) ? 1d : 0d;
                case LIST:
                    if (((List) value).size() == 1)
                        return coerceToDouble(((List) value).get(0));
            }
        } catch (NumberFormatException nfe) {
            throw new ConversionException(
                    String.format("Unable to coerce [%s] of type [%s] to type [%s]",
                    value, DataType.getDataType(value), DOUBLE));
        }

        throw new ConversionException(
                String.format("Unable to coerce [%s] of type [%s] to type [%s]",
                value, DataType.getDataType(value), DOUBLE));
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
