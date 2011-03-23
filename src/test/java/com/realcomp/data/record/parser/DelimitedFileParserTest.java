package com.realcomp.data.record.parser;

import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.validation.ValidationException;
import com.realcomp.data.schema.FileSchema;
import com.realcomp.data.DataType;
import java.io.ByteArrayInputStream;
import com.realcomp.data.record.Record;
import com.realcomp.data.record.parser.DelimitedFileParser.Delimiter;
import com.realcomp.data.schema.SchemaField;
import java.io.InputStream;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krenfro
 */
public class DelimitedFileParserTest {

    public DelimitedFileParserTest() {
    }


    /**
     * Test of open method, of class DelimitedFileParser.
     */
    @Test
    public void testOpenClose() {

        DelimitedFileParser instance = new DelimitedFileParser();
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
        instance.close();
        instance.close();

    }


    /**
     * Test of getType method, of class DelimitedFileParser.
     */
    @Test
    public void testGetType() {

        
        DelimitedFileParser instance = new DelimitedFileParser();
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
    public void testNoTypeChangeAfterOpen(){

        DelimitedFileParser instance = new DelimitedFileParser();
        String data = "a\tb\tc";
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


        DelimitedFileParser instance = new DelimitedFileParser();
        String data = "a\tb\tc\nd\te\tf";
        instance.open(new ByteArrayInputStream(data.getBytes()));
        instance.setSchema(get3FieldSchema());

        Record record = instance.next();
        assertNotNull(record);
        record = instance.next();
        assertNotNull(record);
        record = instance.next();
        assertNull(record);
        
        instance.close();
    }


    @Test
    public void testCSV() throws Exception {

        DelimitedFileParser instance = new DelimitedFileParser();
        instance.setDelimiter(Delimiter.CSV);
        String data = "\"a123\",\"b123\",\"c123\"";
        instance.open(new ByteArrayInputStream(data.getBytes()));
        instance.setSchema(get3FieldSchema());

        Record record = instance.next();
        assertNotNull(record);
        assertEquals("a123", record.get("a").getValue());
        assertEquals("b123", record.get("b").getValue());
        assertEquals("c123", record.get("c").getValue());
        assertNull(instance.next());
        instance.close();

        //embedded comma
        data = "\"a123\",\"b1,23\",\"c123\"";
        instance.open(new ByteArrayInputStream(data.getBytes()));
        instance.setSchema(get3FieldSchema());

        record = instance.next();
        assertNotNull(record);
        assertEquals("a123", record.get("a").getValue());
        assertEquals("b1,23", record.get("b").getValue());
        assertEquals("c123", record.get("c").getValue());
        assertNull(instance.next());
        instance.close();


        //embedded comma
        data = "\"a123\",\"b1,23\",\"c123\"";
        instance.open(new ByteArrayInputStream(data.getBytes()));
        instance.setSchema(get3FieldSchema());

        record = instance.next();
        assertNotNull(record);
        assertEquals("a123", record.get("a").getValue());
        assertEquals("b1,23", record.get("b").getValue());
        assertEquals("c123", record.get("c").getValue());
        assertNull(instance.next());
        instance.close();

         //embedded quote
        data = "\"a123\",\"b1\"\"23\",\"c123\"";
        instance.open(new ByteArrayInputStream(data.getBytes()));
        instance.setSchema(get3FieldSchema());

        record = instance.next();
        assertNotNull(record);
        assertEquals("a123", record.get("a").getValue());
        assertEquals("b1\"23", record.get("b").getValue());
        assertEquals("c123", record.get("c").getValue());
        assertNull(instance.next());
        instance.close();

        //embedded quote at end
        data = "\"a123\",\"b123\"\"\",\"c123\"";
        instance.open(new ByteArrayInputStream(data.getBytes()));
        instance.setSchema(get3FieldSchema());

        record = instance.next();
        assertNotNull(record);
        assertEquals("a123", record.get("a").getValue());
        assertEquals("b123\"", record.get("b").getValue());
        assertEquals("c123", record.get("c").getValue());
        assertNull(instance.next());
        instance.close();

    }


    /**
     * Test of loadRecord method, of class DelimitedFileParser.
     */
    @Test
    public void testLoadRecord() throws Exception {
        
        List<SchemaField> fields = get3FieldSchema().getSchemaFields();
        
        String[] data = new String[]{"a123","b123","c123"};
        DelimitedFileParser instance = new DelimitedFileParser();
        instance.setSchema(get3FieldSchema());
        Record result = instance.loadRecord(fields, data);
        assertEquals(3, result.size());
    }

     /**
     * Test of loadRecord method, of class DelimitedFileParser.
     */
    @Test
    public void testLoadRecordMissingFields() throws Exception {

        List<SchemaField> fields = get3FieldSchema().getSchemaFields();

        String[] data = new String[]{"a123","b123"};
        DelimitedFileParser instance = new DelimitedFileParser();
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
        schema.addSchemaField(new SchemaField("a", DataType.STRING));
        schema.addSchemaField(new SchemaField("b", DataType.STRING));
        schema.addSchemaField(new SchemaField("c", DataType.STRING));
        return schema;
    }

}