package com.realcomp.data.schema.xml;

import com.thoughtworks.xstream.converters.SingleValueConverter;
import java.util.regex.Pattern;

/**
 *
 * @author krenfro
 */
public class RegexPatternConverter implements SingleValueConverter{

    @Override
    public String toString(Object obj) {
        return ((Pattern) obj).toString();
    }

    @Override
    public Object fromString(String str) {
        return Pattern.compile(str);
    }

    @Override
    public boolean canConvert(Class type) {        
        return Pattern.class.isAssignableFrom(type);
    }
    
}
