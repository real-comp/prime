package com.realcomp.data.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Utility class for code translations based on a code prefix.
 * Data loaded from properties files.
 */
public class PrefixCodes extends Codes{

    private static final Logger logger = Logger.getLogger(PrefixCodes.class.getName());

    private int prefixLength;

    public PrefixCodes(){
        super();
    }

    public PrefixCodes(Properties properties){
        Objects.requireNonNull(properties);
        codes = new Properties();
        prefixLength = getMinKeyLength(properties);        
        for (String key: properties.stringPropertyNames()){
            codes.put(getPrefix(key), properties.getProperty(key));
        }
    }
    
    private String getPrefix(String key){
        return key.length() <= prefixLength ? key : key.substring(0, prefixLength);
    }
    
    private int getMinKeyLength(Properties properties){
        int min = Integer.MAX_VALUE;
        for (String key: properties.stringPropertyNames()){
            min = Math.min(key.length(), min);
        }
        return min;
    }

    public PrefixCodes(PrefixCodes copy){
        this.codes = new Properties();
        this.codes.putAll(copy.codes);
        this.description = copy.description;
        this.logLevel = copy.logLevel;
    }

    public PrefixCodes(InputStream in) throws IOException{
        Properties temp = new Properties();
        temp.load(in);
        codes = new Properties();
        prefixLength = getMinKeyLength(temp);        
        for (String key: temp.stringPropertyNames()){
            codes.put(getPrefix(key), temp.getProperty(key));
        }
    }

    @Override
    public String translate(String code){
        String translation = null;
        if (code != null){
            String prefix = getPrefix(code);
            translation = codes.getProperty(prefix);
            if (translation == null && !code.isEmpty()){
                logger.log(
                        logLevel, 
                        "Missing translation for code prefix [{0}] in [{1}]", 
                        new Object[]{prefix, description});
                translation = code;
            }
        }
        return translation;
    }

    @Override
    public String translate(String code, String defaultValue){
        return codes.getProperty(getPrefix(code), defaultValue);
    }

    @Override
    public List<String> translateList(String codes, String delimiter){
        List<String> retVal = new ArrayList<>();
        if (codes != null){
            for (String code : codes.split(delimiter)){
                String trans = translate(getPrefix(code));
                if (trans != null){
                    retVal.add(trans);
                }
            }
        }
        return retVal;
    }


    @Override
    public List<String> translateList(String codes, String delimiter, String defaultValue){
        List<String> retVal = new ArrayList<>();
        if (codes != null){
            for (String code : codes.split(delimiter)){
                retVal.add(translate(getPrefix(code), defaultValue));
            }
        }
        return retVal;
    }

    @Override
    public String toString(){
        return "PrefixCodes{" + "description=" + description + '}';
    }
}
