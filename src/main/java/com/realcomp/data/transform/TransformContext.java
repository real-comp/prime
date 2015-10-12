package com.realcomp.data.transform;

import com.realcomp.data.record.Record;
import com.realcomp.data.schema.Field;
import com.realcomp.data.schema.FieldList;
import com.realcomp.data.schema.Schema;
import com.realcomp.data.validation.Severity;
import com.realcomp.data.validation.ValidationException;
import com.realcomp.data.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class TransformContext{

    private static final Logger logger = Logger.getLogger(TransformContext.class.getName());

    private Schema schema;
    private List<Field> fields;
    private String key;
    private Record record;
    private Severity validationExceptionThreshold = Severity.HIGH;
    private long recordCount;

    public TransformContext(){
        record = new Record();
    }

    public TransformContext(TransformContext copy){
        this();

        schema = new Schema(copy.schema);
        if (copy.fields != null){
            fields = new ArrayList<>();
            for (Field field : copy.fields){
                fields.add(new Field(field));
            }
        }
        key = copy.key;
        validationExceptionThreshold = copy.validationExceptionThreshold;
        record = new Record(copy.record);
        recordCount = copy.recordCount;

    }

    @Override
    public String toString(){
        if (schema != null && record != null){
            return schema.toString(record);
        }
        else if (fields != null && record != null){
            return new FieldList(fields).toString(record);
        }
        else{
            return record.toString();
        }
    }

    public void handleValidationException(Validator validator, ValidationException ex) throws ValidationException{

        Severity severity = validator.getSeverity();
        String message = String.format("%s for [%s] in record [%s]", new Object[]{ex.getMessage(), key, toString()});

        if (severity.ordinal() >= validationExceptionThreshold.ordinal()){
            throw new ValidationException(message, ex);
        }
        else{
            switch (severity){
                case LOW:
                    logger.log(Level.INFO, message);
                    break;
                case MEDIUM:
                    logger.log(Level.WARNING, message);
                    break;
                case HIGH:
                    logger.log(Level.SEVERE, message);
                    break;
            }
        }
    }

    public Record getRecord(){
        return record;
    }

    public void setRecord(Record record){
        if (record == null){
            throw new IllegalArgumentException("record is null");
        }
        this.record = record;
    }

    public Schema getSchema(){
        return schema;
    }

    public void setSchema(Schema schema){
        this.schema = schema;
    }

    public List<Field> getFields(){
        return fields;
    }

    public void setFields(List<Field> fields){
        this.fields = fields;
    }

    public String getKey(){
        return key;
    }

    public void setKey(String key){
        if (key == null){
            throw new IllegalArgumentException("key is null");
        }
        this.key = key;
    }

    public Severity getValidationExceptionThreshold(){
        return validationExceptionThreshold;
    }

    public void setValidationExceptionThreshold(Severity validationExceptionThreshold){
        this.validationExceptionThreshold = validationExceptionThreshold;
    }

    public long getRecordCount(){
        return recordCount;
    }

    public void setRecordCount(long recordCount){
        this.recordCount = recordCount;
    }

    @Override
    public boolean equals(Object obj){
        if (obj == null){
            return false;
        }
        if (getClass() != obj.getClass()){
            return false;
        }
        final TransformContext other = (TransformContext) obj;
        if (this.schema != other.schema && (this.schema == null || !this.schema.equals(other.schema))){
            return false;
        }
        if (this.fields != other.fields && (this.fields == null || !this.fields.equals(other.fields))){
            return false;
        }
        if ((this.key == null) ? (other.key != null) : !this.key.equals(other.key)){
            return false;
        }
        if (this.validationExceptionThreshold != other.validationExceptionThreshold){
            return false;
        }
        return true;
    }

    @Override
    public int hashCode(){
        int hash = 7;
        hash = 79 * hash + (this.schema != null ? this.schema.hashCode() : 0);
        hash = 79 * hash + (this.fields != null ? this.fields.hashCode() : 0);
        hash = 79 * hash + (this.key != null ? this.key.hashCode() : 0);
        hash = 79 * hash + (this.validationExceptionThreshold != null ? this.validationExceptionThreshold.hashCode() : 0);
        return hash;
    }
}
