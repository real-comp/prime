package com.realcomp.data.record.io.delimited;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.schema.FileSchema;
import com.realcomp.data.DataType;
import java.io.ByteArrayInputStream;
import com.realcomp.data.record.Record;
import com.realcomp.data.schema.Field;
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
        assertEquals("TAB", instance.getDelimiter());
        instance.setDelimiter("CSV");
        assertEquals("CSV", instance.getDelimiter());
        instance.setDelimiter("TAB");
        assertEquals("TAB", instance.getDelimiter());
        instance.setDelimiter("-");
        assertEquals("-", instance.getDelimiter());

        try{
            instance.setDelimiter(null);
            fail("should have thrown IllegalArgumentException");
        }
        catch(IllegalArgumentException expected){}
        
        
        try{
            instance.setDelimiter("12");
            fail("should have thrown IllegalArgumentException");
        }
        catch(IllegalArgumentException expected){}
      
    }
    


    @Test
    public void testCSV() throws Exception {

        DelimitedFileWriter writer = new DelimitedFileWriter();
        writer.setSchema(get3FieldSchema());
        writer.setDelimiter("CSV");

        DelimitedFileReader reader = new DelimitedFileReader();
        reader.setSchema(get3FieldSchema());
        reader.setDelimiter("CSV");

        
        String data = "\"a123\",\"b123\",\"c123\"";
        ByteArrayInputStream in = new ByteArrayInputStream(data.getBytes());
        reader.open(in);
        Record a = reader.read();
        assertNotNull(a);
        assertEquals("a123", a.get("a"));
        assertEquals("b123", a.get("b"));
        assertEquals("c123", a.get("c"));
        
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        writer.open(out);
        writer.setSchema(get3FieldSchema());
        writer.write(a);
        writer.write(a);
        writer.write(a);
        
        writer.close();
        reader.close();
        
        in = new ByteArrayInputStream(out.toByteArray());
        reader.open(in);        
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

        assertTrue(schema.getDefaultFieldList().equals(schema.classify(good)));
        
        Record bad = new Record();
        bad.put("foo","bar");

        assertTrue(schema.getDefaultFieldList().equals(schema.classify(bad)));

    }


    protected FileSchema get3FieldSchema() throws SchemaException{
        
        FileSchema schema = new FileSchema();
        schema.setName("test");
        schema.setVersion("0");
        schema.addField(new Field("a", DataType.STRING));
        schema.addField(new Field("b", DataType.STRING));
        schema.addField(new Field("c", DataType.STRING));
        return schema;
    }

}