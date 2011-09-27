package com.realcomp.data.record;

import java.util.ArrayList;
import java.util.List;

/**
 * A record key that references an item in a List. 
 * e.g., "property.owner[1].name" would reference name field of the the 2nd item in the owner list.
 * 
 * 
 * @author krenfro
 */
public class IndexedRecordKey {

    private static final String KEY_DELIMITER_REGEX = "\\.";
    
    private String key;
    private int index;

    public IndexedRecordKey(String key) {

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

    public boolean isIndexed() {
        return index >= 0;
    }

    public String getKey() {
        return key;
    }
    
    public int getIndex(){
        return index;
    }

    static List<IndexedRecordKey> parse(String compositeKey) {
        List<IndexedRecordKey> list = new ArrayList<IndexedRecordKey>();
        for (String key : compositeKey.split(KEY_DELIMITER_REGEX))
            list.add(new IndexedRecordKey(key));
        return list;
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
        final IndexedRecordKey other = (IndexedRecordKey) obj;
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
