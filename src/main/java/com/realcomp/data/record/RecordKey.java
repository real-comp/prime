package com.realcomp.data.record;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The key for a value in a Record.
 * <p>
 * <h2>Indexed Keys</h2>
 * A record key can be indexed; referencing an item in a List. e.g., "owner[1]" would reference 2nd item in the list
 * stored at key 'owner'.
 * </p>
 * <p>
 * <h2>Composite Keys</h2>
 *
 * </p>
 *
 * @author krenfro
 */
public final class RecordKey{

    protected static final String namePattern = "[A-Za-z0-9\\_ :-]+";
    protected static final String optionalIndexPattern = "(\\[[0-9]+\\])?";
    protected static final Pattern validKeyPattern =
            Pattern.compile(
            String.format("%s%s(\\.%s%s)*", namePattern, optionalIndexPattern, namePattern, optionalIndexPattern));
    protected static final Pattern parsingPattern =
            Pattern.compile("([A-Za-z0-9\\_ :-]+)(?:\\[)?([0-9]+)?(?:\\])?[\\.]?");
    private RecordKey parent;
    private String name;
    private Integer index;

    /**
     * @param key not null. may be indexed or composite.
     */
    public RecordKey(String key){

        if (key == null){
            throw new RecordKeyException("key is null");
        }
        if (key.isEmpty()){
            throw new RecordKeyException("key is empty");
        }

        if (!validKeyPattern.matcher(key).matches()){
            throw new RecordKeyException("invalid RecordKey [" + key + "]");
        }

        Matcher m = parsingPattern.matcher(key);
        RecordKey prev = null;
        while (m.find()){
            prev = new RecordKey(m.group(1), m.group(2), prev);
        }

        if (prev == null){
            name = key;
        }
        else{
            name = prev.name;
            index = prev.index;
            parent = prev.parent;
        }
    }

    public RecordKey(RecordKey copy){
        this.name = copy.name;
        this.index = copy.index;
        this.parent = copy.parent;
    }

    private RecordKey(String name, String index){
        assert (name != null);
        assert (!name.isEmpty());
        this.name = name;
        this.index = index == null ? null : Integer.parseInt(index);
        parent = null;
        if (this.index != null && this.index < 0){
            throw new IllegalArgumentException("RecordKey [" + name + "] index [" + index + "] < 0");
        }
    }

    private RecordKey(String name, String index, RecordKey parent){
        this(name, index);
        this.parent = parent;
    }

    /**
     * Builds the sequence that keys need to be resolved from the root Map. The root key will be at the top of the
     * stack, and <i>key</i> will be at the bottom.
     *
     * @param key not null
     * @return
     */
    public Stack<RecordKey> buildKeySequence(){

        Stack<RecordKey> sequence = new Stack<>();
        RecordKey current = this;
        sequence.push(current);
        while (current.hasParent()){
            current = current.getParent();
            sequence.push(current);
        }
        assert (!sequence.isEmpty());
        return sequence;
    }

    /**
     * An indexed RecordKey represents an index into a list of values in a Record. (e.g., "property.improvement[1]")
     *
     * @return true if this RecordKey has an index; else false.
     */
    public boolean isIndexed(){
        return index != null;
    }

    /**
     *
     * @param name
     */
    public void setName(String name){
        if (name == null){
            throw new IllegalArgumentException("name is null");
        }
        if (name.isEmpty()){
            throw new IllegalArgumentException("name is empty");
        }
        if (name.contains(".")){
            throw new IllegalArgumentException(
                    "name must not contain a '.' This character is reserved for composite keys.");
        }
        this.name = name;
    }

    /**
     * @return the key name, without optional index and not composite.
     */
    public String getName(){
        return name;
    }

    /**
     *
     * @return true if this RecordKey has a parent; else false.
     */
    public boolean hasParent(){
        return parent != null;
    }

    /**
     *
     * @return This RecordKey's parent key; or null if there is no parent.
     */
    public RecordKey getParent(){
        return parent;
    }

    /**
     *
     * @param parent the parent for this key. May be null.
     */
    public void setParent(RecordKey parent){
        this.parent = parent;
    }

    /**
     *
     * @return the index for the key; or null if not indexed.
     */
    public Integer getIndex(){
        return index;
    }

    /**
     *
     * @param index set the index for this key. may be null.
     */
    public void setIndex(Integer index){
        this.index = index;
    }

    @Override
    public String toString(){
        if (hasParent()){
            StringBuilder s = new StringBuilder();
            s.append(index == null ? name : String.format("%s[%s]", new Object[]{name, index}));
            s.insert(0, ".");
            s.insert(0, parent.toString()); //will recurse up the stack.
            return s.toString();
        }
        else{
            return index == null ? name : String.format("%s[%s]", new Object[]{name, index});
        }
    }

    @Override
    public boolean equals(Object obj){
        if (obj == null){
            return false;
        }
        if (getClass() != obj.getClass()){
            return false;
        }
        final RecordKey other = (RecordKey) obj;
        if (this.parent != other.parent && (this.parent == null || !this.parent.equals(other.parent))){
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)){
            return false;
        }
        if (this.index != other.index && (this.index == null || !this.index.equals(other.index))){
            return false;
        }
        return true;
    }

    @Override
    public int hashCode(){
        int hash = 7;
        hash = 37 * hash + (this.parent != null ? this.parent.hashCode() : 0);
        hash = 37 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 37 * hash + (this.index != null ? this.index.hashCode() : 0);
        return hash;
    }
}
