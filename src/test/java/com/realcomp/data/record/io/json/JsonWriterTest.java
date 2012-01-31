package com.realcomp.data.record.io.json;

import com.realcomp.data.schema.SchemaFactory;
import com.realcomp.data.schema.FileSchema;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.ByteArrayOutputStream;
import com.realcomp.data.record.Record;
import java.io.ByteArrayInputStream;
import java.util.regex.Pattern;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krenfro
 */
public class JsonWriterTest {
    
    public JsonWriterTest() {
    }


    private Record getSampleRecord(){
        
        Record record = new Record();
        record.put("zip", "78717");
        record.put("address", "8665 EPHRAIM RD");
        record.put("userId", "73783");
        record.put("orderId", "299");
        record.put("product", "ALLSTATE AUTO SPECIFIC");
        record.put("source", "relevate");
        record.put("usedDate", "2011-09-21");
        return record;
    }
    
    private Record getComplexRecord(){
        Record record = new Record();
        record.put("s", "test string");
        record.put("i", 1);
        record.put("f", 1.2f);
        record.put("d", 1.3434d);
        record.put("l", 7474l);
        record.put("b", true);
        
        ArrayList list = new ArrayList();
        list.add("a");
        list.add("b");
        list.add("c");
        list.add(1);
        list.add(2);
        list.add(3);
        record.put("list", list);
        
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("entry", "a");
        map.put("entryNumber", 1);
        record.put("map", map);
        
        return record;
    }
        
    @Test
    public void testWrite() throws Exception{
        
        //write the Record to json string.
        JsonWriter writer = new JsonWriter();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        writer.open(out);
        writer.write(getSampleRecord());
        writer.close();        
        String json = new String(out.toByteArray());
        
        //read the json string back into a Record
        JsonReader reader = new JsonReader();
        reader.open(new ByteArrayInputStream(json.getBytes()));
        Record record = reader.read();
        reader.close();
        
        //make sure the read record matches the original Record
        assertEquals(record, getSampleRecord());
    }
    
    
    
    @Test
    public void testWriteTypes() throws Exception{
        
        JsonWriter writer = new JsonWriter();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        writer.open(out);
        writer.write(getComplexRecord());
        writer.close();        
        String json = new String(out.toByteArray());
        
        //should be close to:
        //{"f":1.2,"d":1.3434,"b":true,"s":"test string","list":["a","b","c",1,2,3],"l":7474,"i":1, "map":{"entryNumber":1,"entry":"a"}}
        //{"l":7474,"f":1.2,"map":{"entry":"a","entryNumber":1},"d":1.3434,"list":["a","b","c",1,2,3],"s":"test string","i":1,"b":true}

        assertTrue(Pattern.compile("\"f\"[ ]*:[ ]*[0-9\\.]+").matcher(json).find());
        assertTrue(Pattern.compile("\"d\"[ ]*:[ ]*[0-9\\.]+").matcher(json).find());
        assertTrue(Pattern.compile("\"b\"[ ]*:[ ]*true").matcher(json).find());
        assertTrue(Pattern.compile("\"l\"[ ]*:[ ]*[0-9\\.]+").matcher(json).find());
        assertTrue(Pattern.compile("\"i\"[ ]*:[ ]*[0-9\\.]+").matcher(json).find());
        assertTrue(Pattern.compile("\"s\"[ ]*:[ ]*\"test string\"").matcher(json).find());
        assertTrue(Pattern.compile("\"list\"[ ]*:[ ]*\\[[0-9a-zA-Z,\"\\. ]+\\]").matcher(json).find());
        assertTrue(Pattern.compile("\"map\"[ ]*:[ ]*\\{[0-9a-zA-Z,\"\\.: ]+\\}").matcher(json).find());
        
    }
    
    
    
    @Test
    public void testPrettyPrint() throws Exception{
        
        
        JsonWriter writer = new JsonWriter();
        writer.setPretty(true);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        writer.open(out);
        writer.write(getComplexRecord());
        writer.close();        
        String json = new String(out.toByteArray());
        
        assertTrue(Pattern.compile("\"f\"[ ]*:[ ]*[0-9\\.]+").matcher(json).find());
        assertTrue(Pattern.compile("\"d\"[ ]*:[ ]*[0-9\\.]+").matcher(json).find());
        assertTrue(Pattern.compile("\"b\"[ ]*:[ ]*true").matcher(json).find());
        assertTrue(Pattern.compile("\"l\"[ ]*:[ ]*[0-9\\.]+").matcher(json).find());
        assertTrue(Pattern.compile("\"i\"[ ]*:[ ]*[0-9\\.]+").matcher(json).find());
        assertTrue(Pattern.compile("\"s\"[ ]*:[ ]*\"test string\"").matcher(json).find());
        assertTrue(Pattern.compile("\"list\"[ ]*:[ ]*\\[[0-9a-zA-Z,\"\\. ]+\\]").matcher(json).find());
        
        //not matching very much of the map here...
        assertTrue(Pattern.compile("\"map\"[ ]*:[ ]*\\{").matcher(json).find());
    }
    
    
        
    @Test
    public void testWriteWithSchema() throws Exception{
        
         
        JsonReader reader = new JsonReader();
        reader.open(this.getClass().getResourceAsStream("sample.json"));
        FileSchema schema = SchemaFactory.buildFileSchema(this.getClass().getResourceAsStream("sample.schema"));
        reader.setSchema(schema);
        
        Record record = reader.read();
        
        //add field that is not in schema
        record.put("skip", "not in schema");
        
        //change a field so the upper-case operation is run.
        record.put("source", "relevate");//lower-case
        
        //write the Record to json string.
        JsonWriter writer = new JsonWriter();
        writer.setSchema(schema);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        writer.open(out);
        writer.write(record);
        writer.close();        
        String json = new String(out.toByteArray());
        
        System.out.println(json);
        
        //note upper case operation ran
        assertTrue(Pattern.compile("\"source\"[ ]*:[ ]*\"RELEVATE\"").matcher(json).find());
        
        //the skip field should have been removed
        assertFalse(Pattern.compile("\"skip\"").matcher(json).find());
        
    }
    
    
}
