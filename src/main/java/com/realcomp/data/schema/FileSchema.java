package com.realcomp.data.schema;

import com.realcomp.data.MultiFieldOperation;
import com.realcomp.data.Operation;
import com.realcomp.data.record.Record;
import com.realcomp.data.record.reader.RecordReader;
import com.realcomp.data.record.writer.RecordWriter;
import com.realcomp.data.schema.xml.RecordReaderConverter;
import com.realcomp.data.schema.xml.RecordWriterConverter;
import com.realcomp.data.schema.xml.SchemaFieldsConverter;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;

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
    
    protected LinkedHashSet<SchemaFieldList> fields;
    
    public FileSchema(){
        fields = new LinkedHashSet<SchemaFieldList>();
        fields.add(new SchemaFieldList());
    }

    public FileSchema(FileSchema copy) throws SchemaException{
        fields = new LinkedHashSet<SchemaFieldList>();
        for (SchemaFieldList fieldList: fields)
            fields.add(new SchemaFieldList(fieldList));
        
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
     * Classify some data and return the List of SchemaFields that should be used to parse the data.
     * If no classifiers have been specified, or there is not a classifier match, the
     * default Field list is returned.
     * 
     * @param data not null
     * @return the List of SchemaFields that should be used to parse the data.
     */
    public List<SchemaField> classify(String data){

        if (data == null)
            throw new IllegalArgumentException("data is null");

        List<SchemaField> result = fields.get(DEFAULT_CLASSIFIER);
        
        for (Pattern pattern: fields.keySet()){            
            if (!pattern.equals(DEFAULT_CLASSIFIER) && pattern.matcher(data).matches())
                result = fields.get(pattern);
        }
        
        return result;
    }

    
    /**
     * Classify a record and return the layout (List of SchemaFields) that match.
     * If multiple SchemaFields are defined that contain Fields named in the Record,
     * then the layout defined first in the Schema will be returned.
     *
     * @param record not null
     * @return
     * @throws SchemaException if no defined layout supports the Record
     */
    public List<SchemaField> classify(Record record) throws SchemaException{
        
        if (record == null)
            throw new IllegalArgumentException("record is null");
        
        List<SchemaField> result = null;
        List<String> fieldsInRecord = getFieldNames(record);
        
        for (List<SchemaField> f: fields.values()){            
            if (getFieldNames(f).containsAll(fieldsInRecord)){
                result = f;
                break;
            }
        }
        
        if (result == null)
            throw new SchemaException("Unable to find layout that supports Record: " + record);

        return result;
    }


    /**
     * 
     * @param schemaFields
     * @return a list containing the name of each SchemaField
     */
    private List<String> getFieldNames(List<SchemaField> schemaFields){
        List<String> names = new ArrayList<String>();
        for (SchemaField schemaField: schemaFields)
            names.add(schemaField.getName());
        return names;
    }
    
    /**
     * 
     * @param record
     * @return a list containing the key of each (top-level) field in the record
     */
    private List<String> getFieldNames(Record record){
        List<String> names = new ArrayList<String>();
        names.addAll(record.keySet());
        return names;
    }
    
    
    /**
     * 
     * @param name
     * @return the named field from the default list of fields
     */
    public SchemaField getField(String name){
        
        if (name == null)
            throw new IllegalArgumentException("name is null");
        
        for (SchemaField field: fields.get(DEFAULT_CLASSIFIER))
            if (field.getName().equals(name))
                return field;

        return null;
    }
        
    
    public List<SchemaField> getDefaultFields(){
        return fields.get(DEFAULT_CLASSIFIER);
    }
    
    public List<SchemaField> setDefaultFields(List<SchemaField> schemaFields){
        return fields.put(DEFAULT_CLASSIFIER, schemaFields);
    }
    
    /**
     *
     * @return all SchemaFields defined for this Schema
     */
    public List<SchemaField> getFields(Pattern classifier) {

        return fields.get(classifier);
    }
    
    
    public List<SchemaField> setFields(Pattern classifier, List<SchemaField> schemaFields) throws SchemaException{
        if (classifier == null)
            throw new IllegalArgumentException("pattern is null");
        if (schemaFields == null)
            throw new IllegalArgumentException("fields is null");
        if (schemaFields.isEmpty())
            throw new IllegalArgumentException("fields is empty");
        
        List<SchemaField> copy = new ArrayList<SchemaField>();
        for (SchemaField f: schemaFields)
            copy.add(new SchemaField(f));

        return fields.put(Pattern.compile(classifier.toString()), copy);
    }

    
    public List<Pattern> getClassifiers(){
        List<Pattern> classifiers = new ArrayList<Pattern>();
        
        if (fields.size() > 1){
            classifiers.addAll(fields.keySet());
            classifiers.remove(DEFAULT_CLASSIFIER);            
        }
        
        return classifiers;
    }
    
    public boolean hasClassifiers(){
        return fields.size() > 1;
    }
    
    /**
     * Add a field to the default list of fields.
     * @param field
     * @throws SchemaException if there is already a field with the same name
     */
    public void addField(SchemaField field) throws SchemaException{
        if (field == null)
            throw new IllegalArgumentException("field is null");

        verifyUniqueName(field.getName());
        fields.get(DEFAULT_CLASSIFIER).add(new SchemaField(field));
    }

    protected void verifyUniqueName(String name) throws SchemaException{
        if (name == null)
            throw new IllegalArgumentException("name is null");
        
        for (SchemaField existing: fields.get(DEFAULT_CLASSIFIER))
            if (name.equals(existing.getName()))
                throw new SchemaException(
                    String.format(
                        "A field with name [%s] is already defined in schema [%s].",
                        name,
                        this.toString()));
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
     * @return all Operations to perform on all fields after fields specific operations are
     * finished, or null if none specified.
     *
     */
    public List<Operation> getAfterOperations() {
        return after;
    }

    /**
     * Set all Operations to perform on all fields after all field specific operations are finished.
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
     * Add an Operation to the after operations group, to be run after all field specific
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
     * @return all Operations to perform after all records have been processed, or null
     * if none specified.
     *
     */
    public List<Operation> getAfterLastOperations() {
        return afterLast;
    }

    /**
     * Set all Operations to perform after all records have been processed.
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
     * Add an Operation to the afterLast operations group, to be run after all records
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
     * @return all Operations to perform on all fields before any field specific Operations are
     * performed, or null if none specified.
     */
    public List<Operation> getBeforeOperations() {
        return before;
    }

    /**
     * Set all Operations to perform on all fields before any field specific Operations are
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
     * Add an Operation to the before operations group, to be run before all field specific
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
     * @return all Operations to perform before any records are processed, or null
     * if none specified.
     */
    public List<Operation> getBeforeFirstOperations() {
        return beforeFirst;
    }

    /**
     * Set all Operations to perform before any records are processed.
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
     * Add an Operation to the beforeFirst operations group, to be run before any records
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

    public boolean isKeyField(SchemaField field){
        for (Operation op: field.getOperations()){
            if (op instanceof com.realcomp.data.validation.field.Key)
                return true;
        }
        return false;
    }
    
    public boolean isForeignKeyField(SchemaField field){
        for (Operation op: field.getOperations()){
            if (op instanceof com.realcomp.data.validation.field.ForeignKey)
                return true;
        }
        return false;
    }
    
    /**
     * 
     * @return all 'key' SchemaFields, in the order defined by this FileSchema
     */
    public List<SchemaField> getKeyFields(){
        List<SchemaField> keyFields = new ArrayList<SchemaField>();
        for (SchemaField f: fields.get(DEFAULT_CLASSIFIER)){            
            if (isKeyField(f))
                keyFields.add(f);
        }
        
        return keyFields;
    }
    
    
    /**
     * 
     * @return all 'foreign key' SchemaFields, in the order defined by this FileSchema
     */
    public List<SchemaField> getForeignKeyFields(){
        List<SchemaField> foreignKeyFields = new ArrayList<SchemaField>();
        for (SchemaField f: fields.get(DEFAULT_CLASSIFIER)){            
            if (isForeignKeyField(f))
                foreignKeyFields.add(f);
        }
        
        return foreignKeyFields;
    }
    
    
    
    
    /**
     * List of Strings pulled from Fields in a Record that are marked as 'Keys'.
     * 
     * @param record not null
     * @return list of Key fields, as Strings, from the specified record. 
     */
    public List<String> getKeys(Record record){

        List<String> key = new ArrayList<String>();
        
        for (SchemaField f: getKeyFields()){                       
            Object value = record.get(f.getName());

            //Note: a key value may be NULL if the Record is not fully constructed.
            //For example, if a ValidationException is thrown during Record creation, the
            //Record creator may try to construct a helpful message using schema.toString(record).
            if (value != null)
                key.add(value.toString());
        }
        
        return key;
    }
    
    /**
     * A superior Record.toString() that uses this schema's knowledge of the Record to 
     * output a pipe "|" delimited string of the Record's keys, in the order defined
     * in the schema.
     * 
     * If no keys are defined in the schema, then return Record.toString().
     * 
     * @see Record#toString()
     * @param record
     * @return Pipe delimited String of the Record's keys
     */
    public String toString(Record record){
        List<String> keys = getKeys(record);
        String retVal = null;
        if (keys.isEmpty()){            
            try {
                StringBuilder s = new StringBuilder();
                List<SchemaField> schemaFields = record == null ? fields.get(DEFAULT_CLASSIFIER) : classify(record);
                boolean needDelimiter = false;
                Object fieldValue = null;
                for (SchemaField field: schemaFields){
                    if (needDelimiter)
                        s.append("|");
                    needDelimiter = true;
                    fieldValue = record.get(field.getName());
                    if (fieldValue != null)
                        s.append(fieldValue.toString());
                }
                retVal = s.toString();
            }
            catch (SchemaException ex) {
                retVal = record.toString(); //this shouldn't happen.
            }
            
        }
        else{
            retVal = StringUtils.join(keys, "|");
        }
        
        return retVal;
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
        if (this.fields != other.fields && (this.fields == null || !this.fields.equals(other.fields)))
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
        hash = 53 * hash + (this.fields != null ? this.fields.hashCode() : 0);
        return hash;
    }

    
}
