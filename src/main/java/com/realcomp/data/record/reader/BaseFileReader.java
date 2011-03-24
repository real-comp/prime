package com.realcomp.data.record.reader;

import com.realcomp.data.Field;
import com.realcomp.data.FieldFactory;
import com.realcomp.data.Operation;
import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.conversion.Converter;
import com.realcomp.data.record.Record;
import com.realcomp.data.schema.FileSchema;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.schema.SchemaField;
import com.realcomp.data.validation.Severity;
import com.realcomp.data.validation.ValidationException;
import com.realcomp.data.validation.Validator;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author krenfro
 */
public abstract class BaseFileReader implements RecordReader{

    protected static final Logger log = Logger.getLogger(BaseFileReader.class.getName());

    protected InputStream in;
    protected FileSchema schema;
    protected Severity validationExceptionThreshold = DEFAULT_VALIDATION_THREASHOLD;
    protected int skipLeading = 0;
    protected int skipTrailing = 0;

    @XStreamOmitField
    protected long count;
    
    @Override
    public Severity getValidationExceptionThreshold() {
        return validationExceptionThreshold;
    }

    @Override
    public void setValidationExceptionThreshold(Severity severity) {
        this.validationExceptionThreshold = severity;
    }

    /**
     *
     * @return number of leading records to skip. default 0
     */
    public int getSkipLeading() {
        return skipLeading;
    }

    /**
     *
     * @param skipLeading number of leading records to skip. >= 0
     */
    public void setSkipLeading(int skipLeading) {
        if (skipLeading < 0)
            throw new IllegalArgumentException(
                    String.format("skipLeading out of range: %s < 0", skipLeading));
        this.skipLeading = skipLeading;
    }

    /**
     *
     * @return number of trailing records to skip. default 0
     */
    public int getSkipTrailing() {
        return skipTrailing;
    }

    /**
     *
     * @param skipTrailing number of trailing records to skip. >= 0
     */
    public void setSkipTrailing(int skipTrailing) {
        if (skipTrailing < 0)
            throw new IllegalArgumentException(
                    String.format("skipTrailing out of range: %s < 0", skipTrailing));
        this.skipTrailing = skipTrailing;
    }
    
    @Override
    public void open(InputStream in){
        if (in == null)
            throw new IllegalArgumentException("in is null");
        this.in = in;
        count = 0;
        
    }

    @Override
    public void close(){
        IOUtils.closeQuietly(in);
    }

    protected void executeAfterLastOperations() throws ValidationException, ConversionException{
        
        if (schema != null){
            List<Operation> afterLast = schema.getAfterLastOperations();
            if (afterLast != null){
                for (Operation op: afterLast){
                    operate(op, "" + this.getCount(), "AFTER LAST RECORD");
                }
            }
        }
    }

    protected void executeBeforeFirstOperations() throws ValidationException, ConversionException{

        if (schema != null){
            List<Operation> afterLast = schema.getBeforeFirstOperations();
            if (afterLast != null){
                for (Operation op: afterLast){
                    operate(op, "", "BEFORE FIRST RECORD");
                }
            }
        }
    }
    
    @Override
    public void setSchema(FileSchema schema) throws SchemaException{
        this.schema = schema;
    }

    @Override
    public FileSchema getSchema() {
        return schema;
    }

    protected Record loadRecord(List<SchemaField> fields, String[] data)
            throws ValidationException, ConversionException{

        if (fields == null)
            throw new IllegalArgumentException("fields is null");
        if (data == null)
            throw new IllegalArgumentException("data is null");

        if (fields.size() != data.length)
            throw new ValidationException(
                    "number of fields in schema does not match data.",
                    fields.size() + " != " + data.length,
                    Severity.HIGH);

        Record record = new Record();
        String recordId = getRecordIdentifier(data);

        for (int x = 0; x < data.length; x++){
            SchemaField field = fields.get(x);
            record.put(field.getName(), loadField(field, data[x], recordId));
        }

        return record;
    }

    protected Field loadField(SchemaField schemaField, String data, String recordId)
            throws ValidationException, ConversionException{

        String original = data;
        try{
            data = operate(schema.getBeforeOperations(), data, recordId);
            data = operate(schemaField.getOperations(), data, recordId);
            data = operate(schema.getAfterOperations(), data, recordId);
        }
        catch(ValidationException ex){
            throw new ValidationException(schemaField.getName() + " " + ex.getMessage(), ex);
        }
        catch(ConversionException ex){
            throw new ConversionException(schemaField.getName() + " " + ex.getMessage(), ex);
        }
        
        return FieldFactory.create(schemaField.getType(), data);
    }

    protected String operate(List<Operation> operations, String data, String recordIdentifier) 
            throws ConversionException, ValidationException{
        
        if (operations == null)
            return data;
        for (Operation op: operations)
            data = operate(op, data, recordIdentifier);
        return data;
    }


    protected String operate(Operation op, String data, String recordIdentifier)
                throws ConversionException, ValidationException{

        if (op instanceof Validator){
            try {
                ((Validator) op).validate(data);
            }
            catch (ValidationException ex) {
                Severity severity = ((Validator) op).getSeverity();
                switch(severity){
                    case LOW:
                        log.log(Level.INFO, "{0} in record [{1}]",
                                new Object[]{ex.getMessage(), recordIdentifier});
                        break;
                    case MEDIUM:
                        log.log(Level.WARNING, "{0} in record [{1}]",
                                new Object[]{ex.getMessage(), recordIdentifier});
                        break;
                    case HIGH:
                        log.log(Level.SEVERE, "{0} in record [{1}]",
                                new Object[]{ex.getMessage(), recordIdentifier});
                        break;
                }

                if (severity.ordinal() >= validationExceptionThreshold.ordinal())
                    throw ex;
            }
        }
        else if (op instanceof Converter){
            data = ((Converter) op).convert(data);
        }
        else{
            throw new IllegalStateException("Unhandled operator: " + op.getClass().getName());
        }

        return data;
    }

    /**
     * @param data
     * @return up to 25 characters from data.
     */
    protected String getRecordIdentifier(String data){
        if (data == null || data.isEmpty())
            return "";
        return data.substring(0, Math.min(data.length(), 25));
    }

    /**
     * @param data
     * @return the first two entries from data, delimited by a colon.
     */
    protected String getRecordIdentifier(String[] data){
        if (data == null)
            return "";
        
        String id = "";
        for (int x = 0; x < data.length && x < 2; x++){
            if (x > 0)
                id = id.concat(":");
            id = id.concat(data[x]);
        }
        return id;
    }

    /** {@inheritDoc} */
    @Override
    public long getCount(){
        return count;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final BaseFileReader other = (BaseFileReader) obj;
        if (this.schema != other.schema && (this.schema == null || !this.schema.equals(other.schema)))
            return false;
        if (this.validationExceptionThreshold != other.validationExceptionThreshold)
            return false;
        if (this.skipLeading != other.skipLeading)
            return false;
        if (this.skipTrailing != other.skipTrailing)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 73 * hash + (this.schema != null ? this.schema.hashCode() : 0);
        hash = 73 * hash + (this.validationExceptionThreshold != null
                ? this.validationExceptionThreshold.hashCode() : 0);
        hash = 73 * hash + this.skipLeading;
        hash = 73 * hash + this.skipTrailing;
        return hash;
    }
    
}
