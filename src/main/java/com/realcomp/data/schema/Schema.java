package com.realcomp.data.schema;

import com.realcomp.data.MultiFieldOperation;
import com.realcomp.data.Operation;
import com.realcomp.data.record.Record;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 *
 * @author krenfro
 */
@XStreamAlias("schema")
public class Schema{

    protected static final Pattern DEFAULT_CLASSIFIER = Pattern.compile(".*");

    @XStreamAsAttribute
    private String name;

    @XStreamAsAttribute
    private String version;
    private Map<String, String> format;
    private List<Operation> beforeFirst;
    private List<Operation> before;
    private List<Operation> after;
    private List<Operation> afterLast;

    @XStreamImplicit(itemFieldName = "fields")
    private final List<FieldList> fieldLists;
    
    @XStreamOmitField
    private FieldList defaultFieldList;

    public Schema(){
        format = new HashMap<>();
        fieldLists = new ArrayList<>();
        defaultFieldList = null;
    }

    public Schema(Schema copy){
        format = new HashMap<>();
        format.putAll(copy.format);
        fieldLists = new ArrayList<>();
        if (copy.fieldLists != null){
            for (FieldList fieldList : copy.fieldLists){
                addFieldList(fieldList);
            }
        }

        this.name = copy.name;
        this.version = copy.version;
        setBeforeFirstOperations(copy.getBeforeFirstOperations());
        setBeforeOperations(copy.getBeforeOperations());
        setAfterLastOperations(copy.getAfterLastOperations());
        setAfterOperations(copy.getAfterOperations());
    }

    /**
     * Classify a record and return the FieldList that matches. If only one FieldList is defined, it is returned. If
     * multiple FieldLists support the specified Record, then the supported FieldList that is not the <i>default</i> is returned.
     * If multiple FieldLists support the specified Record, and neither are the <i>default</i> then the one defined
     * first is returned.
     *
     * @param record not null
     * @return the FieldList that should be used for the Record. never null
     * @throws SchemaException if no defined layout supports the Record
     */
    public FieldList classify(Record record) throws SchemaException{

        if (record == null){
            throw new IllegalArgumentException("record is null");
        }
        FieldList match = null;

        if (fieldLists.size() == 1){
            match = fieldLists.get(0);
        }
        else{
            match = getBestSupportingFieldList(record);
        }
        
        if (match == null){
            if (defaultFieldList != null){
                match = defaultFieldList;
            }
            else{
                throw new SchemaException("The schema [" + getName() + "] does not support the Record.");
            }
        }
        return match;        
    }
    
    /**
     * For all supported FieldLists, return the FieldList with the same number of fields as the record,
     * or the default list, or the first encountered FieldList.
     * 
     * @param record
     * @return 
     */
    private FieldList getBestSupportingFieldList(Record record){
        FieldList best = null;
        List<FieldList> supported = getSupportingFieldLists(record);
        if (!supported.isEmpty()){
            int recordSize = record.size();
            for (FieldList candidate: supported){
                if (best == null){
                    best = candidate;
                }
                else if (candidate.size() == recordSize && best.size() != recordSize){
                    best = candidate;
                }
                else if (candidate.isDefault()){
                    best = candidate;
                }
            }
        }
        return best;
    }
    
    /**
     * 
     * @param record
     * @return all FieldLists that support the specified record 
     */
    private List<FieldList> getSupportingFieldLists(Record record){
        List<FieldList> candidates = new ArrayList<>();
        for (FieldList fieldList: fieldLists){
            if (fieldList.supports(record)){
                candidates.add(fieldList);
            }
        }
        return candidates;
    }

    public FieldList getDefaultFieldList(){
        FieldList best = defaultFieldList;
        if (best == null && !fieldLists.isEmpty()){
            best = fieldLists.get(0);
        }
        return best;
    }

    /**
     *
     * @return the FieldLists supported by this Schema
     */
    public List<FieldList> getFieldLists(){
        return fieldLists;
    }

