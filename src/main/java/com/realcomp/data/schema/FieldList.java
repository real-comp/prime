package com.realcomp.data.schema;

import com.realcomp.data.record.Record;
import com.realcomp.data.schema.xml.FieldListConverter;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import java.util.*;
import java.util.regex.Pattern;

/**
 * A List of Fields
 * @author krenfro
 */
@XStreamConverter(FieldListConverter.class)
public class FieldList implements List<Field> {
    
    public static final String KEY_DELIMITER = "|";
    public static final Pattern DEFAULT_CLASSIFIER = Pattern.compile(".*");
    
    private List<Field> fields;
    private Pattern classifier = DEFAULT_CLASSIFIER;
    private String name;
    
    /* Several caches of information that are cleared when any change occurs to the List of Fields */
    private transient List<String> names;
    private transient List<Field> keys;
    private transient List<Field> foreignKeys;
    
    public FieldList(){
        fields = new ArrayList<Field>();
        classifier = DEFAULT_CLASSIFIER;
    }
    
    public FieldList(Pattern classifier){
        this();
        if (classifier == null)
            throw new IllegalArgumentException("classifier is null");
        this.classifier = Pattern.compile(classifier.toString());
    }
    
    public FieldList(FieldList copy){
        this();
        if (copy == null)
            throw new IllegalArgumentException("copy is ");
        for (Field field: copy)
            fields.add(new Field(field));
        resetCachedValues();
        this.classifier = Pattern.compile(copy.classifier.toString());
    }
    
    public FieldList(List<Field> copy){
        this();        
        for (Field field: copy)
            fields.add(new Field(field));
        resetCachedValues();
    }

    private void resetCachedValues(){
        names = null;
        keys = null;
        foreignKeys = null;
    }
    
    public boolean isDefaultClassifier(){
        return classifier.equals(DEFAULT_CLASSIFIER);
    }

    public Pattern getClassifier() {
        return classifier;
    }

    public void setClassifier(Pattern classifier) {
        if (classifier == null)
            throw new IllegalArgumentException("classifier is null");
        this.classifier = classifier;
    }
    
    /**
     * @param data not null
     * @return true if the classifier matches the specified data; else false
     */
    public boolean supports(String data){        
        return classifier.matcher(data).matches();
    }
    
    /**
     * 
     * @param record not null
     * @return true if this FieldList has a Field defined for each entry in the Record; else false.
     */
    public boolean supports(Record record){
        
        if (names == null)
            names = getFieldNames();
        
        return names.containsAll(record.keySet());
    }
    
    public Field get(String name){
        for (Field f: this)
            if (f.getName().equals(name))
                return f;
        
        return null;
    }
    
    private List<String> getFieldNames(){
        List<String> retVal = new ArrayList<String>();
        for (Field f: this){
            retVal.add(f.getName());
        }
        return retVal;
    }
    
    /**
     * 
     * @return all 'key' SchemaFields, in the order defined by this FileSchema
     */
    public List<Field> getKeys(){
        
        if (keys == null){
            keys = new ArrayList<Field>();
            for (Field f: this){
                if (f.isKey())
                    keys.add(f);
            }
            Collections.sort(keys, new FieldKeyComparator());
        }
        
        return keys;
    }
    
    /**
     * 
     * @return all 'foreign key' Fields, in the order defined by this FileSchema
     */
    public List<Field> getForeignKeys(){
        
        if (foreignKeys == null){
            foreignKeys = new ArrayList<Field>();
            for (Field f: this){
                if (f.isForeignKey())
                    foreignKeys.add(f);
            }
            Collections.sort(foreignKeys, new FieldKeyComparator());
        }
        
        return foreignKeys;
    }
    
    
    /**
     * List of Strings pulled from Fields in a Record that are marked as 'Keys'.
     * 
     * @param record not null
     * @return list of Key fieldLists from the specified record. 
     */
    public List<Object> resolveKeys(Record record){
        List<Object> result = new ArrayList<Object>();        
        for (Field f: getKeys()){                       
            result.add(record.get(f.getName()));
        }        
        return result;
    }

