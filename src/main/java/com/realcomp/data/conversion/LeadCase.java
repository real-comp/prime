package com.realcomp.data.conversion;

@com.realcomp.data.annotation.Converter("leadCase")
public class LeadCase extends StringConverter{

    @Override
    public Object convert(Object value) throws ConversionException{
        return value == null ? null : new String(convert(value.toString().toCharArray()));
    }

    /** Recieves a character array and converts the first letter in each word to
     * upper case. All other charaters are
     * converted to lower case. White space is ignored and not trimmed in any way.
     *
     * @param input character array to lead-cap
     * @return the input character array with the first letter in each word
     *         capitalized and all other characters converted
     *         to lower case
     */
    public char[] convert(char[] input){
        boolean startOfToken = true;
        char[] retVal = new char[0];

        if (input.length > 0){
            retVal = new char[input.length];

            for (int x = 0; x < retVal.length; x++){
                retVal[x] = input[x];
                if (retVal[x] == ' '){
                    startOfToken = true;
                }
                else if (retVal[x] != ' ' && startOfToken){
                    retVal[x] = Character.toUpperCase(retVal[x]);
                    //uppercase this char
                    startOfToken = false;
                }
                else{
                    retVal[x] = Character.toLowerCase(retVal[x]);
                }
            }
        }

        return retVal;
    }

    @Override
    public LeadCase copyOf(){
        return new LeadCase();
    }

    @Override
    public boolean equals(Object other){
        return (other instanceof LeadCase);
    }

    @Override
    public int hashCode(){
        int hash = 7;
        return hash;
    }
}
