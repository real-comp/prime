package com.realcomp.data.record.io.json;

import com.realcomp.data.DataType;
import com.realcomp.data.Operation;
import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.record.Record;
import com.realcomp.data.record.io.RecordWriter;
import com.realcomp.data.schema.Field;
import com.realcomp.data.schema.FieldList;
import com.realcomp.data.schema.FileSchema;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.transform.TransformContext;
import com.realcomp.data.transform.Transformer;
import com.realcomp.data.transform.ValueSurgeon;
import com.realcomp.data.validation.Severity;
import com.realcomp.data.validation.ValidationException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.impl.DefaultPrettyPrinter;

/**
 *
 * @author krenfro
 */
public class JsonWriter implements RecordWriter{

    private static final Logger logger = Logger.getLogger(JsonWriter.class.getName());
    
    protected JsonFactory jsonFactory;
    protected JsonGenerator json;
    protected OutputStream out;
    protected FileSchema schema;
    protected long count;
    protected boolean beforeFirstOperationsRun = false;   
    protected Transformer transformer;
    protected TransformContext context;
    protected ValueSurgeon surgeon;    
    protected boolean pretty = false;
    
    public JsonWriter(){
    
        jsonFactory = new JsonFactory();
        transformer = new Transformer();
        context = new TransformContext();
        surgeon = new ValueSurgeon();
    }
    
    @Override
    public void write(Record record) throws IOException, ValidationException, ConversionException, SchemaException {
        
        if (record == null)
            throw new IllegalArgumentException("record is null");

        if (!beforeFirstOperationsRun){
            executeBeforeFirstOperations();
            beforeFirstOperationsRun = true;
        }

        if (!pretty && count > 0){
            //add newline to output
            json.writeRaw("\n");
        }
        
        if (schema != null){
            //modify the record, performing all operations and keeping only the fields defined in the schema
            FieldList fields = schema.classify(record);
            transform(record, fields);
            filterFields(record, fields);
        }
        
        writeJson(record);
        
        count++;
        
    }
    
    protected void transform(Record record, FieldList fields) 
            throws ConversionException, ValidationException, SchemaException{
        
        assert(record != null);
        assert(schema != null);
        
        transformer.setBefore(schema.getBeforeOperations());
        transformer.setAfter(schema.getAfterOperations());
        transformer.setFields(fields);
        context.setRecord(record);
        transformer.transform(context);
    }
    
    protected void filterFields(Record record, FieldList fields){
        
        Set<String> filter = new HashSet<String>();
        Set<String> keep = new HashSet<String>();
        for (Field field: fields)
            keep.add(field.getName());
        
        filter.addAll(record.keySet());
        filter.removeAll(keep);
        
        for (String f: filter){
            record.remove(f);
        }
    }

    
    private void writeJson(Record record)
            throws ValidationException, ConversionException, IOException{

        json.writeStartObject();
        
        for (Map.Entry<String,Object> entry: record.entrySet()){
            writeJson(entry.getKey(), entry.getValue());
        }
        
        json.writeEndObject();
    }
    
    private void writeJson(String name, Object value) 
            throws IOException, ValidationException, ConversionException{
        
        if (value == null){
            json.writeFieldName(name);
            json.writeNull();
        }
        else{
            DataType type = DataType.getDataType(value);

            switch(type){
                case MAP:
                    json.writeFieldName(name);
                    writeJson(new Record((Map) value));
                    break;
                case LIST:
                    json.writeArrayFieldStart(name);
                    writeJson(value, type);
                    json.writeEndArray();
                    break;
                default:
                    json.writeFieldName(name);
                    writeJson(value, type);
            }
        }
    }

    
    private void writeJson(Object value, DataType type) 
            throws IOException, ValidationException, ConversionException{
        
        assert(value != null);
        assert(type != null);
        
        switch(type){
            case STRING:
                json.writeString((String) value);
                break;
            case INTEGER:
                json.writeNumber((Integer) value);
                break;
            case LONG:
                json.writeNumber((Long) value);
                break;
            case FLOAT:
                json.writeNumber((Float) value);
                break;
            case DOUBLE:
                json.writeNumber((Double) value);
                break;
            case BOOLEAN:
                json.writeBoolean((Boolean) value);
                break;
            case MAP:
                writeJson(new Record((Map) value));
                break;
            case LIST:
                for (Object entry: (List) value)
                    writeJson(entry, DataType.getDataType(entry));
                break;
        }

    }
    
    
    
    @Override
    public void close() {
        try {
            executeAfterLastOperations();
        }
        catch (ValidationException ex) {
            logger.log(Level.WARNING, null, ex);
        }
        catch (ConversionException ex) {
            logger.log(Level.WARNING, null, ex);
        }
        
        if (json != null){
            try {
                json.close();
            }
            catch (IOException ex) {
                logger.log(Level.WARNING, null, ex);
            }
        }
        
        IOUtils.closeQuietly(out);
    }

    @Override
    public void open(OutputStream out) throws IOException {
        if (out == null)
            throw new IllegalArgumentException("out is null");
        
        close();
        this.out = out;
        beforeFirstOperationsRun = true;
        count = 0;   
        json = jsonFactory.createJsonGenerator(out, JsonEncoding.UTF8);
        if (pretty){
            json.setPrettyPrinter(new DefaultPrettyPrinter());
        }
    }

    @Override
    public void setSchema(FileSchema schema) throws SchemaException {
        if (schema == null)
            throw new IllegalArgumentException("schema is null");
        this.schema = schema;
        context.setSchema(schema);        
    }

    @Override
    public FileSchema getSchema() {
        return schema;
    }

    @Override
    public Severity getValidationExceptionThreshold() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setValidationExceptionThreshold(Severity severity) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long getCount() {
        return count;
    }
    
    protected void executeAfterLastOperations() throws ValidationException, ConversionException{
        
        if (schema != null){
            List<Operation> operations = schema.getAfterLastOperations();
            if (operations != null && !operations.isEmpty()){
                context.setRecordCount(this.getCount());
                surgeon.operate(operations, context);
            }
        }

            
    }

    protected void executeBeforeFirstOperations() throws ValidationException, ConversionException{

         if (schema != null){
            List<Operation> operations = schema.getBeforeFirstOperations();            
            if (operations != null && !operations.isEmpty()){
                context.setRecordCount(this.getCount());
                surgeon.operate(operations, context);
            }
        }
    }

    public boolean isPretty() {
        return pretty;
    }

    public void setPretty(boolean pretty) {
        this.pretty = pretty;
    }
    
    
}
