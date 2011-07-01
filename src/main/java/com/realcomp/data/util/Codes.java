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
    protected Level logLevel = Level.FINE;
    
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
    }
    
    public Codes(InputStream in) throws IOException{
        codes = new Properties();
        codes.load(in);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Level getLogLevel() {
        return logLevel;
    }

    /**
     * By default, code translations misses are logged at Level.FINE 
     * JDK logging can be figured to override this level for all Codes instances, but it may be 
     * he case that you desire a different logging level for an individual Codes instance.
     * 
     * @param logLevel The level at which to log code translations misses.
     */
    public void setLogLevel(Level logLevel) {
        if (logLevel == null)
            throw new IllegalArgumentException("logLevel is null");
        this.logLevel = logLevel;
    }

    public Properties getCodes() {
        return codes;
    }

    public void setCodes(Properties codes) {
        if (codes == null)
            throw new IllegalArgumentException("codes is null");
        this.codes.clear();
        this.codes.putAll(codes);
    }
    
    public String setTranslation(String code, String translation){
        return (String) codes.setProperty(code, translation);
    }
    
    public String translate(String code){
        String translation = codes.getProperty(code);
        if (translation == null && (code != null && !code.isEmpty()))
            logger.log(logLevel, 
                       "Missing transation for [{0}] in [{1}]", 
                       new Object[]{code, description});
        return translation;
    }
    
    public String translate(String code, String defaultValue){
        return codes.getProperty(code, defaultValue);
    }

    /**
     * 
     * @param codes list of codes to be translated, delimited by <i>delimiter</i>. may be null
     * @param delimiter regex
     * @return All successfully translated codes. never null.
     */
    public List<String> translateList(String codes, String delimiter){
        List<String> retVal = new ArrayList<String>();
        if (codes != null){
            for (String code: codes.split(delimiter)){
                String trans = translate(code);
                if (trans != null)
                    retVal.add(trans);
            }
        }
        return retVal;
    }
    
    /**
     * 
     * @param codes list of codes to be translated, delimited by <i>delimiter</i>. may be null
     * @param delimiter regex
     * @param defaultValue
     * @return
     */
    public List<String> translateList(String codes, String delimiter, String defaultValue){
        List<String> retVal = new ArrayList<String>();
        if (codes != null){
            for (String code: codes.split(delimiter))
                retVal.add(translate(code, defaultValue));
        }
        return retVal;
    }

    @Override
    public String toString() {
        return "Codes{" + "description=" + description + '}';
    }
    

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Codes other = (Codes) obj;
        if (this.codes != other.codes && (this.codes == null || !this.codes.equals(other.codes)))
            return false;
        if ((this.description == null) ? (other.description != null)
                : !this.description.equals(other.description))
            return false;
        if (this.logLevel != other.logLevel && (this.logLevel == null || !this.logLevel.equals(other.logLevel)))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + (this.codes != null ? this.codes.hashCode() : 0);
        hash = 83 * hash + (this.description != null ? this.description.hashCode() : 0);
        hash = 83 * hash + (this.logLevel != null ? this.logLevel.hashCode() : 0);
        return hash;
    }
}
