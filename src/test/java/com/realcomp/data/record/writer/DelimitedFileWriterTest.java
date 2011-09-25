package com.realcomp.data.record.writer;

import java.io.IOException;
import com.realcomp.data.record.io.Delimiter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import com.realcomp.data.record.reader.*;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.schema.FileSchema;
import com.realcomp.data.DataType;
import java.io.ByteArrayInputStream;
import com.realcomp.data.record.Record;
import com.realcomp.data.schema.SchemaField;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krenfro
 */
public class DelimitedFileWriterTest {

    public DelimitedFileWriterTest() {
    }


    /**
     * Test of open method, of class DelimitedFileParser.
     */
    @Test
    public void testOpenClose() throws IOException{

        DelimitedFileWriter instance = new DelimitedFileWriter();
        OutputStream out = null;
        try{
            instance.open(out);
            fail("should have thrown IllegalArgumentException");
        }
        catch(IllegalArgumentException expected){}

        instance.close();
        instance.close();
    }


    /**
     * Test of getType method, of class DelimitedFileParser.
     */
    @Test
    public void testGetType() {

        DelimitedFileWriter instance = new DelimitedFileWriter();
        assertEquals(Delimiter.TAB, instance.getDelimiter());
        instance.setDelimiter(Delimiter.CSV);
        assertEquals(Delimiter.CSV, instance.getDelimiter());
        instance.setDelimiter(Delimiter.TAB);
        assertEquals(Delimiter.TAB, instance.getDelimiter());

        try{
            instance.setDelimiter(null);
            fail("should have thrown IllegalArgumentException");
        }
        catch(IllegalArgumentException expected){}
    }
    
    @Test
    public void testTypes(){

        assertEquals(Delimiter.TAB, Delimiter.parse("tab"));
        assertEquals(Delimiter.TAB, Delimiter.parse("TAB"));
        assertEquals(Delimiter.TAB, Delimiter.parse("tabbed"));
        assertEquals(Delimiter.TAB, Delimiter.parse("TABBED"));
        assertEquals(Delimiter.CSV, Delimiter.parse("csv"));
        assertEquals(Delimiter.CSV, Delimiter.parse("CSV"));

        try{
            Delimiter.parse(null);
            fail("should have thrown IllegalArgumentException");
        }
        catch(IllegalArgumentException expected){}

        try{
            Delimiter.parse("asdf");
            fail("should have thrown IllegalArgumentException");
        }
        catch(IllegalArgumentException expected){}
    }


    @Test
    public void testNoTypeChangeAfterOpen() throws IOException{

        DelimitedFileWriter instance = new DelimitedFileWriter();
        String data = "a\tb\tc";
        instance.open(new ByteArrayOutputStream());
        try{
            instance.setDelimiter(Delimiter.CSV);
            fail("should have thrown IllegalStateException");
        }
        catch(IllegalStateException expected){}
        instance.close();
    }



    @Test
    public void testCSV() throws Exception {

        DelimitedFileWriter writer = new DelimitedFileWriter();
        writer.setSchema(get3FieldSchema());
        writer.setDelimiter(Delimiter.CSV);

        DelimitedFileReader reader = new DelimitedFileReader();
        reader.setSchema(get3FieldSchema());
        reader.setDelimiter(Delimiter.CSV);

        
        File temp = File.createTempFile("realcomp-data.", ".tmp");
        temp.deleteOnExit();
        writer.open(new FileOutputStream(temp));
        writer.setSchema(get3FieldSchema());
        
        String data = "\"a123\",\"b123\",\"c123\"";
        reader.open(new ByteArrayInputStream(data.getBytes()));

        Record a = reader.read();
        assertNotNull(a);
        assertEquals("a123", a.get("a"));
        assertEquals("b123", a.get("b"));
        assertEquals("c123", a.get("c"));

        writer.write(a);
        writer.write(a);
        writer.write(a);
        writer.close();
        reader.close();

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        reader.open(new FileInputStream(temp));
        
        Record b = reader.read();
        assertEquals(a, b);
        assertEquals(a, reader.read());
        assertEquals(a, reader.read());
        assertNull(reader.read());
        reader.close();
    }


    @Test
    public void testClassification() throws SchemaException{

        FileSchema schema = get3FieldSchema();
        Record good = new Record();
        good.put("a", "1");
        good.put("b", "2");
        good.put("c", "3");

        assertTrue(schema.getFields().equals(schema.classify(good)));
        
        Record bad = new Record();
        bad.put("foo","bar");

        try{
            schema.getFields().equals(schema.classify(bad));
            fail("should have thrown SchemaException");
        }
        catch(SchemaException ok){}
    }


    protected FileSchema get3FieldSchema() throws SchemaException{
        
        FileSchema schema = new FileSchema();
        schema.setName("test");
        schema.setVersion("0");
        schema.addField(new SchemaField("a", DataType.STRING));
        schema.addField(new SchemaField("b", DataType.STRING));
        schema.addField(new SchemaField("c", DataType.STRING));
        return schema;
    }

}