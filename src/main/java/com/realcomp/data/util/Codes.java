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
    private static final Logger logger = Logger.getLogger(Codes.class.getName());
    protected Properties codes;
    protected String description;
    protected Level logLevel = Level.INFO;

    public Codes(){
        codes = new Properties();
    }

    public Codes(Properties properties){
        codes = new Properties();
        this.codes.putAll(properties);
    }

    public Codes(Codes copy){
        this.codes = new Properties();
        this.codes.putAll(copy.codes);
        this.description = copy.description;
        this.logLevel = copy.logLevel;
    }

    public Codes(InputStream in) throws IOException{
        codes = new Properties();
        codes.load(in);
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public Level getLogLevel(){
        return logLevel;
    }

    /**
     * By default, code translations misses are logged at Level.INFO
     * JDK logging can be figured to override this level for all Codes instances, but it may be
     * the case that you desire a different logging level for an individual Codes instance.
     *
     * @param logLevel The level at which to log code translations misses.
     */
    public void setLogLevel(Level logLevel){
        if (logLevel == null){
            throw new IllegalArgumentException("logLevel is null");
        }
        this.logLevel = logLevel;
    }


    public String setTranslation(String code, String translation){
        return (String) codes.setProperty(code, translation);
    }

    public String translate(String code){
        String translation = null;
        if (code != null){
            translation = codes.getProperty(code);
            if (translation == null && !code.isEmpty()){
                logger.log(logLevel, "Missing translation for [{0}] in [{1}]", new Object[]{code, description});
                translation = code;
            }
        }
        return translation;
    }

    public String translate(String code, String defaultValue){
        return codes.getProperty(code, defaultValue);
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
