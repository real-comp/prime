package com.realcomp.data.record.io.json;

import com.realcomp.data.Operation;
import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.record.Record;
import com.realcomp.data.record.io.RecordReader;
import com.realcomp.data.schema.BeforeFirstField;
import com.realcomp.data.schema.Field;
import com.realcomp.data.schema.FileSchema;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.transform.TransformContext;
import com.realcomp.data.transform.Transformer;
import com.realcomp.data.transform.ValueSurgeon;
import com.realcomp.data.validation.Severity;
import com.realcomp.data.validation.ValidationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
/**
 * 
 * The JSON format is rich enough that a FileSchema is <i>not</i> required to parse a Record.
 * If a schema <i>is</i> specified, only the fields specified in the schema will appear in the Record.
 * 
 * 
 * @author krenfro
 */
public class JsonReader implements RecordReader {
    
    protected JsonFactory jsonFactory;
    protected JsonParser jsonParser;
    protected long count;
    protected boolean beforeFirstOperationsRun = false;
    protected FileSchema schema;
    protected Severity validationExceptionThreshold = DEFAULT_VALIDATION_THREASHOLD;
    protected ValueSurgeon surgeon;
    protected TransformContext context;

    public JsonReader() {
        jsonFactory = new JsonFactory();
        surgeon = new ValueSurgeon();
        context = new TransformContext();
    }

    @Override
    public Record read() throws IOException, ValidationException, ConversionException, SchemaException {

        if (!beforeFirstOperationsRun) {
            executeBeforeFirstOperations();
            beforeFirstOperationsRun = true;
        }

        Record record = null;
        moveToNextObject();
        Map map = parseMap();

        if (map != null) {
            
            
            if (schema == null){
                record = new Record(map);
            }
            else{
                /* Since a schema is defined, only put the fields defined in the schema into the final record.
                 * The operations should be able to find values in the more complete Record parsed from 
                 * the raw json.  Create a temporary Record from the parsed json, and use that for the
                 * field creation.  
                 */
                record = new Record();
                Record temp = new Record(map);
                context.setRecord(temp);
                for (Field field : schema.classify(temp)) {
                    context.setKey(field.getName());
                    List value = surgeon.operate(getOperations(field), context);                    
                    if (!value.isEmpty()){
                        //Write the results of the operations to both the final Record, and the
                        // temporary Record for subsequent field creation.
                        record.put(field.getName(), field.getType().coerce(value.get(0)));
                        temp.put(field.getName(), field.getType().coerce(value.get(0)));
                    }
                }
            }
            count++;
        }
        else {
            executeAfterLastOperations();
        }

        return record;
    }

    private List<Operation> getOperations(Field field) {

        assert(field != null);
        assert(schema != null);

        List<Operation> operations = new ArrayList<Operation>();
        if (schema.getBeforeOperations() != null)
            operations.addAll(schema.getBeforeOperations());
        operations.addAll(field.getOperations());
        if (schema.getAfterOperations() != null)
            operations.addAll(schema.getAfterOperations());
        return operations;
    }

    private void moveToNextObject() throws IOException {
        JsonToken token = jsonParser.nextToken();
        if (token == JsonToken.START_ARRAY) {
            //more than one record in the input stream
            moveToNextObject();
        }
        else if (token == JsonToken.START_OBJECT) {
            //ready - probably only one json object in the input stream
        }
    }

    private Map parseMap() throws IOException {

        Map map = null;
        if (jsonParser.getCurrentToken() == JsonToken.START_OBJECT) {

            map = new HashMap();
            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {

                JsonToken token = jsonParser.getCurrentToken();

                if (token == JsonToken.START_ARRAY) {
                    //nested list
                    map.put(jsonParser.getCurrentName(), parseList());
                }
                else if (token == JsonToken.START_OBJECT) {
                    //nested map
                    map.put(jsonParser.getCurrentName(), parseMap());
                }
                else if (token == JsonToken.VALUE_TRUE) {
                    map.put(jsonParser.getCurrentName(), Boolean.TRUE);
                }
                else if (token == JsonToken.VALUE_FALSE) {
                    map.put(jsonParser.getCurrentName(), Boolean.FALSE);
                }
                else if (token == JsonToken.VALUE_STRING) {
                    //TODO: charset being used!
                    map.put(jsonParser.getCurrentName(), jsonParser.getText());
                }
                else if (token == JsonToken.VALUE_NUMBER_FLOAT) {
                    //TODO: charset being used!
                    map.put(jsonParser.getCurrentName(), Float.valueOf(jsonParser.getFloatValue()));
                }
                else if (token == JsonToken.VALUE_NUMBER_INT) {
                    //TODO: charset being used!
                    map.put(jsonParser.getCurrentName(), Integer.valueOf(jsonParser.getIntValue()));
                }
                else if (token == JsonToken.VALUE_NULL) {
                    //skip
                }
            }
        }

        return map;
    }

