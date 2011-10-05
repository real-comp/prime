package com.realcomp.data.schema;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 *
 * @author krenfro
 */
public class SchemaFieldList extends ArrayList<SchemaField> {
    
    public static final Pattern DEFAULT_CLASSIFIER = Pattern.compile(".*");
      
    private Pattern classifier = DEFAULT_CLASSIFIER;
    
    public SchemaFieldList(){
        super();
    }
    
    public SchemaFieldList(Pattern classifier){
        
        if (classifier == null)
            throw new IllegalArgumentException("classifier is null");
        this.classifier = Pattern.compile(classifier.toString());
    }
    
    public SchemaFieldList(SchemaFieldList copy){
        super(copy);
        this.classifier = Pattern.compile(copy.classifier.toString());
    }

    public boolean isDefaultClassifier(){
        return classifier.equals(DEFAULT_CLASSIFIER);
    }

    public Pattern getClassifier() {
        return classifier;
    }

    public void setClassifier(Pattern classifier) {
        this.classifier = classifier;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final SchemaFieldList other = (SchemaFieldList) obj;
        if (this.classifier != other.classifier && (this.classifier == null || !this.classifier.equals(other.classifier)))
            return false;
        
        return super.equals(obj); //DANGER
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.classifier != null ? this.classifier.hashCode() : 0);
        return hash;
    }
    
    
    
}
