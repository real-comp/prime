package com.realcomp.data.schema;

import com.realcomp.data.MultiFieldOperation;
import com.realcomp.data.Operation;
import com.realcomp.data.record.Record;
import com.realcomp.data.record.reader.RecordReader;
import com.realcomp.data.record.writer.RecordWriter;
import com.realcomp.data.schema.xml.RecordReaderConverter;
import com.realcomp.data.schema.xml.RecordWriterConverter;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author krenfro
 */
@XStreamAlias("file-schema")
public class FileSchema {

    protected static final Logger logger = Logger.getLogger(FileSchema.class.getName());

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
    protected List<SchemaField> fields;

    @XStreamImplicit
    protected List<Classifier> classifiers;


    public FileSchema(){
        fields = new ArrayList<SchemaField>();
        classifiers = new ArrayList<Classifier>();
    }

    public FileSchema(FileSchema copy) throws SchemaException{
        fields = new ArrayList<SchemaField>();
        classifiers = new ArrayList<Classifier>();
        this.name = copy.name;
        this.version = copy.version;

        try{
            reader = copy.getReader();
            writer = copy.getWriter();
        }
        catch(SchemaException ignored){
        }
        
        setFields(copy.getFields());
        setClassifiers(copy.getClassifiers());
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

    public List<Classifier> getClassifiers() {

        if (classifiers == null)
            classifiers = new ArrayList<Classifier>();

        return classifiers;
    }

    public void setClassifiers(List<Classifier> classifiers) throws SchemaException{

        if (classifiers == null)
            throw new IllegalArgumentException("classifiers is null");
        
        this.classifiers.clear();
        for (Classifier c: classifiers)
            addClassifier(c);
    }

    public void addClassifier(Classifier classifier) throws SchemaException{
        if (classifier == null)
            throw new IllegalArgumentException("classifier is null");
        if (classifier.getFields() == null)
            throw new SchemaException("no fields specified for the classifier");
        if (classifier.getFields().isEmpty())
            throw new SchemaException("no fields specified for the classifier");

        classifiers.add(classifier);
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

        for (Classifier c: classifiers){
            if (c.supports(data))
                return c.getFields();
        }

        return fields;
    }

    /**
     * Classify a record and return the layout (List of SchemaFields) that match.
     * If multiple SchemaFields are defined that contain Fields named in the Record,
     * then the layout defined first in the Schema will be returned.
     *
     * @param record
     * @return
     * @throws SchemaException if no defined layout supports the Record
     */
    public List<SchemaField> classify(Record record) throws SchemaException{
        if (record == null)
            throw new IllegalArgumentException("record is null");

        List<SchemaField> result = null;

        for (Classifier c: classifiers){
            if (c.supports(record)){
                if (result == null)
                    result = c.getFields();
                else if (result.size() < c.getFields().size())
                    result = c.getFields();
            }
        }

        if (result == null){
            Classifier defaultClassifier = new Classifier();
            defaultClassifier.setFields(fields);
            if (defaultClassifier.supports(record))
                result = fields;
        }

        if (result == null)
            throw new SchemaException("Unable to find layout that supports Record: " + record);

        return result;
    }


    public SchemaField getField(String name){
        
        if (name == null)
            throw new IllegalArgumentException("name is null");
        if (fields == null)
            return null;

        for (SchemaField field: fields)
            if (field.getName().equals(name))
                return field;

        return null;
    }
        
    /**
     *
     * @return all SchemaFields defined for this Schema
     */
    public List<SchemaField> getFields() {

        if (fields == null)
            fields = new ArrayList<SchemaField>();
        
        return fields;
    }

    /**
     * @param fields not null nor empty.
     * @throws SchemaException if there is already a field with the same name as one of the fields
     */
    public void setFields(List<SchemaField> fields) throws SchemaException{
        if (fields == null)
            throw new IllegalArgumentException("fields is null");
        if (fields.isEmpty())
            throw new IllegalArgumentException("fields is empty");

        this.fields.clear();
        for (SchemaField field: fields)
            addField(field);
    }

    /**
     * Add a field to the schema
     * @param field
     * @throws SchemaException if there is already a field with the same name
     */
    public void addField(SchemaField field) throws SchemaException{
        if (field == null)
            throw new IllegalArgumentException("field is null");

        verifyUniqueName(field.getName());
        fields.add(new SchemaField(field));
    }

    protected void verifyUniqueName(String name) throws SchemaException{
        if (name == null)
            throw new IllegalArgumentException("name is null");
        
        for (SchemaField existing: fields)
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

    @Override
    public String toString(){
        StringBuilder s = new StringBuilder(name);
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
        if (this.classifiers != other.classifiers && (this.classifiers == null || !this.classifiers.equals(other.classifiers)))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 19 * hash + (this.version != null ? this.version.hashCode() : 0);
        hash = 19 * hash + (this.reader != null ? this.reader.hashCode() : 0);
        hash = 19 * hash + (this.writer != null ? this.writer.hashCode() : 0);
        hash = 19 * hash + (this.beforeFirst != null ? this.beforeFirst.hashCode() : 0);
        hash = 19 * hash + (this.before != null ? this.before.hashCode() : 0);
        hash = 19 * hash + (this.after != null ? this.after.hashCode() : 0);
        hash = 19 * hash + (this.afterLast != null ? this.afterLast.hashCode() : 0);
        hash = 19 * hash + (this.fields != null ? this.fields.hashCode() : 0);
        hash = 19 * hash + (this.classifiers != null ? this.classifiers.hashCode() : 0);
        return hash;
    }
}