    /**
     *
     * @param fieldLists not null
     */
    public void setFieldLists(List<FieldList> fieldLists) throws SchemaException{
        if (fieldLists == null){
            throw new IllegalArgumentException("fieldLists is null");
        }

        this.fieldLists.clear();
        defaultFieldList = null;
        for (FieldList f : fieldLists){
            addFieldList(f);
        }
    }

    /**
     *
     * @param fieldList to be added
     */
    public void addFieldList(FieldList fieldList){
        Objects.requireNonNull(fieldList);
        FieldList copy = new FieldList(fieldList);
        fieldLists.add(copy);
        if (copy.isDefault()){
            if (defaultFieldList != null){
                throw new IllegalArgumentException("Only one default FieldList is supported");
            }
            defaultFieldList = copy;
        }
    }

    /**
     *
     * @param fieldList to be removed
     * @return true if removed; else false
     */
    public boolean removeFieldList(FieldList fieldList){
        if (fieldList == null){
            throw new IllegalArgumentException("fieldList is null");
        }
        
        boolean result = fieldLists.contains(fieldList);
        if (result){
            List<FieldList> copy = new ArrayList<>();
            copy.addAll(fieldLists);
            copy.remove(fieldList);
            try{
                setFieldLists(copy);
            }
            catch(SchemaException ex){
                throw new IllegalStateException(ex);
            }
        }
        return result;
    }


    /**
     *
     * @return optional name for this schema, or null
     */
    public String getName(){
        return name;
    }

    /**
     * A name for the schema.
     * Names are optional, but if you are joining records using relational schemas, a name must be provided.
     * 
     * @param name
     */
    public void setName(String name){
        this.name = name;
    }

    /**
     * @return all Operations to perform on all Fields after all Field specific operations are finished, or null if none
     *         specified.
     *
     */
    public List<Operation> getAfterOperations(){
        return after;
    }

    /**
     * Set all Operations to perform on all Fields after all Field specific operations are finished.
     *
     * @param after null will clear existing list
     */
    public void setAfterOperations(List<Operation> after){

        if (after == null){
            this.after = null;
        }
        else{
            if (this.after != null){
                this.after.clear();
            }
            for (Operation op : after){
                addAfterOperation(op);
            }
        }
    }

    /**
     * Add an Operation to the after operations group, to be run after all Field specific Operations are performed.
     *
     * @param op not null
     */
    public void addAfterOperation(Operation op){
        if (op == null){
            throw new IllegalArgumentException("op is null");
        }
        if (after == null){
            after = new ArrayList<>();
        }

        this.after.add(op);
    }

    /**
     * @return all Operations to perform after all Records have been processed, or null if none specified.
     *
     */
    public List<Operation> getAfterLastOperations(){
        return afterLast;
    }

    /**
     * Set all Operations to perform after all Records have been processed.
     *
     * @param afterLast null will clear existing list
     */
    public void setAfterLastOperations(List<Operation> afterLast){

        if (afterLast == null){
            this.afterLast = null;
        }
        else{
            if (this.afterLast != null){
                this.afterLast.clear();
            }
            for (Operation op : afterLast){
                addAfterOperation(op);
            }
        }
    }

    /**
     * Add an Operation to the afterLast operations group, to be run after all Records have been processed.
     *
     * @param op not null
     */
    public void addAfterLastOperation(Operation op){
        if (op == null){
            throw new IllegalArgumentException("op is null");
        }
        if (afterLast == null){
            afterLast = new ArrayList<>();
        }

        this.afterLast.add(op);
    }

    /**
     *
     * @return all Operations to perform on all Fields before any Field specific Operations are performed, or null if
     *         none specified.
     */
    public List<Operation> getBeforeOperations(){
        return before;
    }

    /**
     * Set all Operations to perform on all Fields before any Field specific Operations are performed.
     *
     * @param before null will clear list
     */
    public void setBeforeOperations(List<Operation> before){
        if (before == null){
            this.before = null;
        }
        else{
            if (this.before != null){
                this.before.clear();
            }
            for (Operation op : before){
                addBeforeOperation(op);
            }
        }
    }