    @Override
    public boolean add(Field e) {
        resetCachedValues();
        return fields.add(e);
    }

    @Override
    public void add(int index, Field element) {
        resetCachedValues();
        fields.add(index, element);
    }

    @Override
    public boolean addAll(Collection<? extends Field> c) {
        resetCachedValues();
        return fields.addAll(c);
    }

    @Override
    public boolean addAll(int index,
                          Collection<? extends Field> c) {
        resetCachedValues();
        return fields.addAll(index, c);
    }

    @Override
    public void clear() {
        resetCachedValues();
        fields.clear();
    }

    @Override
    public Field remove(int index) {
        resetCachedValues();
        return fields.remove(index);
    }

    @Override
    public boolean remove(Object o) {
        resetCachedValues();
        return fields.remove(o);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        resetCachedValues();
        return fields.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        resetCachedValues();
        return fields.retainAll(c);
    }

    @Override
    public Field set(int index, Field element) {
        resetCachedValues();
        return fields.set(index, element);
    }
    
    
    /**
     * <p>
     * A smart toString() for a Record that uses the key fields to output a 
     * shorter String representation of a Record.  Each key field is delimited by a pipe '|', 
     * in the order defined by this FieldList.</p>
     * 
     * <p>
     * If no keys are defined in the schema, then all the fields in the record are appended and delimited
     * with a pipe character.
     * </p>
     * 
     * </p>
     * No check is performed to make sure the Record is supported by this FieldList.
     * </p>
     * 
     * 
     * @param record
     * @return Pipe delimited String of the Record's keys
     */
    public String toString(Record record){
        
        StringBuilder s = new StringBuilder();
        List<Object> keyValues = resolveKeys(record);
        boolean needDelimiter = false;
        if (keyValues.isEmpty()){
            //construct the String using all values from the Record 
            Object fieldValue;
            for (Field field: this){
                if (needDelimiter)
                    s.append("|");
                needDelimiter = true;
                fieldValue = record.get(field.getName());
                if (fieldValue != null)
                    s.append(fieldValue.toString());
            }
        }
        else{
            //construct the String using only the values of the Key fields from the Record.
            for (Object keyValue: keyValues){
                if (needDelimiter)
                    s.append(KEY_DELIMITER);
                needDelimiter = false;
                if (keyValue != null){
                    s.append(keyValue.toString());
                }
            }
        }
        
        return s.toString();
    }
    
    
    @Override
    public int size() {
        return fields.size();
    }

    @Override
    public boolean isEmpty() {
        return fields.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return fields.contains(o);
    }

    @Override
    public Iterator<Field> iterator() {
        return fields.iterator();
    }

    @Override
    public Object[] toArray() {
        return fields.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return fields.toArray(a);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return fields.containsAll(c);
    }

    @Override
    public Field get(int index) {
        return fields.get(index);
    }

    @Override
    public int indexOf(Object o) {
        return fields.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return fields.lastIndexOf(o);
    }

    @Override
    public ListIterator<Field> listIterator() {
        return fields.listIterator();
    }

    @Override
    public ListIterator<Field> listIterator(int index) {
        return fields.listIterator(index);
    }

    @Override
    public List<Field> subList(int fromIndex, int toIndex) {
        return fields.subList(fromIndex, toIndex);
    }

    /**
     * An optional name for this field list.
     */
    public String getName() {
        return name;
    }

    /**
     * An optional name for this field list.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final FieldList other = (FieldList) obj;
        if (this.fields != other.fields && (this.fields == null || !this.fields.equals(other.fields)))
            return false;        
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        
        //Pattern does not implement .equals()
        String a = this.classifier == null ? null : this.classifier.toString();
        String b = other.classifier == null ? null : other.classifier.toString();
        if (a != b && (a == null || !a.equals(b)))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + (this.fields != null ? this.fields.hashCode() : 0);
        hash = 43 * hash + (this.classifier != null ? this.classifier.toString().hashCode() : 0);
        return hash;
    }
    
}
