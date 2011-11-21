package com.realcomp.data.record;

import java.util.ArrayList;
import java.util.List;

/**
 * The key for a value in a Record.
 * A record key can also reference an item in a List. 
 * e.g., "owner[1]" would reference 2nd item in the list stored at key 'owner'.
 * 
 * @author krenfro
 */
public class RecordKey {

    private static final String KEY_DELIMITER_REGEX = "\\.";
    private static final String KEY_DELIMITER = ".";
    
    private String key;
    private int index;

    public RecordKey(String key) {

        index = -1;
        int start = key.indexOf("[");
        if (start > 0) {
            int stop = key.indexOf("]", start);
            if (stop > start) {
                index = Integer.parseInt(key.substring(start + 1, stop));
                this.key = key.substring(0, start);
            }
            else {
                this.key = key;
            }
        }
        else {
            this.key = key;
        }
    }
    
    /**
     * Parses period delimited composite key for each individual RecordKey component.
     * For example, the key "property.owner[1].name" would return
     * a list of 3 RecordKeys. 
     * 
     * @param compositeKey not null
     * @return 
     */
    public static List<RecordKey> parse(String compositeKey) {
        if (compositeKey == null)
            throw new IllegalArgumentException("compositeKey is null");
        
        List<RecordKey> list = new ArrayList<RecordKey>();
        if (!compositeKey.isEmpty()){
            for (String key : compositeKey.split(KEY_DELIMITER_REGEX))
                list.add(new RecordKey(key));
        }
        return list;
    }

    public boolean isIndexed() {
        return index >= 0;
    }

    public String getKey() {
        return key;
    }
    
    public static String toKey(List<RecordKey> keys){
        
        StringBuilder result = new StringBuilder();
        for (int x = 0; x < keys.size(); x++){
            if (x != 0){
                result.append(KEY_DELIMITER);
            }
            result.append(keys.get(x));
        }
        return result.toString();
    }
    
    public int getIndex(){
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setKey(String key) {
        this.key = key;
    }

    
    @Override
    public String toString(){
        return index >= 0 ? String.format("%s[%s]", new Object[]{key, index}) : key;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final RecordKey other = (RecordKey) obj;
        if ((this.key == null) ? (other.key != null) : !this.key.equals(other.key))
            return false;
        if (this.index != other.index)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (this.key != null ? this.key.hashCode() : 0);
        hash = 37 * hash + this.index;
        return hash;
    }
}
