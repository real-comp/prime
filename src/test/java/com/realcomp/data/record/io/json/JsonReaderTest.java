package com.realcomp.data.record.io.json;

import com.realcomp.data.schema.FileSchema;
import com.realcomp.data.record.Record;
import com.realcomp.data.schema.SchemaFactory;
import java.io.ByteArrayInputStream;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krenfro
 */
public class JsonReaderTest {
    
    public JsonReaderTest() {
    }

    
    public String getJsonTestString1(){
        return "{\"attributes\":{\"valueDescription\":\"2011 Preliminary\",\"buildDate\":\"20111031\"},\"rawAddress\":{\"address\":[\"1028 HWY 3\"],\"state\":\"TX\",\"city\":\"LA MARQUE\",\"zip\":\"77568\",\"fips\":\"48267\"},\"exemptions\":[],\"owners\":[{\"name\":{\"first\":\"FELIPE\",\"last\":\"ATONAL\",\"salutation\":\"FELIPE\"},\"rawAddress\":{\"address\":[\"1028 HWY 3 LOT 17\"],\"state\":\"TX\",\"city\":\"LA MARQUE\",\"zip\":\"77568\"},\"percentOwnership\":100.0}],\"landSegments\":[],\"improvements\":[{\"description\":\"MOBILE HOME\",\"stories\":1.0,\"details\":[{\"description\":\"OUT BUILDINGS\"},{\"type\":\"MAIN_AREA\",\"description\":\"MAIN AREA\",\"sqft\":952.0}],\"sketchCommands\":\"NV!NV\"}],\"deedDate\":\"20051019\",\"agriculturalValue\":0,\"cadGeographicId\":\"101131\",\"cadPropertyId\":\"M279227\",\"landValue\":0,\"legalDescription\":\"SHADY OAKS MHPK-LM, SPACE 17, SERIAL # JE3107A, TITLE # 00146824, LABEL # TEX0122548, LIFESTYLE BAYSHORE 1980 14X68 CRM/TAN/BRN\",\"subdivision\":\"\",\"totalImprovementSqft\":952,\"totalLandAcres\":0.0,\"totalValue\":7810}";
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
        
    
    @Test
    public void testReadFromString() throws Exception{
        
        JsonReader reader = new JsonReader();
        ByteArrayInputStream in = new ByteArrayInputStream(getJsonTestString1().getBytes());
        reader.open(in);
        Record record = reader.read();
        assertNotNull(record);
        assertEquals(1, reader.getCount());
        assertEquals(7810, record.resolveFirst("totalValue"));
        reader.close();
    }
    
        
    @Test
    public void testReadFromFile() throws Exception{
        
        JsonReader reader = new JsonReader();
        reader.open(this.getClass().getResourceAsStream("sample.json"));
        Record record = reader.read();
        assertNotNull(record);
        assertEquals(1, reader.getCount());
        assertEquals("relevate", record.resolveFirst("source"));
        
        assertNull(reader.read());
        assertNull(reader.read());
        reader.close();
        
        
        Record sample = getSampleRecord();
        assertEquals(record, sample);
        
        
        reader = new JsonReader();
        reader.open(this.getClass().getResourceAsStream("multiRecordSample.json"));
        record = reader.read();
        assertNotNull(record);
        assertEquals(1, reader.getCount());
        assertEquals("8665 EPHRAIM RD", record.resolveFirst("address"));
        
        record = reader.read();
        assertNotNull(record);
        assertEquals(2, reader.getCount());
        assertEquals("8666 EPHRAIM RD", record.resolveFirst("address"));
        
        
        assertNull(reader.read());
        reader.close();
    }
    
    
        
    @Test
    public void testWithSchema() throws Exception{
        
        JsonReader reader = new JsonReader();
        reader.open(this.getClass().getResourceAsStream("sample.json"));
        FileSchema schema = SchemaFactory.buildFileSchema(this.getClass().getResourceAsStream("sample.schema"));
        reader.setSchema(schema);
        
        Record record = reader.read();
        assertNotNull(record);
        assertEquals(1, reader.getCount());
        assertEquals("RELEVATE", record.resolveFirst("source")); //upper-case converter
        
        assertEquals(null, record.resolveFirst("asdf"));
        assertEquals("78717", record.resolveFirst("doesnotexistinjson"));
        
        assertNull(reader.read());
        assertNull(reader.read());
        reader.close();
    
    }
    
    
    
}