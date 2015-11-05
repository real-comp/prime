package com.realcomp.data.schema;

import com.realcomp.data.record.Record;
import com.realcomp.data.schema.xml.FieldListConverter;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import java.util.*;
import java.util.regex.Pattern;

/**
 * A List of Fields
 *
 * @author krenfro
 */
@XStreamConverter(FieldListConverter.class)
public class FieldList implements List<Field>{

    public static final String KEY_DELIMITER = "|";
    public static final Pattern DEFAULT_CLASSIFIER = Pattern.compile(".*");

    private List<Field> fields;
    private Pattern classifier;
    private String name;
    private boolean defaultList = false;
    
    
    /* Several caches of information that are cleared when any change occurs to the List of Fields */
    private List<String> names;
    private List<Field> keys;
    private List<Field> foreignKeys;
    private boolean isFixedLength;
    private int recordLength;
    

    public FieldList(){
        fields = new ArrayList<>();
        names = new ArrayList<>();
        keys = new ArrayList<>();
        foreignKeys = new ArrayList<>();
    }

    public FieldList(Pattern classifier){
        this();
        Objects.requireNonNull(classifier);
        this.classifier = Pattern.compile(classifier.toString());
    }

    public FieldList(FieldList copy){
        this();
        if (copy == null){
            throw new IllegalArgumentException("copy is ");
        }
        for (Field field : copy){
            fields.add(new Field(field));
        }        
        if (copy.classifier != null){
            this.classifier = Pattern.compile(copy.classifier.toString());
        }
        this.name = copy.name;
        this.defaultList = copy.defaultList;
        
        resetCachedValues();
    }

    public FieldList(List<Field> copy){
        this();
        for (Field field : copy){
            fields.add(new Field(field));
        }
        resetCachedValues();
    }

    /**
     * @return true if the field list was identified as the default in a Schema.
     */
    public boolean isDefault(){
        return defaultList;
    }

    public void setDefault(boolean defaultList){
        this.defaultList = defaultList;
    }

    private void resetCachedValues(){
        names = calculateFieldNames();
        keys = calculateKeys();
        foreignKeys = calculateForeignKeys();
        Integer temp = calculateRecordLength();
        if (temp != null){
            isFixedLength = true;
            recordLength = temp;
        }
        else{
            isFixedLength = false;
            recordLength = 0;
        }
    }
    
    private Integer calculateRecordLength(){
        int length = 0;
        for (Field field: this){
            if (field.getLength() > 0){
                length += field.getLength();
            }
            else{
                return null;
            }
        }
        return length;
    }
            
    
    public Pattern getClassifier(){
        return classifier;
    }

    public void setClassifier(Pattern classifier){
        this.classifier = classifier;
    }

    /**
     * @param data not null
     * @return true if the classifier matches the specified data; else false
     */
    public boolean supports(String data){
        boolean supports = true;
        if (classifier != null){
            supports = classifier.matcher(data).matches();
        }
        else if (isFixedLength){
            supports = data.length() == recordLength;
        }
        return supports;
    }
    

    /**
     *
     * @param record not null
     * @return true if this FieldList has a Field defined for each entry in the Record; else false.
     */
    public boolean supports(Record record){
        return names.containsAll(record.keySet());
    }

    public Field get(String name){
        for (Field f : this){
            if (f.getName().equals(name)){
                return f;
            }
        }

        return null;
    }
    
    private List<Field> calculateKeys(){
        List<Field> result = new ArrayList<>();
        for (Field f : this){
            if (f.isKey()){
                result.add(f);
            }
        }
        Collections.sort(result, new FieldKeyComparator());
        return result;
    }
    
    private List<Field> calculateForeignKeys(){
        List<Field> result = new ArrayList<>();
        for (Field f : this){
            if (f.isForeignKey()){
                result.add(f);
            }
        }
        Collections.sort(result, new FieldKeyComparator());
        return result;
    }

    private List<String> calculateFieldNames(){
        List<String> retVal = new ArrayList<>();
        for (Field f : this){
            retVal.add(f.getName());
        }
        return retVal;
    }

    /**
     *
     * @return all 'key' SchemaFields, in the order defined by this FileSchema
     */
    public List<Field> getKeys(){
        return keys;
    }

    /**
     *
     * @return all 'foreign key' Fields, in the order defined by this FileSchema
     */
    public List<Field> getForeignKeys(){
        return foreignKeys;
    }

