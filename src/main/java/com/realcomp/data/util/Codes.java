package com.realcomp.data.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for code translations.
 * Data loaded from properties files.
 *
 * @author krenfro
 */
public class Codes{    
    public static final String COMMENT_PREFIX = "#";
    protected Properties codes;
    protected String description;

    public Codes(){
        codes = new Properties();
    }

    public Codes(Properties properties){
        codes = new Properties();
        for (String key: properties.stringPropertyNames()){
            if (!key.startsWith(COMMENT_PREFIX)){
                codes.setProperty(key, properties.getProperty(key));
            }
        }
        this.codes.putAll(properties);
    }

    public Codes(Codes copy){
        this.codes = new Properties();
        this.codes.putAll(copy.codes);
        this.description = copy.description;
    }

    public Codes(InputStream in) throws IOException{
        codes = new Properties();
        Properties properties  = new Properties();
        properties.load(in);
        for (String key: properties.stringPropertyNames()){
            if (!key.startsWith(COMMENT_PREFIX)){
                codes.setProperty(key, properties.getProperty(key));
            }
        }
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }


    public String setTranslation(String code, String translation){
        return (String) codes.setProperty(code, translation);
    }

    public String translate(String code){
        return code == null ? null : codes.getProperty(code);
    }

    public String translate(String code, String defaultValue){
        String translation = translate(code);
        return translation == null ? defaultValue : translation;
    }

    /**
     *
     * @param codes     list of codes to be translated, delimited by <i>delimiter</i>. may be null
     * @param delimiter regex
     * @return All successfully translated codes. never null.
     */
    public List<String> translateList(String codes, String delimiter){
        List<String> retVal = new ArrayList<>();
        if (codes != null){
            for (String code : codes.split(delimiter)){
                String trans = translate(code);
                if (trans != null){
                    retVal.add(trans);
                }
            }
        }
        return retVal;
    }

    /**
     *
     * @param codes        list of codes to be translated, delimited by <i>delimiter</i>. may be null
     * @param delimiter    regex
     * @param defaultValue
     * @return
     */
    public List<String> translateList(String codes, String delimiter, String defaultValue){
        List<String> retVal = new ArrayList<>();
        if (codes != null){
            for (String code : codes.split(delimiter)){
                retVal.add(translate(code, defaultValue));
            }
        }
        return retVal;
    }

    @Override
    public String toString(){
        return "Codes{" + "description=" + description + '}';
    }
}
