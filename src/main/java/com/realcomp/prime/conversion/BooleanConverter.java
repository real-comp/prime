package com.realcomp.prime.conversion;

@com.realcomp.prime.annotation.Converter("boolean")
public class BooleanConverter extends SimpleConverter{

    private String truthy = ",TRUE,T,YES,Y,1,";
    private String falsy = ",*,";
    private boolean caseSensitive = false;

    /**
     *
     * @param value
     * @return a Boolean
     * @throws ConversionException
     */
    @Override
    public Object convert(Object value) throws ConversionException{

        Boolean result = null;
        if (value != null){
            String test = addCommas(value.toString(), caseSensitive);

            if (contains(truthy, test, caseSensitive)){
                result = Boolean.TRUE;
            }
            else if (contains(falsy, test, caseSensitive)){
                result = Boolean.FALSE;
            }
            else if (truthy.equals(",*,")){
                result = Boolean.TRUE;
            }
            else if (falsy.equals(",*,")){
                result = Boolean.FALSE;
            }
            else{
                throw new ConversionException("Unable to convert [" + value + "] to a boolean value");
            }
        }

        return result;
    }

    @Override
    public BooleanConverter copyOf(){
        BooleanConverter copy = new BooleanConverter();
        copy.truthy = truthy;
        copy.falsy = falsy;
        copy.caseSensitive = caseSensitive;
        return copy;
    }

    private String addCommas(String value, boolean caseSensitive){

        assert (value != null);

        if (caseSensitive){
            return ",".concat(value).concat(",");
        }
        else{
            return ",".concat(value.toUpperCase()).concat(",");
        }
    }

    private String removeCommas(String value){
        return value.substring(1, value.length() - 1);
    }

    /**
     *
     * @param flags
     * @param test
     * @param caseSensitive true if comparisons should be made in a case sensitive manner
     * @return true if flags string contains test string
     */
    private boolean contains(String flags, String test, boolean caseSensitive){

        assert (test != null);
        assert (flags != null);

        if (caseSensitive && flags.contains(test)){
            return true;
        }
        else if (!caseSensitive
                && flags.toUpperCase().contains(test.toUpperCase())){
            return true;
        }

        return false;
    }

    /**
     * @return the comma-delimited list of flags that will evaluate to FALSE. (default = wildcard "*")
     */
    public String getFalsy(){
        return removeCommas(falsy);
    }

    /**
     * @param falsy comma-delimited list of flags that will resolve to FALSE (default = wildcard "*") Not null nor
     * empty-string. Use the wildcard "*" to match anything. When using wildcard character, no other flags can be
     * specified
     * @throws IllegalArgumentException if falsy is null, or the wildcard is used with other flags.
     */
    public void setFalsy(String falsy){

        if (falsy == null){
            throw new IllegalArgumentException("falsy is null");
        }
        if (falsy.contains("*") && falsy.length() != 1){
            throw new IllegalArgumentException(
                    "Cannot use wildcard with other values: " + falsy);
        }

        this.falsy = addCommas(falsy, caseSensitive);
    }

    /**
     *
     * @return comma-delimited list of flags that will resolve to TRUE (default = "TRUE,T,YES,Y,1")
     */
    public String getTruthy(){
        return removeCommas(truthy);
    }

    /**
     * @param truthy comma-delimited list of flags that will resolve to TRUE (default = "TRUE,T,YES,Y,1") Not null nor
     * empty-string. Use the wildcard "*" to match anything. When using wildcard character, no other flags can be
     * specified
     * * @throws IllegalArgumentException if truthy is null or the wildcard is used with other flags.
     */
    public void setTruthy(String truthy){

        if (truthy == null){
            throw new IllegalArgumentException("truthy is null");
        }
        if (truthy.contains("*") && truthy.length() != 1){
            throw new IllegalArgumentException(
                    "Cannot use wildcard with other values: " + truthy);
        }

        this.truthy = addCommas(truthy, caseSensitive);
    }

    /**
     *
     * @return whether comparisons to truthy and falsy flag should be made case-sensitively (default: false)
     */
    public boolean isCaseSensitive(){
        return caseSensitive;
    }

    /**
     * Set whether comparisons should be made in a case-sensitive manner.
     *
     * @param caseSensitive
     */
    public void setCaseSensitive(boolean caseSensitive){
        this.caseSensitive = caseSensitive;
    }

    @Override
    public boolean equals(Object obj){
        if (obj == null){
            return false;
        }
        if (getClass() != obj.getClass()){
            return false;
        }
        final BooleanConverter other = (BooleanConverter) obj;
        if ((this.truthy == null) ? (other.truthy != null) : !this.truthy.equals(other.truthy)){
            return false;
        }
        if ((this.falsy == null) ? (other.falsy != null) : !this.falsy.equals(other.falsy)){
            return false;
        }
        return this.caseSensitive == other.caseSensitive;
    }

    @Override
    public int hashCode(){
        int hash = 7;
        hash = 17 * hash + (this.truthy != null ? this.truthy.hashCode() : 0);
        hash = 17 * hash + (this.falsy != null ? this.falsy.hashCode() : 0);
        hash = 17 * hash + (this.caseSensitive ? 1 : 0);
        return hash;
    }
}
