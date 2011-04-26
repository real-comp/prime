package com.realcomp.data.record;

import com.realcomp.data.BooleanField;
import com.realcomp.data.DoubleField;
import com.realcomp.data.Field;
import com.realcomp.data.FloatField;
import com.realcomp.data.IntegerField;
import com.realcomp.data.LongField;
import com.realcomp.data.MapField;
import com.realcomp.data.StringField;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * A collection of zero or more Fields.
 * <p>
 * Simply a MapField with support for the setting of 'key' field names.
 * A record may have more than one 'key' field.  These fields
 * are used to construct a record 'key' that may be useful.
 * By default, the 'key' is the value of the first field in the map.
 * Typically, a field is marked with a key validator in the schema.
 * </p>
 * <p>
 * There are a series of helpful <i>put</i> methods that hide the *Field objects.
 * (e.g., put(String key, String value))
 *
 * </p>
 *
 * @author krenfro
 */
public class Record extends MapField implements Serializable{

    private static final long serialVersionUID = 1L;
    protected List<String> keys;

    public Record(){
    }

    /**
     *
     * @param keys the names of the fields that constitute the <i>key</i> for this Record.
     */
    public Record(Collection<String> keys){
        if (keys != null){
            this.keys = new ArrayList<String>();
            this.keys.addAll(keys);
        }
    }

    /**
     *
     * @return the value of the <i>key</i> fields for this Record, or null if unknown.
     */
    public List<String> getKey(){

        List<String> key = null;
        if (keys != null && !wrapped.isEmpty()){
            key = new ArrayList<String>();
            for (String fieldname: keys)
                key.add(wrapped.get(fieldname).getValue().toString());
        }

        return key;
    }


    /**
     * Convenience method to for very common operation of adding a String
     * value to a Record.
     * @param key
     * @param value
     * @return previous value.
     */
    public String put(String key, String value){
        StringField existing = (StringField) super.put(key, new StringField(value));
        return existing == null ? null : existing.getValue();
    }

    /**
     * Convenience method to for very common operation of adding an Integer
     * value to a Record.
     * @param key
     * @param value
     * @return previous value
     */
    public Integer put(String key, Integer value){
        IntegerField existing = (IntegerField) super.put(key, new IntegerField(value));
        return existing == null ? null : existing.getValue();
    }

    /**
     * Convenience method to for very common operation of adding an Float
     * value to a Record.
     * @param key
     * @param value
     * @return previous value
     */
    public Float put(String key, Float value){
        FloatField existing = (FloatField) super.put(key, new FloatField(value));
        return existing == null ? null : existing.getValue();
    }

    /**
     * Convenience method to for very common operation of adding an Long
     * value to a Record.
     * @param key
     * @param value
     * @return previous value
     */
    public Long put(String key, Long value){
        LongField existing = (LongField) super.put(key, new LongField(value));
        return existing == null ? null : existing.getValue();
    }

    /**
     * Convenience method to for very common operation of adding an Double
     * value to a Record.
     * @param key
     * @param value
     * @return previous value
     */
    public Double put(String key, Double value){
        DoubleField existing = (DoubleField) super.put(key, new DoubleField(value));
        return existing == null ? null : existing.getValue();
    }

    /**
     * Convenience method to for very common operation of adding an Boolean
     * value to a Record.
     * @param key
     * @param value
     * @return previous value
     */
    public Boolean put(String key, Boolean value){
        BooleanField existing = (BooleanField) super.put(key, new BooleanField(value));
        return existing == null ? null : existing.getValue();
    }


    
    /**
     * @return the key fields of this Record delimited by a colon, or
     * the value of the first two fields if no key fields defined.
     */
    @Override
    public String toString(){
        List<String> key = getKey();
        StringBuilder s = new StringBuilder();
        boolean needDelimiter = false;

        if (key == null){
            //no key defined, use value of first 2 fields
            key = new ArrayList<String>();
            for (Field f: wrapped.values()){
                if (key.size() >= 2)
                    break;

                key.add(f.getValue().toString());
            }
        }
        
        for (String k: key){
            if (needDelimiter)
                s.append(":");
            s.append(k);
        }
        
        return s.toString();
    }
}
