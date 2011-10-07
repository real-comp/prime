package com.realcomp.data.schema;

import com.realcomp.data.MultiFieldOperation;
import com.realcomp.data.Operation;
import com.realcomp.data.record.Record;
import com.realcomp.data.record.reader.RecordReader;
import com.realcomp.data.record.writer.RecordWriter;
import com.realcomp.data.schema.xml.FieldListConverter;
import com.realcomp.data.schema.xml.RecordReaderConverter;
import com.realcomp.data.schema.xml.RecordWriterConverter;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 *
 * @author krenfro
 */
@XStreamAlias("file-schema")
public class FileSchema {

    protected static final Logger logger = Logger.getLogger(FileSchema.class.getName());
    protected static final Pattern DEFAULT_CLASSIFIER = Pattern.compile(".*");

    @XStreamAsAttribute
    protected String name;

    @XStreamAsAttribute
    protected String version;

    @XStreamConverter(RecordReaderConverter.class)
    protected RecordReader reader;

    @XStreamConverter(RecordWriterConverter.class)
    protected RecordWriter writer;

    protected List<Operation> beforeFirst;
    protected List<Operation> before;
    protected List<Operation> after;
    protected List<Operation> afterLast;
    
    @XStreamImplicit(itemFieldName="fields")
    protected List<FieldList> fieldLists;
    
    public FileSchema(){
        fieldLists = new ArrayList<FieldList>();
        fieldLists.add(new FieldList());
    }

    public FileSchema(FileSchema copy) throws SchemaException{
        fieldLists = new ArrayList<FieldList>();
        for (FieldList fieldList: copy.fieldLists)
            fieldLists.add(new FieldList(fieldList));
        
        this.name = copy.name;
        this.version = copy.version;
        setBeforeFirstOperations(copy.getBeforeFirstOperations());
        setBeforeOperations(copy.getBeforeOperations());
        setAfterLastOperations(copy.getAfterLastOperations());
        setAfterOperations(copy.getAfterOperations());
    }

    public RecordReader getReader() throws SchemaException{
        if (reader != null)
            reader.setSchema(this);
        return reader;
    }

    public void setReader(RecordReader reader) throws SchemaException{
        if (reader == null)
            throw new IllegalArgumentException("reader is null");
        this.reader = reader;
        this.reader.setSchema(this);
    }

    public RecordWriter getWriter() throws SchemaException{
        if (writer != null)
            writer.setSchema(this);
        return writer;
    }

    public void setWriter(RecordWriter writer) throws SchemaException{
        if (writer == null)
            throw new IllegalArgumentException("writer is null");
        this.writer = writer;
        this.writer.setSchema(this);
    }
    
    
    /**
     * Classify some data and return the FieldList that should be used to parse the data.
     * If no classifiers have been specified, or there is not a classifier match, the
     * default FieldList is returned.
     * 
     * @param data not null
     * @return the FieldList that should be used to parse the data. never null
     * @throws SchemaException if no defined layout supports the data
     */
    public FieldList classify(String data) throws SchemaException{

        if (data == null)
            throw new IllegalArgumentException("data is null");
        
        FieldList match = null;
        
        for (FieldList fieldList: fieldLists){
            if (fieldList.supports(data)){
                if (match == null){
                    match = fieldList;
                }
                else if (match != null && !fieldList.isDefaultClassifier()){
                    match = fieldList; //allow a more specific FieldList
                }
            }
        }
        
        if (match == null)
            throw new SchemaException("The schema does not support the specified data.");
        
        return match;
    }

    
    /**
     * Classify a record and return the FieldList that matches.
     * If multiple FieldLists support the specified Record, then the FieldList that
     * is not the <i>default</i> is returned.  If multiple FieldLists support the specified Record,
     * and neither are the <i>default</i> then the one defined first is returned.
     *
     * @param record not null
     * @return the FieldList that should be used for the Record. never null
     * @throws SchemaException if no defined layout supports the Record
     */
    public FieldList classify(Record record) throws SchemaException{
        
        if (record == null)
            throw new IllegalArgumentException("record is null");
        
        FieldList match = null;
        
        for (FieldList fieldList: fieldLists){
            if (fieldList.supports(record)){
                 if (match == null){
                    match = fieldList;
                }
                else if (match != null && !fieldList.isDefaultClassifier()){
                    match = fieldList; //allow a more specific FieldList
                }
            }
        }
        
        if (match == null)
            throw new SchemaException("The schema does not support the specified Record");
        
        return match;
    }
    
    
    /**
     * Returns the FieldList that has the default classifier (match anything),
     * or is defined first.
     * 
     * @return the default FieldList, or the FieldList that was defined first.
     */
    public FieldList getDefaultFieldList(){
        
        FieldList retVal = null;
        for (FieldList fieldList: fieldLists){
            if (retVal == null)
                retVal = fieldList;
            if (fieldList.isDefaultClassifier())
                retVal = fieldList;
        }
        
        return retVal;
    }

