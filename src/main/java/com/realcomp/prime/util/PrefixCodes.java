package com.realcomp.prime.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Utility class for code translations based on a code prefix.
 * Data loaded from properties files.
 */
public class PrefixCodes extends Codes{

    private static final Logger logger = Logger.getLogger(PrefixCodes.class.getName());

    private int maxLength;

    public PrefixCodes(){
        super();
    }

    public PrefixCodes(Properties properties){
        super(properties);
        maxLength = getMaxKeyLength(properties);        
    }
    
    private int getMaxKeyLength(Properties properties){
        int max = Integer.MIN_VALUE;
        for (String key: properties.stringPropertyNames()){
            max = Math.max(key.length(), max);
        }
        return max;
    }

    public PrefixCodes(PrefixCodes copy){
        this.codes = new Properties();
        this.codes.putAll(copy.codes);
        this.description = copy.description;
        this.maxLength = copy.maxLength;
    }

    public PrefixCodes(InputStream in) throws IOException{
        super(in);
        maxLength = getMaxKeyLength(codes);
    }

    @Override
    public String translate(String code){        
        String translation = super.translate(code);        
        if (translation == null && code != null){
            if (code.length() > maxLength){
                code = code.substring(0, maxLength);
                translation = super.translate(code); 
            }
            while (translation == null && !code.isEmpty()){
                code = code.substring(0, code.length() - 1);
                translation = super.translate(code);
            }
        }
        
        return translation;
    }

    @Override
    public String translate(String code, String defaultValue){
        String translation = translate(code);        
        return translation == null ? defaultValue: translation;
    }


    @Override
    public String toString(){
        return "PrefixCodes{" + "description=" + description + '}';
    }
}
