package com.realcomp.prime.conversion;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

/**
 *
 * @author krenfro
 */
@com.realcomp.prime.annotation.Converter("lookup")
public class Lookup extends StringConverter{

    private static final Logger logger = Logger.getLogger(Lookup.class.getName());
    
    
    private String source;
    private String delimiter = "\t";
    private Map<String,String> table;
    
    private void initializeTable() throws IOException{     
        table = new HashMap<>();
        try( BufferedReader reader = new BufferedReader(new FileReader(source))){
            String s = reader.readLine();
            while (s != null){
                String[] tokens = s.split(delimiter);
                if (tokens.length >= 2){
                    table.put(tokens[0], tokens[1]);
                }
                s = reader.readLine();
            }
        }
    }
    
    @Override
    public Object convert(Object value) throws ConversionException{
        if (table == null){
            try{
                initializeTable();
            }
            catch (IOException ex){
                throw new ConversionException(ex);
            }
        }
        
        Object result = value;
        if (value != null){
            String key = value.toString();
            String found = table.get(key);
            if (found != null){
                result = found;
            }
        }
        return result;
    }

    public String getSource(){
        return source;
    }

    public void setSource(String source){
        this.source = source;
    }

    public String getDelimiter(){
        return delimiter;
    }

    public void setDelimiter(String delimiter){
        this.delimiter = delimiter;
    }

    
    @Override
    public Lookup copyOf(){
        
        Lookup copy = new Lookup();
        copy.setSource(source);
        copy.setDelimiter(delimiter);
        return copy;
    }

    @Override
    public int hashCode(){
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.source);
        hash = 67 * hash + Objects.hashCode(this.delimiter);
        return hash;
    }

    @Override
    public boolean equals(Object obj){
        if (obj == null){
            return false;
        }
        if (getClass() != obj.getClass()){
            return false;
        }
        final Lookup other = (Lookup) obj;
        if (!Objects.equals(this.source, other.source)){
            return false;
        }
        if (!Objects.equals(this.delimiter, other.delimiter)){
            return false;
        }
        return true;
    }
    
}
