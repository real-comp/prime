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
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;

/**
 * 
 * The JSON format is rich enough that a FileSchema is not required to parse a Record.
 * 
 * 
 * @author krenfro
 */
public class JsonFileReader implements RecordReader {

    private static final Logger logger = Logger.getLogger(JsonFileReader.class.getName());
    
    private ObjectMapper jackson;
    private JsonFactory jsonFactory;
    private JsonParser jsonParser;
    private long count;
    private boolean beforeFirstOperationsRun = false;
    private FileSchema schema;
    private Severity validationExceptionThreshold = DEFAULT_VALIDATION_THREASHOLD;
    private ValueSurgeon surgeon;
    private TransformContext context;

    public JsonFileReader() {


        jackson = new ObjectMapper();
        //jackson.getSerializationConfig().appendAnnotationIntrospector(new JaxbAnnotationIntrospector());
        //jackson.getSerializationConfig().set(Feature.WRITE_NULL_MAP_VALUES, false); //Jackson 1.8.5
        jackson.getSerializationConfig().setAnnotationIntrospector(new JaxbAnnotationIntrospector());
        jackson.getSerializationConfig().setSerializationInclusion(Inclusion.NON_NULL); //Jackson 1.4
        jsonFactory = jackson.getJsonFactory();

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
            record = new Record(map);            
            
            if (schema != null) {
                context.setRecord(record);
                
                for (Field field : schema.classify(record)) {
                    context.setKey(field.getName());
                    List value = surgeon.operate(getOperations(field), context);                    
                    if (!value.isEmpty())
                        record.put(field.getName(), field.getType().coerce(value.get(0))); //set final value            
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
                Logger.getLogger(JsonFileReader.class.getName()).log(Level.SEVERE, null, ex);
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

    private void executeAfterLastOperations() throws ValidationException, ConversionException {

        if (schema != null) {
            List<Operation> operations = schema.getAfterLastOperations();
            if (operations != null && !operations.isEmpty()) {
                Transformer transformer = new Transformer();
                List<Field> fields = new ArrayList<Field>();
                fields.add(new BeforeFirstField());
                transformer.setFields(fields);
                transformer.setAfter(operations);
                TransformContext context = new TransformContext();
                context.setValidationExceptionThreshold(validationExceptionThreshold);
                context.setRecordCount(this.getCount());
                context.setSchema(schema);
                transformer.transform(context);
            }
        }
    }

    private void executeBeforeFirstOperations() throws ValidationException, ConversionException {

        if (schema != null) {
            List<Operation> operations = schema.getBeforeFirstOperations();
            if (operations != null && !operations.isEmpty()) {
                List<Field> fields = new ArrayList<Field>();
                fields.add(new BeforeFirstField());
                Transformer transformer = new Transformer();
                transformer.setFields(fields);
                transformer.setBefore(operations);
                TransformContext context = new TransformContext();
                context.setValidationExceptionThreshold(validationExceptionThreshold);
                context.setRecordCount(this.getCount());
                context.setSchema(schema);
                transformer.transform(context);
            }
        }
    }
}
