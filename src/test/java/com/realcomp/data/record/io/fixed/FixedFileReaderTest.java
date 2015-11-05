package com.realcomp.data.record.io.fixed;

import com.realcomp.data.schema.FieldList;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.validation.ValidationException;
import com.realcomp.data.conversion.Trim;
import com.realcomp.data.DataType;
import com.realcomp.data.conversion.UpperCase;
import com.realcomp.data.schema.Schema;
import java.io.ByteArrayInputStream;
import com.realcomp.data.record.Record;
import com.realcomp.data.record.io.IOContext;
import com.realcomp.data.record.io.IOContextBuilder;
import com.realcomp.data.schema.Field;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krenfro
 */
public class FixedFileReaderTest{

    public FixedFileReaderTest(){
    }

    /**
     * Test of open method, of class DelimitedFileParser.
     */
    @Test
    public void testOpenClose() throws IOException, SchemaException{

        FixedFileReader reader = new FixedFileReader();
        try{
            reader.open(null);
            fail("should have thrown IllegalArgumentException");
        }
        catch (IllegalArgumentException expected){
        }

        reader.close();
        reader.close();

        Schema schema = new Schema();
        schema.setName("test");
        schema.setVersion("0");

        FieldList fields = new FieldList();
        fields.add(new Field("a", DataType.STRING, 1));
        fields.add(new Field("b", DataType.STRING, 1));
        fields.add(new Field("c", DataType.STRING, 1));
        schema.addFieldList(fields);

        String data = "abc";
        IOContext ctx = new IOContextBuilder().schema(schema).in(new ByteArrayInputStream(data.getBytes())).build();
        reader.open(ctx);
        reader.close();
        reader.close();

    }

    @Test
    public void testNotAllLengthsSepcified() throws Exception{

        FixedFileReader instance = new FixedFileReader();

        String data = "a\tb\tc";


        Schema schema = new Schema();
        schema.setName("test");
        schema.setVersion("0");
        FieldList fields = new FieldList();
        fields.add(new Field("a", DataType.STRING, 1));
        fields.add(new Field("b", DataType.STRING, 2));
        fields.add(new Field("c", DataType.STRING)); //<-- missing length
        schema.addFieldList(fields);

        try{
            IOContext ctx = new IOContextBuilder().schema(schema).in(new ByteArrayInputStream(data.getBytes())).build();
            instance.open(ctx);
            fail("should have thrown SchemaException");
        }
        catch (SchemaException expected){
        }

        instance.close();
    }

    /**
     * Test of next method, of class DelimitedFileParser.
     */
    @Test
    public void testNext() throws Exception{

        String data = "abcdef\nghijkl";
        IOContext ctx = new IOContextBuilder()
                .schema(get3FieldSchema())
                .in(new ByteArrayInputStream(data.getBytes()))
                .build();

        FixedFileReader reader = new FixedFileReader();
        reader.open(ctx);

        Record record = reader.read();
        assertNotNull(record);
        record = reader.read();
        assertNotNull(record);
        record = reader.read();
        assertNull(record);
        reader.close();

        ctx = new IOContextBuilder(ctx).in(new ByteArrayInputStream(data.getBytes())).build();
        reader.open(ctx);

        record = reader.read();
        assertNotNull(record);
        assertEquals("a", record.get("a"));
        assertEquals("bc", record.get("b"));
        assertEquals("def", record.get("c"));
        record = reader.read();
        assertNotNull(record);
        assertEquals("g", record.get("a"));
        assertEquals("hi", record.get("b"));
        assertEquals("jkl", record.get("c"));
        reader.close();

    }

    @Test
    public void testLengthSpecified() throws Exception{

        String data = "1234567890";
        IOContext ctx = new IOContextBuilder()
                .schema(getLengthSpecifiedSchema())
                .in(new ByteArrayInputStream(data.getBytes()))
                .build();

        FixedFileReader reader = new FixedFileReader();
        reader.open(ctx);

        Record record = reader.read();
        assertNotNull(record);
        assertEquals("12345", record.get("a"));

        record = reader.read();
        assertNotNull(record);
        assertEquals("67890", record.get("a"));

        record = reader.read();
        assertNull(record);
        reader.close();
    }

