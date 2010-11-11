package com.realcomp.data.schema;

import com.realcomp.data.Operation;
import com.realcomp.data.record.parser.RecordParser;
import com.realcomp.data.schema.xml.DataViewConverter;
import com.realcomp.data.schema.xml.RecordParserConverter;
import com.realcomp.data.view.DataView;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author krenfro
 */
@XStreamAlias("file-schema")
public class FileSchema {

    @XStreamAsAttribute
    protected String name;

    @XStreamAsAttribute
    protected String version;

    @XStreamConverter(RecordParserConverter.class)
    protected RecordParser parser;

    protected List<Operation> preops;
    protected List<Operation> postops;
    protected List<SchemaField> fields;


    @XStreamImplicit
    protected List<Classifier> classifiers;

    public FileSchema(){
        fields = new ArrayList<SchemaField>();
    }

    public RecordParser getParser() {
        return parser;
    }

    public void setParser(RecordParser parser) {
        if (parser == null)
            throw new IllegalArgumentException("parser is null");
        this.parser = parser;
    }

    public List<Classifier> getClassifiers() {
        return classifiers;
    }

    public void setClassifiers(List<Classifier> classifiers) throws SchemaException{

        if (classifiers == null){
            this.classifiers = null;
        }
        else{
            if (this.classifiers != null)
                this.classifiers.clear();
            for (Classifier c: classifiers)
                addClassifier(c);
        }
    }

    public void addClassifier(Classifier classifier) throws SchemaException{
        if (classifier == null)
            throw new IllegalArgumentException("classifier is null");
        if (classifier.getFields() == null)
            throw new SchemaException("no fields specified for the classifier");
        if (classifier.getFields().isEmpty())
            throw new SchemaException("no fields specified for the classifier");

        if (classifiers == null)
            classifiers = new ArrayList<Classifier>();
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

        
        if (classifiers != null){
            for (Classifier c: classifiers){
                if (c.supports(data))
                    return c.getFields();
            }
        }

        return fields;
    }

    /**
     *
     * @return all SchemaFields defined for this Schema
     */
    public List<SchemaField> getSchemaFields() {
        return fields;
    }

    /**
     * @param fields not null nor empty.
     * @throws SchemaException if there is already a field with the same name as one of the fields
     */
    public void setSchemaFields(List<SchemaField> fields) throws SchemaException{
        if (fields == null)
            throw new IllegalArgumentException("fields is null");
        if (fields.isEmpty())
            throw new IllegalArgumentException("fields is empty");

        this.fields.clear();
        for (SchemaField field: fields)
            addSchemaField(field);
    }

    /**
     * Add a field to the schema
     * @param field
     * @throws SchemaException if there is already a field with the same name
     */
    public void addSchemaField(SchemaField field) throws SchemaException{
        if (field == null)
            throw new IllegalArgumentException("field is null");

        verifyUniqueName(field.getName());
        fields.add(field);        
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
    public List<Operation> getPostops() {
        return postops;
    }

    /**
     * Set all Operations to perform on all fields after all field specific operations are finished.
     * @param postops null will clear existing list
     */
    public void setPostops(List<Operation> postops) {

        if (postops == null){
            this.postops = null;
        }
        else{
            if (this.postops != null)
                this.postops.clear();
            for (Operation op: postops)
                addPostop(op);
        }
    }

    /**
     * Add an Operation to the postop operations group, to be run after all field specific
     * Operations are performed.
     * @param op not null
     */
    public void addPostop(Operation op){
        if (op == null)
            throw new IllegalArgumentException("op is null");
        if (postops == null)
            postops = new ArrayList<Operation>();

        this.postops.add(op);
    }

    /**
     *
     * @return all Operations to perform on all fields before any field specific Operations are
     * performed, or null if none specified.
     */
    public List<Operation> getPreops() {
        return preops;
    }

    /**
     * Set all Operations to perform on all fields before any field specific Operations are
     * performed.
     * @param preops null will clear list
     */
    public void setPreops(List<Operation> preops) {
        if (preops == null){
            this.preops = null;
        }
        else{
            if (this.preops != null)
                this.preops.clear();
            for (Operation op: preops)
                addPreop(op);
        }
    }

    /**
     * Add an Operation to the preop operations group, to be run before all field specific
     * Operations are performed.
     * @param op not null
     */
    public void addPreop(Operation op){
        if (op == null)
            throw new IllegalArgumentException("op is null");

        if (preops == null)
            preops = new ArrayList<Operation>();
        this.preops.add(op);
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
        if (this.parser != other.parser && (this.parser == null || !this.parser.equals(other.parser)))
            return false;
        if (this.preops != other.preops && (this.preops == null || !this.preops.equals(other.preops)))
            return false;
        if (this.postops != other.postops && (this.postops == null || !this.postops.equals(other.postops)))
            return false;
        if (this.fields != other.fields && (this.fields == null || !this.fields.equals(other.fields)))
            return false;
        if (this.classifiers != other.classifiers && (this.classifiers == null || !this.classifiers.equals(other.classifiers)))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 67 * hash + (this.version != null ? this.version.hashCode() : 0);
        hash = 67 * hash + (this.parser != null ? this.parser.hashCode() : 0);
        hash = 67 * hash + (this.preops != null ? this.preops.hashCode() : 0);
        hash = 67 * hash + (this.postops != null ? this.postops.hashCode() : 0);
        hash = 67 * hash + (this.fields != null ? this.fields.hashCode() : 0);
        hash = 67 * hash + (this.classifiers != null ? this.classifiers.hashCode() : 0);
        return hash;
    }


    
}