    /**
     * 
     * @return the FieldLists supported by this FileSchema
     */
    public List<FieldList> getFieldLists() {
        return fieldLists;
    }

    /**
     * 
     * @param fieldLists not null
     */
    public void setFieldLists(List<FieldList> fieldLists) {
        if (fieldLists == null)
            throw new IllegalArgumentException("fieldLists is null");
        
        this.fieldLists.clear();
        for (FieldList f: fieldLists)
            this.fieldLists.add(new FieldList(f));
    }
    
    /**
     * 
     * @param fieldList to be added
     */
    public void addFieldList(FieldList fieldList){
        if (fieldList == null)
            throw new IllegalArgumentException("fieldList is null");
        
        if (fieldList.isDefaultClassifier()){
            logger.log(Level.FINE, "replacing default field list");
            fieldLists.remove(getDefaultFieldList());
        }
        
        fieldLists.add(new FieldList(fieldList));
    }
    
    /**
     * 
     * @param fieldList to be removed
     * @return true if removed; else false
     */
    public boolean removeFieldList(FieldList fieldList){
        if (fieldList == null)
            throw new IllegalArgumentException("fieldList is null");
        return fieldLists.remove(fieldList);
    }
    
    
    public void addField(Field field){
        getDefaultFieldList().add(field);
    }
    
    public boolean removeField(Field field){
        return getDefaultFieldList().remove(field);
    }
    
    public Field getField(String name){
        return getDefaultFieldList().get(name);
    }
    
    /**
     *
     * @return optional name for this schema, or null
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return all Operations to perform on all Fields after all Field specific operations are
     * finished, or null if none specified.
     *
     */
    public List<Operation> getAfterOperations() {
        return after;
    }

    /**
     * Set all Operations to perform on all Fields after all Field specific operations are finished.
     * @param after null will clear existing list
     */
    public void setAfterOperations(List<Operation> after) {

        if (after == null){
            this.after = null;
        }
        else{
            if (this.after != null)
                this.after.clear();
            for (Operation op: after)
                addAfterOperation(op);
        }
    }

    /**
     * Add an Operation to the after operations group, to be run after all Field specific
     * Operations are performed.
     * @param op not null
     */
    public void addAfterOperation(Operation op){
        if (op == null)
            throw new IllegalArgumentException("op is null");
        if (after == null)
            after = new ArrayList<Operation>();

        this.after.add(op);
    }



    /**
     * @return all Operations to perform after all Records have been processed, or null
     * if none specified.
     *
     */
    public List<Operation> getAfterLastOperations() {
        return afterLast;
    }

    /**
     * Set all Operations to perform after all Records have been processed.
     * @param afterLast null will clear existing list
     */
    public void setAfterLastOperations(List<Operation> afterLast) {

        if (afterLast == null){
            this.afterLast = null;
        }
        else{
            if (this.afterLast != null)
                this.afterLast.clear();
            for (Operation op: afterLast)
                addAfterOperation(op);
        }
    }

    /**
     * Add an Operation to the afterLast operations group, to be run after all Records
     * have been processed.
     * @param op not null
     */
    public void addAfterLastOperation(Operation op){
        if (op == null)
            throw new IllegalArgumentException("op is null");
        if (afterLast == null)
            afterLast = new ArrayList<Operation>();

        this.afterLast.add(op);
    }



    /**
     *
     * @return all Operations to perform on all Fields before any Field specific Operations are
     * performed, or null if none specified.
     */
    public List<Operation> getBeforeOperations() {
        return before;
    }

