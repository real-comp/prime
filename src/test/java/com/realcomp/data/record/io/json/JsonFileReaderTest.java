
package com.realcomp.data.record.io.json;

import java.util.List;
import java.util.Map;
import com.realcomp.data.record.Record;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krenfro
 */
public class JsonFileReaderTest {
    
    public JsonFileReaderTest() {
    }

    
    public String getJsonTestString1(){
        return "{\"attributes\":{\"valueDescription\":\"2011 Preliminary\",\"buildDate\":\"20111031\"},\"rawAddress\":{\"address\":[\"1028 HWY 3\"],\"state\":\"TX\",\"city\":\"LA MARQUE\",\"zip\":\"77568\",\"fips\":\"48267\"},\"exemptions\":[],\"owners\":[{\"name\":{\"first\":\"FELIPE\",\"last\":\"ATONAL\",\"salutation\":\"FELIPE\"},\"rawAddress\":{\"address\":[\"1028 HWY 3 LOT 17\"],\"state\":\"TX\",\"city\":\"LA MARQUE\",\"zip\":\"77568\"},\"percentOwnership\":100.0}],\"landSegments\":[],\"improvements\":[{\"description\":\"MOBILE HOME\",\"stories\":1.0,\"details\":[{\"description\":\"OUT BUILDINGS\"},{\"type\":\"MAIN_AREA\",\"description\":\"MAIN AREA\",\"sqft\":952.0}],\"sketchCommands\":\"NV!NV\"}],\"deedDate\":\"20051019\",\"agriculturalValue\":0,\"cadGeographicId\":\"101131\",\"cadPropertyId\":\"M279227\",\"landValue\":0,\"legalDescription\":\"SHADY OAKS MHPK-LM, SPACE 17, SERIAL # JE3107A, TITLE # 00146824, LABEL # TEX0122548, LIFESTYLE BAYSHORE 1980 14X68 CRM/TAN/BRN\",\"subdivision\":\"\",\"totalImprovementSqft\":952,\"totalLandAcres\":0.0,\"totalValue\":7810}";
    }
    
    @Test
    public void testReader() throws Exception {
        
        String json = getJsonTestString1();
        JsonFileReader reader = new JsonFileReader();
        Record r = reader.parse(json);
        assertEquals(7810, r.resolveFirst("totalValue"));
        
        Map<String,String> attributes = (Map) r.get("attributes");        
        assertEquals("2011 Preliminary", attributes.get("valueDescription"));
        
        Map<String,Object> rawAddress = (Map) r.get("rawAddress");
        assertEquals("1028 HWY 3", ((List) rawAddress.get("address")).get(0).toString());
        
    }
    
    
}
