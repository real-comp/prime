package com.realcomp.prime;

import com.realcomp.prime.conversion.ConversionException;
import org.junit.Test;

import java.util.regex.Matcher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author krenfro
 */
public class DataSetIDTest {

    public DataSetIDTest(){
    }

    @Test
    public void testPatterns() throws ConversionException{

        //prefix + type + geography + source + version + name;
        Matcher matcher = DataSetID.PREFIXED_ID_PATTERN.matcher("/asdf/123/type/geography/source/20140201/name.csv");

        assertTrue(matcher.matches());
        assertEquals(6, matcher.groupCount());

        DataSetID id = DataSetID.parse("/asdf/123/type/geography/source/20140201/name.csv");
        assertEquals("name.csv", id.getName());
        assertEquals("geography", id.getGeography());
        assertEquals("source", id.getSource());
        assertEquals("20140201", id.getVersion());
        assertEquals("type", id.getType());

        id = DataSetID.parse("/asdf/123/type/geography/source/20140201/name.csv.gz");
        assertEquals("name.csv.gz", id.getName());
        assertEquals("geography", id.getGeography());
        assertEquals("source", id.getSource());
        assertEquals("20140201", id.getVersion());
        assertEquals("type", id.getType());

        id = DataSetID.parse("s3://rc-prime-raw/dl/tx/txdps/20160103/PTXDPS_WEEKLYUPDATE_01032016_1.txt.gz");
        assertEquals("20160103", id.getVersion());
        assertEquals("PTXDPS_WEEKLYUPDATE_01032016_1.txt.gz", id.getName());
    }
}
