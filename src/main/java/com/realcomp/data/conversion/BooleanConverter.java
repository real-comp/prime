package com.realcomp.data.conversion;

/**
 *
 * @author krenfro
 */
@com.realcomp.data.annotation.Converter("boolean")
public class BooleanConverter implements Converter{

    protected String truthy = ",TRUE,T,YES,Y,1,";
    protected String falsy = ",*,";
    protected boolean caseSensitive = false;
    

    @Override
    public String convert(String value) throws ConversionException{

        if (value == null)
            throw new IllegalArgumentException("value is null");

        Boolean result = null;
        String test = addCommas(value, caseSensitive);
        
        if (contains(truthy, test, caseSensitive ))
            result = Boolean.TRUE;
        else if(contains(falsy, test, caseSensitive ))
            result = Boolean.FALSE;
        else if (truthy.equals(",*,"))
            result = Boolean.TRUE;
        else if (falsy.equals(",*,"))
            result = Boolean.FALSE;

        if (result == null)
            throw new ConversionException("Unable to convert [" + value + "] to a boolean value");
        
        return result.toString();
    }

    private String addCommas(String value, boolean caseSensitive){

        if (caseSensitive)
            return ",".concat(value).concat(",");
        else
            return ",".concat(value.toUpperCase()).concat(",");
    }

    private String removeCommas(String value){
        return value.substring(1, value.length() - 1);
    }

    /**
     *
     * @param flags
     * @param test
     * @param caseSensitive true if comparisons should be made in a case
     *                           sensitive manner
     * @return true if flags string contains test string or both are null.
     */
    private boolean contains(String flags, String test, boolean caseSensitive){

        if (test == null && flags == null)
            return true;
        if (test == null || flags == null)
            return false;

        if (caseSensitive && flags.contains(test))
            return true;
        else if (!caseSensitive &&
                 flags.toUpperCase().contains(test.toUpperCase()))
            return true;

        return false;
    }


    public String getFalsy() {
        return removeCommas(falsy);
    }

    public void setFalsy(String falsy) {
        
        if (falsy.contains( "*" ) && falsy.length() != 1)
            throw new IllegalArgumentException(
                    "Cannot use wildcard with other values: " + falsy);

        this.falsy = addCommas(falsy, caseSensitive);
    }

    public String getTruthy() {
        return removeCommas(truthy);
    }

    public void setTruthy(String truthy) {

        if (truthy.contains( "*" ) && truthy.length() != 1)
            throw new IllegalArgumentException(
                    "Cannot use wildcard with other values: " + truthy);

        this.truthy = addCommas(truthy, caseSensitive);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final BooleanConverter other = (BooleanConverter) obj;
        if ((this.truthy == null) ? (other.truthy != null) : !this.truthy.equals(other.truthy))
            return false;
        if ((this.falsy == null) ? (other.falsy != null) : !this.falsy.equals(other.falsy))
            return false;
        if (this.caseSensitive != other.caseSensitive)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.truthy != null ? this.truthy.hashCode() : 0);
        hash = 17 * hash + (this.falsy != null ? this.falsy.hashCode() : 0);
        hash = 17 * hash + (this.caseSensitive ? 1 : 0);
        return hash;
    }
}
