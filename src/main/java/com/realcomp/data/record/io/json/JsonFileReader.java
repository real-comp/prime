package com.realcomp.data.record.io.json;

import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.record.Record;
import com.realcomp.data.record.io.BaseRecordReader;
import com.realcomp.data.record.io.SkippingBufferedReader;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.validation.ValidationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig.Feature;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;

/**
 *
 * @author krenfro
 */
public class JsonFileReader extends BaseRecordReader{

    private ObjectMapper jackson;
    protected SkippingBufferedReader reader;
    
    public JsonFileReader(){
        jackson = new ObjectMapper(); 
        //jackson.getSerializationConfig().appendAnnotationIntrospector(new JaxbAnnotationIntrospector());
        //jackson.getSerializationConfig().set(Feature.WRITE_NULL_MAP_VALUES, false); //Jackson 1.8.5
        jackson.getSerializationConfig().setAnnotationIntrospector(new JaxbAnnotationIntrospector());
        jackson.getSerializationConfig().setSerializationInclusion(Inclusion.NON_NULL); //Jackson 1.4
    }
    
    
    @Override
    public Record read() throws IOException, ValidationException, ConversionException, SchemaException {
        
        if (!beforeFirstOperationsRun){
            executeBeforeFirstOperations();
            beforeFirstOperationsRun = true;
        }

        Record record = null;
        String json = reader.readLine();
        if (json != null){         
            try{
                record = parse(json);
            }
            catch(JsonParseException ex){
                throw new ConversionException(ex);
            }
            catch(JsonMappingException ex){
                throw new ConversionException(ex);
            }
        }

        if (record != null)
            count++;
        else
            executeAfterLastOperations();
        
        return record;
    }
    
    protected Record parse(String json) throws JsonParseException, JsonMappingException, IOException{
        Map m = jackson.readValue(json, Map.class);
        return new Record(m);
    }
    
    @Override
    public void open(InputStream in) throws IOException{
        
        close();
        super.open(in);
        
        Charset c = charset == null ? Charset.defaultCharset() : Charset.forName(charset);
        reader = new SkippingBufferedReader(new InputStreamReader(in, c));
        reader.setSkipLeading(skipLeading);
        reader.setSkipTrailing(skipTrailing);
        charset = c.name();
        count = 0;
    }

    

    @Override
    public void close(){        
        super.close();
        IOUtils.closeQuietly(reader);        
    }
    
    
}