    /**
     * Add an Operation to the before operations group, to be run before all Field specific Operations are performed.
     *
     * @param op not null, not a MultiFieldOperation
     */
    public void addBeforeOperation(Operation op){
        if (op == null){
            throw new IllegalArgumentException("op is null");
        }
        if (op instanceof MultiFieldOperation){
            throw new IllegalArgumentException(
                    "You cannot specify a MultiFieldOperation as a 'before' operation");
        }

        if (before == null){
            before = new ArrayList<>();
        }
        this.before.add(op);
    }

    /**
     *
     * @return all Operations to perform before any Records are processed, or null if none specified.
     */
    public List<Operation> getBeforeFirstOperations(){
        return beforeFirst;
    }

    /**
     * Set all Operations to perform before any Records are processed.
     *
     * @param beforeFirst null will clear list
     */
    public void setBeforeFirstOperations(List<Operation> beforeFirst){
        if (beforeFirst == null){
            this.beforeFirst = null;
        }
        else{
            if (this.beforeFirst != null){
                this.beforeFirst.clear();
            }
            for (Operation op : beforeFirst){
                addBeforeFirstOperation(op);
            }
        }
    }

    /**
     * Add an Operation to the beforeFirst operations group, to be run before any Records are processed.
     *
     * @param op not null, not a MultiFieldOperation
     */
    public void addBeforeFirstOperation(Operation op){
        if (op == null){
            throw new IllegalArgumentException("op is null");
        }
        if (op instanceof MultiFieldOperation){
            throw new IllegalArgumentException(
                    "You cannot specify a MultiFieldOperation as a 'beforeFirst' operation");
        }

        if (beforeFirst == null){
            beforeFirst = new ArrayList<>();
        }
        this.beforeFirst.add(op);
    }

    /**
     * @return optional version for this Schema, or null.
     */
    public String getVersion(){
        return version;
    }

    /**
     * @param version
     */
    public void setVersion(String version){
        this.version = version;
    }

    public Map<String, String> getFormat(){
        return format;
    }

    public void setFormat(Map<String, String> format){
        if (format == null){
            throw new IllegalArgumentException("format is null");
        }
        this.format = format;
    }

    @Override
    public String toString(){
        StringBuilder s = new StringBuilder(name == null ? "" : name);
        if (version != null && !version.isEmpty()){
            s.append(" (").append(version).append(")");
        }
        return s.toString();
    }

    public String toString(Record record){
        try{
            return classify(record).toString(record);
        }
        catch (SchemaException ex){
            return getDefaultFieldList().toString(record);
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
        final Schema other = (Schema) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)){
            return false;
        }
        if ((this.version == null) ? (other.version != null) : !this.version.equals(other.version)){
            return false;
        }
        if (this.format != other.format && (this.format == null || !this.format.equals(other.format))){
            return false;
        }
        if (this.beforeFirst != other.beforeFirst && (this.beforeFirst == null || !this.beforeFirst.equals(other.beforeFirst))){
            return false;
        }
        if (this.before != other.before && (this.before == null || !this.before.equals(other.before))){
            return false;
        }
        if (this.after != other.after && (this.after == null || !this.after.equals(other.after))){
            return false;
        }
        if (this.afterLast != other.afterLast && (this.afterLast == null || !this.afterLast.equals(other.afterLast))){
            return false;
        }
        if (this.fieldLists != other.fieldLists && (this.fieldLists == null || !this.fieldLists.equals(other.fieldLists))){
            return false;
        }
        return true;
    }

    @Override
    public int hashCode(){
        int hash = 5;
        hash = 83 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 83 * hash + (this.version != null ? this.version.hashCode() : 0);
        hash = 83 * hash + (this.format != null ? this.format.hashCode() : 0);
        hash = 83 * hash + (this.beforeFirst != null ? this.beforeFirst.hashCode() : 0);
        hash = 83 * hash + (this.before != null ? this.before.hashCode() : 0);
        hash = 83 * hash + (this.after != null ? this.after.hashCode() : 0);
        hash = 83 * hash + (this.afterLast != null ? this.afterLast.hashCode() : 0);
        hash = 83 * hash + (this.fieldLists != null ? this.fieldLists.hashCode() : 0);
        return hash;
    }
}
