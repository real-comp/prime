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
import com.realcomp.data.validation.Severity;
import com.realcomp.data.validation.ValidationException;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;

/**
 * This has not been fully tested and should be considered BETA
 * 
 * @author krenfro
 */
public class JsonFileReader implements RecordReader{
    
    private static final Logger logger = Logger.getLogger(JsonFileReader.class.getName());

    protected ObjectMapper jackson;
    protected JsonFactory jsonFactory;
    protected JsonParser jsonParser;
    
    @XStreamOmitField
    protected long count;
    
    protected boolean beforeFirstOperationsRun = false;      
    protected FileSchema schema;
    protected Severity validationExceptionThreshold = DEFAULT_VALIDATION_THREASHOLD;
    
    public JsonFileReader(){
        
  
        jackson = new ObjectMapper(); 
        //jackson.getSerializationConfig().appendAnnotationIntrospector(new JaxbAnnotationIntrospector());
        //jackson.getSerializationConfig().set(Feature.WRITE_NULL_MAP_VALUES, false); //Jackson 1.8.5
        jackson.getSerializationConfig().setAnnotationIntrospector(new JaxbAnnotationIntrospector());
        jackson.getSerializationConfig().setSerializationInclusion(Inclusion.NON_NULL); //Jackson 1.4
        jsonFactory = jackson.getJsonFactory();
    }
    
    
    @Override
    public Record read() throws IOException, ValidationException, ConversionException, SchemaException {
        
        if (!beforeFirstOperationsRun){
            executeBeforeFirstOperations();
            beforeFirstOperationsRun = true;
        }

        moveToNextObject();
        Map map = parseMap();
        
        if (map != null){
            count++;
        }
        else{
            executeAfterLastOperations();
        }
            
        return map == null ? null : new Record(map);
    }
    
    protected void moveToNextObject() throws IOException{
        JsonToken token = jsonParser.nextToken();
        if (token == JsonToken.START_ARRAY){
            //more than one record in the input stream
            moveToNextObject(); 
        }
        else if (token == JsonToken.START_OBJECT){
            //ready - probably only one json object in the input stream
        }
        else{
            //uh oh.
            logger.warning("Uh oh, unexpected token: " + jsonParser.getCurrentToken());
        }
    }
    
    protected Map parseMap() throws IOException{
        
        Map map = null;
        if (jsonParser.getCurrentToken() == JsonToken.START_OBJECT){
            
            map = new HashMap();
            while (jsonParser.nextToken() != JsonToken.END_OBJECT){

                JsonToken token = jsonParser.getCurrentToken();
                
                if (token == JsonToken.START_ARRAY){
                    //nested list
                    map.put(jsonParser.getCurrentName(), parseList());
                }
                else if (token == JsonToken.START_OBJECT){
                    //nested map
                    map.put(jsonParser.getCurrentName(), parseMap());
                }
                else if (token == JsonToken.VALUE_TRUE){
                    map.put(jsonParser.getCurrentName(), Boolean.TRUE);
                }
                else if (token == JsonToken.VALUE_FALSE){
                    map.put(jsonParser.getCurrentName(), Boolean.FALSE);
                }
                else if (token == JsonToken.VALUE_STRING){
                    //TODO: charset being used!
                    map.put(jsonParser.getCurrentName(), jsonParser.getText());
                }
                else if (token == JsonToken.VALUE_NUMBER_FLOAT){
                    //TODO: charset being used!
                    map.put(jsonParser.getCurrentName(), Float.valueOf(jsonParser.getFloatValue()));
                }
                else if (token == JsonToken.VALUE_NUMBER_INT){
                    //TODO: charset being used!
                    map.put(jsonParser.getCurrentName(), Integer.valueOf(jsonParser.getIntValue()));
                }
                else if (token == JsonToken.VALUE_NULL){
                    //skip
                }
            }
        }
        
        return map;        
    }
    
    protected List parseList() throws IOException{

        List list = null;
        
        if (jsonParser.getCurrentToken() == JsonToken.START_ARRAY){
            
            list = new ArrayList();
            while (jsonParser.nextToken() != JsonToken.END_ARRAY){
                
                JsonToken token = jsonParser.getCurrentToken();
                
                if (token == JsonToken.START_ARRAY){
                    //nested list
                    list.add(parseList());
                }
                else if (token == JsonToken.START_OBJECT){
                    //nested map
                    list.add(parseMap());
                }
                else if (token == JsonToken.VALUE_TRUE){
                    list.add(Boolean.TRUE);
                }
                else if (token == JsonToken.VALUE_FALSE){
                    list.add(Boolean.FALSE);
                }
                else if (token == JsonToken.VALUE_NULL){
                    //skip
                }
                else if (token == JsonToken.VALUE_STRING){
                    //TODO: charset being used!
                    list.add(jsonParser.getText());
                }
                else if (token == JsonToken.VALUE_NUMBER_FLOAT){
                    //TODO: charset being used!
                    list.add(Float.valueOf(jsonParser.getFloatValue()));
                }
                else if (token == JsonToken.VALUE_NUMBER_INT){
                    //TODO: charset being used!
                    list.add(Integer.valueOf(jsonParser.getIntValue()));
                }
            }
        }
        
        return list;
    }
    
    
    protected Record parse(String json) throws JsonParseException, JsonMappingException, IOException{
        Map m = jackson.readValue(json, Map.class);
        return new Record(m);
    }
    
    @Override
    public void open(InputStream in) throws IOException{
        
        close();
        beforeFirstOperationsRun = false;        
        count = 0;
        jsonParser = jsonFactory.createJsonParser(in);
    }

    @Override
    public void close(){        
        
        if (jsonParser != null){
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
    
    
    
    protected void executeAfterLastOperations() throws ValidationException, ConversionException{

        if (schema != null){
            List<Operation> operations = schema.getAfterLastOperations();
            if (operations != null && !operations.isEmpty()){
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

    protected void executeBeforeFirstOperations() throws ValidationException, ConversionException{

        if (schema != null){
            List<Operation> operations = schema.getBeforeFirstOperations();            
            if (operations != null && !operations.isEmpty()){
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