    /**
     * Set all Operations to perform on all Fields before any Field specific Operations are
     * performed.
     * @param before null will clear list
     */
    public void setBeforeOperations(List<Operation> before) {
        if (before == null){
            this.before = null;
        }
        else{
            if (this.before != null)
                this.before.clear();
            for (Operation op: before)
                addBeforeOperation(op);
        }
    }

    /**
     * Add an Operation to the before operations group, to be run before all Field specific
     * Operations are performed.
     * @param op not null, not a MultiFieldOperation
     */
    public void addBeforeOperation(Operation op){
        if (op == null)
            throw new IllegalArgumentException("op is null");
        if (op instanceof MultiFieldOperation)
            throw new IllegalArgumentException(
                    "You cannot specify a MultiFieldOperation as a 'before' operation");

        if (before == null)
            before = new ArrayList<Operation>();
        this.before.add(op);
    }



    /**
     *
     * @return all Operations to perform before any Records are processed, or null
     * if none specified.
     */
    public List<Operation> getBeforeFirstOperations() {
        return beforeFirst;
    }

    /**
     * Set all Operations to perform before any Records are processed.
     * @param beforeFirst null will clear list
     */
    public void setBeforeFirstOperations(List<Operation> beforeFirst) {
        if (beforeFirst == null){
            this.beforeFirst = null;
        }
        else{
            if (this.beforeFirst != null)
                this.beforeFirst.clear();
            for (Operation op: beforeFirst)
                addBeforeFirstOperation(op);
        }
    }

    /**
     * Add an Operation to the beforeFirst operations group, to be run before any Records
     * are processed.
     * @param op not null, not a MultiFieldOperation
     */
    public void addBeforeFirstOperation(Operation op){
        if (op == null)
            throw new IllegalArgumentException("op is null");
        if (op instanceof MultiFieldOperation)
            throw new IllegalArgumentException(
                    "You cannot specify a MultiFieldOperation as a 'beforeFirst' operation");

        if (beforeFirst == null)
            beforeFirst = new ArrayList<Operation>();
        this.beforeFirst.add(op);
    }


    /**
     * @return optional version for this Schema, or null.
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version
     */
    public void setVersion(String version) {
        this.version = version;
    }           

     

    @Override
    public String toString(){
        StringBuilder s = new StringBuilder(name == null ? "" : name);
        if (version != null && !version.isEmpty())
            s.append(" (").append(version).append(")");
        return s.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final FileSchema other = (FileSchema) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name))
            return false;
        if ((this.version == null) ? (other.version != null) : !this.version.equals(other.version))
            return false;
        if (this.reader != other.reader && (this.reader == null || !this.reader.equals(other.reader)))
            return false;
        if (this.writer != other.writer && (this.writer == null || !this.writer.equals(other.writer)))
            return false;
        if (this.beforeFirst != other.beforeFirst && (this.beforeFirst == null || !this.beforeFirst.equals(other.beforeFirst)))
            return false;
        if (this.before != other.before && (this.before == null || !this.before.equals(other.before)))
            return false;
        if (this.after != other.after && (this.after == null || !this.after.equals(other.after)))
            return false;
        if (this.afterLast != other.afterLast && (this.afterLast == null || !this.afterLast.equals(other.afterLast)))
            return false;
        if (this.fieldLists != other.fieldLists && (this.fieldLists == null || !this.fieldLists.equals(other.fieldLists)))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 53 * hash + (this.version != null ? this.version.hashCode() : 0);
        hash = 53 * hash + (this.reader != null ? this.reader.hashCode() : 0);
        hash = 53 * hash + (this.writer != null ? this.writer.hashCode() : 0);
        hash = 53 * hash + (this.beforeFirst != null ? this.beforeFirst.hashCode() : 0);
        hash = 53 * hash + (this.before != null ? this.before.hashCode() : 0);
        hash = 53 * hash + (this.after != null ? this.after.hashCode() : 0);
        hash = 53 * hash + (this.afterLast != null ? this.afterLast.hashCode() : 0);
        hash = 53 * hash + (this.fieldLists != null ? this.fieldLists.hashCode() : 0);
        return hash;
    }

    
}
