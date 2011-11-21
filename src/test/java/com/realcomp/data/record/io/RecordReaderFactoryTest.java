/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcomp.data.record.io;

import com.realcomp.data.record.io.delimited.DelimitedFileReader;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krenfro
 */
public class RecordReaderFactoryTest {
    
    public RecordReaderFactoryTest() {
    }

    @Test
    public void readerTest() throws FormatException{
        
        Format format = new Format("TAB");
        format.setAttribute("header", "true");
        format.setAttribute("doesnotexist", "blabla");
        
        
        RecordReader reader = RecordReaderFactory.build(format);
        assertTrue(reader.getClass() == DelimitedFileReader.class);
        assertTrue(((DelimitedFileReader) reader).getSkipLeading() == 1);
        assertTrue(((DelimitedFileReader) reader).isHeader());
    }
    
}
