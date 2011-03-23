package com.realcomp.data.record.parser;

import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.validation.ValidationException;
import com.realcomp.data.conversion.Trim;
import com.realcomp.data.DataType;
import com.realcomp.data.schema.FileSchema;
import java.io.ByteArrayInputStream;
import com.realcomp.data.record.Record;
import com.realcomp.data.schema.SchemaField;
import java.io.InputStream;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krenfro
 */
public class FixedFileParserTest {

    public FixedFileParserTest() {
    }

    /**
     * Test of open method, of class DelimitedFileParser.
     */
    @Test
    public void testOpenClose() {

        FixedFileParser instance = new FixedFileParser();
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


    @Test
    public void testNotAllLengthsSepcified() throws Exception{

        FixedFileParser instance = new FixedFileParser();
        String data = "a\tb\tc";
        instance.open(new ByteArrayInputStream(data.getBytes()));

        FileSchema schema = new FileSchema();
        schema.setName("test");
        schema.setVersion("0");
        schema.addSchemaField(new SchemaField("a", DataType.STRING, 1));
        schema.addSchemaField(new SchemaField("b", DataType.STRING, 2));
        schema.addSchemaField(new SchemaField("c", DataType.STRING)); //<-- missing length
        instance.setSchema(schema);
        try{
            Record record = instance.next();
            fail("should have thrown SchemaException");
        }
        catch(SchemaException expected){}
        
        instance.close();
    }



    /**
     * Test of next method, of class DelimitedFileParser.
     */
    @Test
    public void testNext() throws Exception {

        FixedFileParser instance = new FixedFileParser();
        String data = "abcdef\nghijkl";
        instance.open(new ByteArrayInputStream(data.getBytes()));
        instance.setSchema(get3FieldSchema());
        Record record = instance.next();
        assertNotNull(record);
        record = instance.next();
        assertNotNull(record);
        record = instance.next();
        assertNull(record);
        instance.close();

        instance.open(new ByteArrayInputStream(data.getBytes()));
        instance.setSchema(get3FieldSchema());
        record = instance.next();
        assertNotNull(record);
        assertEquals("a", record.get("a").getValue());
        assertEquals("bc", record.get("b").getValue());
        assertEquals("def", record.get("c").getValue());
        record = instance.next();
        assertNotNull(record);
        assertEquals("g", record.get("a").getValue());
        assertEquals("hi", record.get("b").getValue());
        assertEquals("jkl", record.get("c").getValue());        
        instance.close();

    }


    @Test
    public void testNumericSchema() throws Exception{

        FixedFileParser instance = new FixedFileParser();
        String data = "    1    2    3    4a";
        instance.open(new ByteArrayInputStream(data.getBytes()));
        instance.setSchema(getNumericSchema());
        Record record = instance.next();
        assertNotNull(record);
        assertEquals(1, record.get("int").getValue());
        assertEquals(2f, record.get("float").getValue());
        assertEquals(3l, record.get("long").getValue());
        assertEquals(4d, record.get("double").getValue());
        assertEquals("a", record.get("string").getValue());

        data = "  001  2.000003 04.4a";
        instance.open(new ByteArrayInputStream(data.getBytes()));
        record = instance.next();
        assertNotNull(record);
        assertEquals(1, record.get("int").getValue());
        assertEquals(2f, record.get("float").getValue());
        assertEquals(3l, record.get("long").getValue());
        assertEquals(4.4d, record.get("double").getValue());
        assertEquals("a", record.get("string").getValue());
        
        instance.close();
    }

    protected FileSchema get3FieldSchema() throws SchemaException{
        FileSchema schema = new FileSchema();
        schema.setName("test");
        schema.setVersion("0");
        schema.addSchemaField(new SchemaField("a", DataType.STRING, 1));
        schema.addSchemaField(new SchemaField("b", DataType.STRING, 2));
        schema.addSchemaField(new SchemaField("c", DataType.STRING, 3));

        return schema;
    }

    protected FileSchema getNumericSchema() throws SchemaException{
        FileSchema schema = new FileSchema();
        schema.setName("test");
        schema.setVersion("0");
        schema.addSchemaField(new SchemaField("int", DataType.INTEGER, 5));
        schema.addSchemaField(new SchemaField("float", DataType.FLOAT, 5));
        schema.addSchemaField(new SchemaField("long", DataType.LONG, 5));
        schema.addSchemaField(new SchemaField("double", DataType.DOUBLE, 5));
        schema.addSchemaField(new SchemaField("string", DataType.STRING, 1));

        schema.addAfterOperation(new Trim());
        return schema;
    }

    @Test
    public void testShortRecord() throws Exception{
    FixedFileParser instance = new FixedFileParser();
        String shortByOneCharacter = "    1    2    3    4";
        instance.open(new ByteArrayInputStream(shortByOneCharacter.getBytes()));
        instance.setSchema(getNumericSchema());

        try{
            Record record = instance.next();
            fail("should have thrown ValidationException");
        }
        catch(ValidationException expected){}
    }

    @Test
    public void testLongRecord() throws Exception{

        FixedFileParser instance = new FixedFileParser();
        String longByOneCharacter = "    1    2    3    4ab";
        instance.open(new ByteArrayInputStream(longByOneCharacter.getBytes()));
        instance.setSchema(getNumericSchema());
        try{
            Record record = instance.next();
            fail("should have thrown ValidationException");
        }
        catch(ValidationException expected){}
    }


    @Test
    public void testZeroLength() throws SchemaException{

        FileSchema schema = new FileSchema();
        schema.setName("test");
        schema.setVersion("0");
        
        try{
            schema.addSchemaField(new SchemaField("a", DataType.STRING, 0));
            fail("should have thrown IllegalArgumentException");
        }
        catch(IllegalArgumentException expected){}

        try{
            schema.addSchemaField(new SchemaField("a", DataType.STRING, -1));
            fail("should have thrown IllegalArgumentException");
        }
        catch(IllegalArgumentException expected){}

    }
}