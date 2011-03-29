package com.realcomp.data.record.reader;

import com.realcomp.data.record.io.Delimiter;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.validation.ValidationException;
import com.realcomp.data.schema.FileSchema;
import com.realcomp.data.DataType;
import java.io.ByteArrayInputStream;
import com.realcomp.data.record.Record;
import com.realcomp.data.schema.SchemaField;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krenfro
 */
public class DelimitedFileReaderTest {

    public DelimitedFileReaderTest() {
    }


    /**
     * Test of open method, of class DelimitedFileParser.
     * @throws IOException
     * @throws SchemaException
     */
    @Test
    public void testOpenClose() throws IOException, SchemaException {

        DelimitedFileReader instance = new DelimitedFileReader();
        InputStream in = null;
        try{
            instance.open(in);
            fail("should have thrown IllegalArgumentException");
        }
        catch(IllegalArgumentException expected){}

        instance.close();
        instance.close();

        String data = "a\tb\tc";
        
        instance.open(new ByteArrayInputStream(data.getBytes()));
        instance.setSchema(get3FieldSchema());
        instance.close();
        instance.close();

    }


    /**
     * Test of getType method, of class DelimitedFileParser.
     */
    @Test
    public void testGetType() {

        
        DelimitedFileReader instance = new DelimitedFileReader();
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
    public void testNoTypeChangeAfterOpen() throws IOException, SchemaException{

        DelimitedFileReader instance = new DelimitedFileReader();
        String data = "a\tb\tc";
        instance.setSchema(get3FieldSchema());
        instance.open(new ByteArrayInputStream(data.getBytes()));
        try{
            instance.setDelimiter(Delimiter.CSV);
            fail("should have thrown IllegalStateException");
        }
        catch(IllegalStateException expected){}
        instance.close();
    }


    /**
     * Test of next method, of class DelimitedFileParser.
     */
    @Test
    public void testNext() throws Exception {


        DelimitedFileReader instance = new DelimitedFileReader();
        String data = "a\tb\tc\nd\te\tf";
        instance.setSchema(get3FieldSchema());
        instance.open(new ByteArrayInputStream(data.getBytes()));
        

        Record record = instance.read();
        assertNotNull(record);
        record = instance.read();
        assertNotNull(record);
        record = instance.read();
        assertNull(record);
        
        instance.close();
    }


    @Test
    public void testCSV() throws Exception {

        DelimitedFileReader instance = new DelimitedFileReader();
        instance.setDelimiter(Delimiter.CSV);
        String data = "\"a123\",\"b123\",\"c123\"";
        instance.setSchema(get3FieldSchema());
        instance.open(new ByteArrayInputStream(data.getBytes()));
        

        Record record = instance.read();
        assertNotNull(record);
        assertEquals("a123", record.get("a").getValue());
        assertEquals("b123", record.get("b").getValue());
        assertEquals("c123", record.get("c").getValue());
        assertNull(instance.read());
        instance.close();

        //embedded comma
        data = "\"a123\",\"b1,23\",\"c123\"";
        instance.open(new ByteArrayInputStream(data.getBytes()));
        instance.setSchema(get3FieldSchema());

        record = instance.read();
        assertNotNull(record);
        assertEquals("a123", record.get("a").getValue());
        assertEquals("b1,23", record.get("b").getValue());
        assertEquals("c123", record.get("c").getValue());
        assertNull(instance.read());
        instance.close();


        //embedded comma
        data = "\"a123\",\"b1,23\",\"c123\"";
        instance.setSchema(get3FieldSchema());
        instance.open(new ByteArrayInputStream(data.getBytes()));
        

        record = instance.read();
        assertNotNull(record);
        assertEquals("a123", record.get("a").getValue());
        assertEquals("b1,23", record.get("b").getValue());
        assertEquals("c123", record.get("c").getValue());
        assertNull(instance.read());
        instance.close();

         //embedded quote
        data = "\"a123\",\"b1\"\"23\",\"c123\"";
        instance.setSchema(get3FieldSchema());
        instance.open(new ByteArrayInputStream(data.getBytes()));
        

        record = instance.read();
        assertNotNull(record);
        assertEquals("a123", record.get("a").getValue());
        assertEquals("b1\"23", record.get("b").getValue());
        assertEquals("c123", record.get("c").getValue());
        assertNull(instance.read());
        instance.close();

        //embedded quote at end
        data = "\"a123\",\"b123\"\"\",\"c123\"";
        instance.setSchema(get3FieldSchema());
        instance.open(new ByteArrayInputStream(data.getBytes()));
        
        record = instance.read();
        assertNotNull(record);
        assertEquals("a123", record.get("a").getValue());
        assertEquals("b123\"", record.get("b").getValue());
        assertEquals("c123", record.get("c").getValue());
        assertNull(instance.read());
        instance.close();

    }



     /**
     * Test of loadRecord method, of class DelimitedFileParser.
     */
    @Test
    public void testLoadRecordMissingFields() throws Exception {

        List<SchemaField> fields = get3FieldSchema().getFields();

        String[] data = new String[]{"a123","b123"};
        DelimitedFileReader instance = new DelimitedFileReader();
        instance.setSchema(get3FieldSchema());
        try{
            instance.loadRecord(fields, data);
            fail("should have thrown ValidationException");
        }
        catch(ValidationException expected){}


        try{
            data = new String[]{"a123","b123","c123","d123"};
            instance.loadRecord(fields, data);
            fail("should have thrown ValidationException");
        }
        catch(ValidationException expected){}

        try{
            data = new String[]{};
            instance.loadRecord(fields, data);
            fail("should have thrown ValidationException");
        }
        catch(ValidationException expected){}

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