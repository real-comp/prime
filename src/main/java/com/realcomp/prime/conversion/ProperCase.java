package com.realcomp.prime.conversion;

import java.util.HashMap;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;


@com.realcomp.prime.annotation.Converter("properCase")
public class ProperCase extends LeadCase{

    private HashMap<String, String> specialWords = null;

    public ProperCase(){
        specialWords = new HashMap<>(100);

        ResourceBundle properties = PropertyResourceBundle.getBundle(this.getClass().getName());

        java.util.Enumeration<String> e = properties.getKeys();
        String key;
        String value;
        while (e.hasMoreElements()){
            key = e.nextElement();
            if (key != null){
                value = properties.getString(key);
                if (value != null){
                    specialWords.put(key.trim(), value.trim());
                }
            }
        }
    }


    @Override
    public Object convert(Object value) throws ConversionException{

        String leadCase = (String) super.convert(value);
        return leadCase == null ? null : toProperCase(leadCase);
    }


    /** Recieves a String and first converts it to lead-capitalization, then
     * performs a series of custom capitalizations.
     * <BR>
     * Any letter after an apostrophe is capitalized (ex. O'Brian ). <BR>
     * Any letter after a dash is capitalized (ex. Smith-Barney ). <BR>
     * Any occurance of "Mcc" is changed to "McC" <BR>
     * Every word in the input string is checked against the
     * explicitCapitalization list and if a match is found
     * the word is capitalized as specified.
     * <BR><BR>
     * NOTE: The output from this method may NOT be the same length as the
     * input. The reason for this is that the user
     * of this object can specify custom capitalizations that may alter the
     * length of the input.
     * <BR><BR>
     *
     * @param input String to convert to proper case
     * @return the input String with the first letter in each word capitalized -
     *         as well as all custom capitalizations.
     */
    private String toProperCase(String input){

        String retVal = null;

        if (input != null){

            retVal = input;

            //fix 0'Brian
            int loc = retVal.indexOf("'");
            if (loc > 0 && loc + 2 <= input.length()){
                retVal = retVal.substring(0, loc) + "'"
                        + retVal.substring(loc + 1, loc + 2).toUpperCase()
                        + retVal.substring(loc + 2);
            }

            //fix Smith-Barney
            loc = retVal.indexOf("-");
            if (loc > 0 && loc + 2 <= input.length()){
                retVal = retVal.substring(0, loc) + "-"
                        + retVal.substring(loc + 1, loc + 2).toUpperCase()
                        + retVal.substring(loc + 2);
            }

            loc = retVal.indexOf("-");
            if (loc > 0 && loc + 2 <= input.length()){
                retVal = retVal.substring(0, loc) + "-"
                        + retVal.substring(loc + 1, loc + 2).toUpperCase()
                        + retVal.substring(loc + 2);
            }
            loc = retVal.indexOf("-");
            if (loc > 0 && loc + 2 <= input.length()){
                retVal = retVal.substring(0, loc) + "-"
                        + retVal.substring(loc + 1, loc + 2).toUpperCase()
                        + retVal.substring(loc + 2);
            }

            //fix McCall
            loc = retVal.indexOf("Mc");
            if (loc >= 0 && loc + 3 < input.length()){
                retVal = retVal.substring(0, loc + 2)
                        + retVal.substring(loc + 2, loc + 3).toUpperCase()
                        + retVal.substring(loc + 3);
            }

            //check for special names
            int startPos = 0;
            String temp = "";
            loc = retVal.indexOf(" ", startPos);
            if (loc >= 0){
                while (loc >= 0){
                    temp = temp.concat(getExplicitCapitalization(
                            retVal.substring(startPos, loc)));
                    temp = temp.concat(" ");
                    startPos = loc + 1;
                    loc = retVal.indexOf(" ", startPos);
                }
                if (startPos < retVal.length()){
                    temp = temp.concat(getExplicitCapitalization(
                            retVal.substring(startPos)));
                }
                retVal = temp;
            }
            else{
                retVal = getExplicitCapitalization(retVal);
            }

            if (retVal.endsWith("'S'")){
                loc = retVal.indexOf("'S");
                retVal = retVal.substring(0, loc) + "'s";
            }

            loc = retVal.indexOf("'S ");
            if (loc > 0){
                retVal = retVal.substring(0, loc) + "'s";
            }

        }

        return retVal;
    }



    /** Check the input String against all known explicit capitalizations and if
     * a match is found
     * returns the corresponding capitalization
     *
     * @param input String to check
     * @return replacement String if a match was found - else the input String.
     */
    private String getExplicitCapitalization(String input){
        String retVal = null;
        if (input != null && !input.isEmpty()){
            retVal = specialWords.get(input);
        }

        if (retVal == null){
            retVal = input;
        }

        return retVal;
    }

    @Override
    public ProperCase copyOf(){
        return new ProperCase();
    }

    @Override
    public boolean equals(Object other){
        return (other instanceof ProperCase);
    }

    @Override
    public int hashCode(){
        int hash = 7;
        return hash;
    }
}
