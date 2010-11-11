package com.realcomp.data.schema;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author krenfro
 */
@XStreamAlias("classifier")
public class Classifier {

    @XStreamAsAttribute
    protected String id;

    @XStreamAsAttribute
    protected String regex;

    protected transient Pattern pattern;

    @XStreamImplicit
    protected List<SchemaField> fields;

    protected Classifier(){
        fields = new ArrayList<SchemaField>();
    }

    public Classifier(String id, String regex){
        this();

        if (id == null)
            throw new IllegalArgumentException("id is null");
        if (id.isEmpty())
            throw new IllegalArgumentException("id is empty");
        if (regex == null)
            throw new IllegalArgumentException("regex is null");
        if (regex.isEmpty())
            throw new IllegalArgumentException("regex is empty");

        this.id = id;
        this.regex = regex;
        pattern = Pattern.compile(regex);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id == null)
            throw new IllegalArgumentException("id is null");
        if (id.isEmpty())
            throw new IllegalArgumentException("id is empty");
        this.id = id;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        if (regex == null)
            throw new IllegalArgumentException("regex is null");
        if (regex.isEmpty())
            throw new IllegalArgumentException("regex is empty");
        this.regex = regex;
        pattern = Pattern.compile(regex);
    }


    /**
     * @return current list of SchemaFields
     */
    public List<SchemaField> getFields() {
        return fields;
    }

    /**
     *
     * @param fields not null or empty. no duplicate named SchemaFields.
     */
    public void setFields(List<SchemaField> fields) {
        if (fields == null)
            throw new IllegalArgumentException("fields is null");
        if (fields.isEmpty())
            throw new IllegalArgumentException("fields is empty");

        this.fields.clear();
        for(SchemaField field: fields)
            addSchemaField(field);
    }

    /**
     *
     * @param field not null. Name must be unique for this Classifier.
     */
    public void addSchemaField(SchemaField field){
        if (field == null)
            throw new IllegalArgumentException("field is null");

        if (isNameAlreadyUsed(field.getName()))
            throw new IllegalArgumentException(
                    String.format("A field with name [%s] is already defined.", field.getName()));

        fields.add(field);
    }

    protected boolean isNameAlreadyUsed(String name){
        if (name == null)
            throw new IllegalArgumentException("name is null");

        for (SchemaField existing: fields)
            if (name.equals(existing.getName()))
                return true;
        return false;
    }

    /**
     *
     * @param data data to classify. not null
     * @return true if the data matches the regex; else false
     */
    public boolean supports(String data){
        if (pattern == null)
            pattern = Pattern.compile(regex);
        return pattern.matcher(data).matches();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Classifier other = (Classifier) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id))
            return false;
        if ((this.regex == null) ? (other.regex != null) : !this.regex.equals(other.regex))
            return false;
        if (this.fields != other.fields && (this.fields == null || !this.fields.equals(other.fields)))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 41 * hash + (this.regex != null ? this.regex.hashCode() : 0);
        hash = 41 * hash + (this.fields != null ? this.fields.hashCode() : 0);
        return hash;
    }
}
