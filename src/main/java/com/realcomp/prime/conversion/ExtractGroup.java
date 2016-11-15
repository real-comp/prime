package com.realcomp.prime.conversion;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extracts part of a string and returns one or more of the first tokens from a String.
 *
 * @author krenfro
 */
@com.realcomp.prime.annotation.Converter("extractGroup")
public class ExtractGroup extends StringConverter{

    protected String regex;
    protected Integer group = 1;

    protected transient Pattern pattern;

    public ExtractGroup(){
        super();
    }
    public ExtractGroup(String regex, Integer group){
        super();
        this.group = group;
        this.regex = regex;
        if (regex != null){
            pattern = Pattern.compile(regex);
        }
    }

    public ExtractGroup(String regex){
        super();
        this.regex = regex;
        if (regex != null){
            pattern = Pattern.compile(regex);
        }
    }

    @Override
    public Object convert(Object value) throws ConversionException{

        if (value == null){
            throw new IllegalArgumentException("value is null");
        }

        if (regex != null){
            Matcher matcher = pattern.matcher(value.toString());
            if (matcher.find()){
                return matcher.group(group);
            }
        }

        return null;
    }

    @Override
    public ExtractGroup copyOf(){
        return new ExtractGroup(regex, group);
    }

    public String getRegex(){
        return regex;
    }

    public void setRegex(String regex){
        this.regex = regex;
    }

    public Integer getGroup(){
        return group;
    }

    public void setGroup(Integer group){
        this.group = group;
    }

    @Override
    public int hashCode(){
        int hash = 3;
        hash = 31 * hash + Objects.hashCode(this.regex);
        hash = 31 * hash + Objects.hashCode(this.group);
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
        final ExtractGroup other = (ExtractGroup) obj;
        if (!Objects.equals(this.regex, other.regex)){
            return false;
        }
        if (!Objects.equals(this.group, other.group)){
            return false;
        }
        return true;
    }

}
