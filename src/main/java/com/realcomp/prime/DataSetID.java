package com.realcomp.prime;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataSetID {

    //prefix + type + geography + source + version + name;
    protected static final Pattern ID_PATTERN = Pattern.compile("([A-Za-z0-9\\-_:]+)/([A-Za-z0-9\\-_]+)/([A-Za-z0-9\\-_]+)/([0-9]+)/([_A-Za-z0-9\\. \\-_]+)");
    protected static final Pattern PREFIXED_ID_PATTERN = Pattern.compile("(.*)/([A-Za-z0-9\\-_:]+)/([A-Za-z0-9\\-_]+)/([A-Za-z0-9\\-_]+)/([0-9]+)/([A-Za-z0-9\\. \\-_]+)");

    protected String prefix;
    protected String type;
    protected String geography;
    protected String source;
    protected String version;
    protected String name;

    public DataSetID(){
    }

    public static DataSetID parse(String path){
        Matcher matcher = ID_PATTERN.matcher(path);
        boolean prefix = false;
        if (!matcher.matches()){
            matcher = PREFIXED_ID_PATTERN.matcher(path);
            if (matcher.matches()) {
                prefix = true;
            }
            else{
                throw new IllegalArgumentException("Unable to parse [" + path + "] as a DataSetID");
            }
        }
        DataSetID id = new DataSetID();
        if (prefix){
            id.setPrefix(matcher.group(1));
            id.setType(matcher.group(2));
            id.setGeography(matcher.group(3));
            id.setSource(matcher.group(4));
            id.setVersion(matcher.group(5));
            id.setName(matcher.group(6));
        }
        else{
            id.setPrefix("");
            id.setType(matcher.group(1));
            id.setGeography(matcher.group(2));
            id.setSource(matcher.group(3));
            id.setVersion(matcher.group(4));
            id.setName(matcher.group(5));
        }

        return id;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGeography() {
        return geography;
    }

    public void setGeography(String geography) {
        this.geography = geography;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
