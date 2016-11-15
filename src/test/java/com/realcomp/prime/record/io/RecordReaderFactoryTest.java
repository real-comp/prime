/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcomp.prime.record.io;

import com.realcomp.prime.record.io.delimited.DelimitedFileReader;
import com.realcomp.prime.schema.SchemaException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 */
public class RecordReaderFactoryTest{

    public RecordReaderFactoryTest(){
    }

    @Test
    public void readerTest() throws FormatException, IOException, SchemaException{

        Map<String, String> format = new HashMap<String, String>();
        format.put("type", "TAB");
        format.put("header", "true");
        format.put("doesnotexist", "blabla");

        RecordReader reader = RecordReaderFactory.build(format);

        IOContext ctx = new IOContextBuilder()
                .attributes(format)
                .in(new ByteArrayInputStream(new byte[10]))
                .build();
        reader.open(ctx);

        assertTrue(reader.getClass() == DelimitedFileReader.class);
        assertTrue('\t' == ((DelimitedFileReader) reader).getDelimiter());
        assertTrue(((DelimitedFileReader) reader).isHeader());
    }
}
