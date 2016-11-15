package com.realcomp.prime.record.io.delimited;

import au.com.bytecode.opencsv.CSVParser;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class CSVParserTest{


    @Test
    public void testBadQuoting() throws IOException{
        String csv = "R000034990,9934859,3/12/2003 12:00:00 AM,7/2/1997 12:00:00 AM,42,1722,LINDSEY ALICE E  TRUST ESTATE \"\",";
        CSVParser parser = new CSVParser(',', '\"', '\\', false);
        String[] tokens = parser.parseLine(csv);
        assertEquals(8, tokens.length);
        assertEquals("LINDSEY ALICE E  TRUST ESTATE \"", tokens[6]);

    }
}