    /**
     * List of Strings pulled from Fields in a Record that are marked as 'Keys'.
     *
     * @param record not null
     * @return list of Key fieldLists from the specified record.
     */
    public List<Object> resolveKeys(Record record){
        List<Object> result = new ArrayList<>();
        for (Field f : getKeys()){
            result.add(record.get(f.getName()));
        }
        return result;
    }

    @Override
    public boolean add(Field field){       
        Objects.requireNonNull(field);
        boolean result = fields.add(field);
        resetCachedValues();
        return result;
    }

    @Override
    public void add(int index, Field element){        
        fields.add(index, element);
        resetCachedValues();
    }

    @Override
    public boolean addAll(Collection<? extends Field> c){
        boolean result = fields.addAll(c);
        resetCachedValues();
        return result;
    }

    @Override
    public boolean addAll(int index, Collection<? extends Field> c){        
        boolean result = fields.addAll(index, c);
        resetCachedValues();
        return result;
    }

    @Override
    public void clear(){
        fields.clear();
        resetCachedValues();        
    }

    @Override
    public Field remove(int index){
        Field result = fields.remove(index);
        resetCachedValues();
        return result;
    }

    @Override
    public boolean remove(Object o){
        boolean result = fields.remove(o);
        resetCachedValues();
        return result;
    }

    @Override
    public boolean removeAll(Collection<?> c){        
        boolean result = fields.removeAll(c);
        resetCachedValues();
        return result;
    }

    @Override
    public boolean retainAll(Collection<?> c){        
        boolean result = fields.retainAll(c);
        resetCachedValues();
        return result;
    }

    @Override
    public Field set(int index, Field element){        
        Field previous = fields.set(index, element);
        resetCachedValues();
        return previous;
    }

    /**
     * <p>
     * A smart toString() for a Record that uses the key fields to output a shorter String representation of a Record.
     * Each key field is delimited by a pipe '|', in the order defined by this FieldList.</p>
     *
     * <p>
     * If no keys are defined in the schema, then all the fields in the record are appended and delimited with a pipe
     * character.
     * </p>
     *
     * <p>
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
            for (Field field : this){
                if (needDelimiter){
                    s.append("|");
                }
                needDelimiter = true;
                fieldValue = record.get(field.getName());
                if (fieldValue != null){
                    s.append(fieldValue.toString());
                }
            }
        }
        else{
            //construct the String using only the values of the Key fields from the Record.
            for (Object keyValue : keyValues){
                if (needDelimiter){
                    s.append(KEY_DELIMITER);
                }
                needDelimiter = false;
                if (keyValue != null){
                    s.append(keyValue.toString());
                }
            }
        }

        return s.toString();
    }

    @Override
    public int size(){
        return fields.size();
    }

    @Override
    public boolean isEmpty(){
        return fields.isEmpty();
    }

    @Override
    public boolean contains(Object o){
        return fields.contains(o);
    }

    @Override
    public Iterator<Field> iterator(){
        return fields.iterator();
    }

    @Override
    public Object[] toArray(){
        return fields.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a){
        return fields.toArray(a);
    }

    @Override
    public boolean containsAll(Collection<?> c){
        return fields.containsAll(c);
    }

    @Override
    public Field get(int index){
        return fields.get(index);
    }

    @Override
    public int indexOf(Object o){
        return fields.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o){
        return fields.lastIndexOf(o);
    }

    @Override
    public ListIterator<Field> listIterator(){
        return fields.listIterator();
    }

    @Override
    public ListIterator<Field> listIterator(int index){
        return fields.listIterator(index);
    }

    @Override
    public List<Field> subList(int fromIndex, int toIndex){
        return fields.subList(fromIndex, toIndex);
    }

    /**
     * An optional name for this field list.
     * @return 
     */
    public String getName(){
        return name;
    }

    /**
     * An optional name for this field list.
     * @param name
     */
    public void setName(String name){
        this.name = name;
    }

    @Override
    public int hashCode(){
        int hash = 5;
        hash = 47 * hash + Objects.hashCode(this.fields);
        hash = 47 * hash + Objects.hashCode(this.classifier == null ? "" : this.classifier.toString());
        hash = 47 * hash + Objects.hashCode(this.name);
        hash = 47 * hash + (this.defaultList ? 1 : 0);
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
        final FieldList other = (FieldList) obj;
        if (!Objects.equals(this.fields, other.fields)){
            return false;
        }
        if (!Objects.equals(this.classifier == null ? "" : this.classifier.toString(), 
                            other.classifier == null ? "" : other.classifier.toString())){
            return false;
        }
        if (!Objects.equals(this.name, other.name)){
            return false;
        }
        if (this.defaultList != other.defaultList){
            return false;
        }
        return true;
    }
    
    
    

    
}