    @Test
    public void testNumericSchema() throws Exception{

        String data = "    1    2    3    4a";
        IOContext ctx = new IOContextBuilder()
                .schema(getNumericSchema())
                .in(new ByteArrayInputStream(data.getBytes()))
                .build();

        FixedFileReader reader = new FixedFileReader();
        reader.open(ctx);

        Record record = reader.read();
        assertNotNull(record);
        assertEquals(1, (long) record.getInteger("int"));
        assertEquals(2f, record.getFloat("float"), .001f);
        assertEquals(3l, (long) record.getLong("long"));
        assertEquals(4d, record.getDouble("double"), .001f);
        assertEquals("a", record.get("string"));

        data = "  001  2.000003 04.4a";
        ctx = new IOContextBuilder(ctx).in(new ByteArrayInputStream(data.getBytes())).build();
        reader.open(ctx);
        record = reader.read();
        assertNotNull(record);
        assertEquals(1, record.get("int"));
        assertEquals(2f, record.get("float"));
        assertEquals(3l, record.get("long"));
        assertEquals(4.4d, record.get("double"));
        assertEquals("a", record.get("string"));

        reader.close();
    }

    
    
    @Test
    public void testNumericFieldWithOperation() throws Exception{

        Schema schema = new Schema();
        schema.setName("test");
        schema.setVersion("0");
        Field field = new Field("long", DataType.LONG, 5);
        field.addOperation(new Trim());
        field.addOperation(new UpperCase());
        FieldList fields = new FieldList();
        fields.add(field);
        schema.addFieldList(fields);
        String data = "    1";
        IOContext ctx = new IOContextBuilder()
                .schema(schema)
                .in(new ByteArrayInputStream(data.getBytes()))
                .build();

        FixedFileReader reader = new FixedFileReader();
        reader.open(ctx);

        Record record = reader.read();
        assertNotNull(record);
        Object value = record.getLong("long");
        assertTrue(value instanceof Long);
        assertEquals(1l, (long) value);
        
        reader.close();
    }
    
    
    protected Schema get3FieldSchema() throws SchemaException{
        Schema schema = new Schema();
        schema.setName("test");
        schema.setVersion("0");
        FieldList fields = new FieldList();
        fields.add(new Field("a", DataType.STRING, 1));
        fields.add(new Field("b", DataType.STRING, 2));
        fields.add(new Field("c", DataType.STRING, 3));
        schema.addFieldList(fields);
        return schema;
    }

    protected Schema getLengthSpecifiedSchema() throws SchemaException{
        Schema schema = new Schema();
        HashMap<String, String> format = new HashMap<>();
        format.put("length", "5");
        schema.setFormat(format);
        schema.setName("test");
        schema.setVersion("0");
        FieldList fields = new FieldList();
        fields.add(new Field("a", DataType.STRING, 5));
        schema.addFieldList(fields);
        return schema;
    }

    protected Schema getNumericSchema() throws SchemaException{
        Schema schema = new Schema();
        schema.setName("test");
        schema.setVersion("0");
        FieldList fields = new FieldList();
        fields.add(new Field("int", DataType.INTEGER, 5));
        fields.add(new Field("float", DataType.FLOAT, 5));
        fields.add(new Field("long", DataType.LONG, 5));
        fields.add(new Field("double", DataType.DOUBLE, 5));
        fields.add(new Field("string", DataType.STRING, 1));
        schema.addFieldList(fields);
        
        schema.addAfterOperation(new Trim());
        return schema;
    }

    @Test
    public void testShortRecord() throws Exception{
        String shortByOneCharacter = "    1    2    3    4";
        IOContext ctx = new IOContextBuilder()
                .schema(getNumericSchema())
                .in(new ByteArrayInputStream(shortByOneCharacter.getBytes()))
                .build();


        FixedFileReader reader = new FixedFileReader();
        reader.open(ctx);

        try{
            Record record = reader.read();
            fail("should have thrown ValidationException");
        }
        catch (ValidationException expected){
        }
    }

    @Test
    public void testLongRecord() throws Exception{

        String longByOneCharacter = "    1    2    3    4ab";
        IOContext ctx = new IOContextBuilder()
                .schema(getNumericSchema())
                .in(new ByteArrayInputStream(longByOneCharacter.getBytes()))
                .build();

        FixedFileReader reader = new FixedFileReader();
        reader.open(ctx);

        try{
            Record record = reader.read();
            fail("should have thrown ValidationException");
        }
        catch (ValidationException expected){
        }
    }

    @Test
    public void testZeroLength() throws SchemaException{

        Schema schema = new Schema();
        schema.setName("test");
        schema.setVersion("0");

        
        //zero length ok
        FieldList fields = new FieldList();
        fields.add(new Field("a", DataType.STRING, 0));


        try{
            fields.add(new Field("a", DataType.STRING, -1));
            fail("should have thrown IllegalArgumentException");
        }
        catch (IllegalArgumentException expected){
        }

    }
}