package com.realcomp.prime;

import com.realcomp.prime.conversion.ConversionException;
import org.junit.Test;

import java.util.regex.Matcher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
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

        id = DataSetID.parse("/asdf/123/type/01/001/source/2014/02/01/name.csv.gz");
        assertEquals("type", id.getType());
        assertEquals("01001", id.getGeography());
        assertEquals("source", id.getSource());
        assertEquals("20140201", id.getVersion());
        assertEquals("name.csv.gz", id.getName());

        id = DataSetID.parse("s3://rc-prime-raw/dl/01/001/txdps/2016/01/03/PTXDPS_WEEKLYUPDATE_01032016_1.txt.gz");
        assertEquals("s3://rc-prime-raw", id.getPrefix());
        assertEquals("dl", id.getType());
        assertEquals("01001", id.getGeography());
        assertEquals("txdps", id.getSource());
        assertEquals("20160103", id.getVersion());
        assertEquals("PTXDPS_WEEKLYUPDATE_01032016_1.txt.gz", id.getName());
    }
}
