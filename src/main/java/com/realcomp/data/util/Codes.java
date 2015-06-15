package com.realcomp.data.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for code translations.
 * Data loaded from properties files.
 *
 * @author krenfro
 */
public class Codes{    
    
    private static final Logger logger = Logger.getLogger(Codes.class.getName());
    
    public static final String COMMENT_PREFIX = "#";
    protected Properties codes;
    protected String description;
    protected boolean logMisses = true;
    protected boolean cacheMisses = true;
    protected Set<String> missCache;

    public Codes(){
        codes = new Properties();
        missCache = new HashSet();
    }

    public Codes(Properties properties){
        codes = new Properties();
        for (String key: properties.stringPropertyNames()){
            if (!key.startsWith(COMMENT_PREFIX)){
                codes.setProperty(key, properties.getProperty(key));
            }
        }
        this.codes.putAll(properties);
        missCache = new HashSet();
    }

    public Codes(Codes copy){
        this.codes = new Properties();
        this.codes.putAll(copy.codes);
        this.description = copy.description;
        missCache = new HashSet();
        missCache.addAll(copy.missCache);
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
        missCache = new HashSet();
    }

    public boolean isLogMisses(){
        return logMisses;
    }

    public void setLogMisses(boolean logMisses){
        this.logMisses = logMisses;
    }

    public boolean isCacheMisses(){
        return cacheMisses;
    }

    public void setCacheMisses(boolean cacheMisses){
        this.cacheMisses = cacheMisses;
    }
    
    

    public Properties getCodes(){
        return codes;
    }

    public void setCodes(Properties codes){
        this.codes = codes;
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
        String translation = null;
        if (code != null){
            translation = codes.getProperty(code);
            if (logMisses){
                if (cacheMisses && !missCache.contains(code)){
                    logger.log(Level.INFO, "No [{0}] translation for code [{1}]", new Object[]{description, code});
                    missCache.add(code);
                }
                else{
                    logger.log(Level.INFO, "No [{0}] translation for code [{1}]", new Object[]{description, code});
                }
            }
        }
        return translation;
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