    private List parseList() throws IOException {

        List list = null;

        if (jsonParser.getCurrentToken() == JsonToken.START_ARRAY) {

            list = new ArrayList();
            while (jsonParser.nextToken() != JsonToken.END_ARRAY) {

                JsonToken token = jsonParser.getCurrentToken();

                if (token == JsonToken.START_ARRAY) {
                    //nested list
                    list.add(parseList());
                }
                else if (token == JsonToken.START_OBJECT) {
                    //nested map
                    list.add(parseMap());
                }
                else if (token == JsonToken.VALUE_TRUE) {
                    list.add(Boolean.TRUE);
                }
                else if (token == JsonToken.VALUE_FALSE) {
                    list.add(Boolean.FALSE);
                }
                else if (token == JsonToken.VALUE_NULL) {
                    //skip
                }
                else if (token == JsonToken.VALUE_STRING) {
                    //TODO: charset being used!
                    list.add(jsonParser.getText());
                }
                else if (token == JsonToken.VALUE_NUMBER_FLOAT) {
                    //TODO: charset being used!
                    list.add(Float.valueOf(jsonParser.getFloatValue()));
                }
                else if (token == JsonToken.VALUE_NUMBER_INT) {
                    //TODO: charset being used!
                    list.add(Integer.valueOf(jsonParser.getIntValue()));
                }
            }
        }

        return list;
    }

    @Override
    public void open(InputStream in) throws IOException {

        close();
        beforeFirstOperationsRun = false;
        count = 0;
        jsonParser = jsonFactory.createJsonParser(in);
    }

    @Override
    public void close() {

        if (jsonParser != null) {
            try {
                jsonParser.close();
            }
            catch (IOException ex) {
                Logger.getLogger(JsonReader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void setSchema(FileSchema schema) throws SchemaException {
        this.schema = schema;
        if (schema != null) {
            context.setSchema(schema);
        }
    }

    @Override
    public FileSchema getSchema() {
        return schema;
    }

    @Override
    public Severity getValidationExceptionThreshold() {
        return validationExceptionThreshold;
    }

    @Override
    public void setValidationExceptionThreshold(Severity severity) {
        validationExceptionThreshold = severity;
    }

    @Override
    public long getCount() {
        return count;
    }

    protected void executeAfterLastOperations() throws ValidationException, ConversionException {

        if (schema != null) {
            List<Operation> operations = schema.getAfterLastOperations();
            if (operations != null && !operations.isEmpty()) {
                Transformer transformer = new Transformer();
                List<Field> fields = new ArrayList<Field>();
                fields.add(new BeforeFirstField());
                transformer.setFields(fields);
                transformer.setAfter(operations);
                TransformContext ctx = new TransformContext();
                ctx.setValidationExceptionThreshold(validationExceptionThreshold);
                ctx.setRecordCount(this.getCount());
                ctx.setSchema(schema);
                transformer.transform(ctx);
            }
        }
    }

    protected void executeBeforeFirstOperations() throws ValidationException, ConversionException {

        if (schema != null) {
            List<Operation> operations = schema.getBeforeFirstOperations();
            if (operations != null && !operations.isEmpty()) {
                List<Field> fields = new ArrayList<Field>();
                fields.add(new BeforeFirstField());
                Transformer transformer = new Transformer();
                transformer.setFields(fields);
                transformer.setBefore(operations);
                TransformContext ctx = new TransformContext();
                ctx.setValidationExceptionThreshold(validationExceptionThreshold);
                ctx.setRecordCount(this.getCount());
                ctx.setSchema(schema);
                transformer.transform(ctx);
            }
        }
    }
}
