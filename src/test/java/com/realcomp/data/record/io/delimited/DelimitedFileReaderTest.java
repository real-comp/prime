package com.realcomp.data.record.io.delimited;

import com.realcomp.data.DataType;
import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.record.Record;
import com.realcomp.data.record.io.IOContext;
import com.realcomp.data.record.io.IOContextBuilder;
import com.realcomp.data.schema.Field;
import com.realcomp.data.schema.FieldList;
import com.realcomp.data.schema.Schema;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.validation.ValidationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author krenfro
 */
public class DelimitedFileReaderTest{

    public DelimitedFileReaderTest(){
    }

    /**
     * Test of open method, of class DelimitedFileParser.
     *
     * @throws IOException
     * @throws SchemaException
     */
    @Test
    public void testOpenClose() throws IOException, SchemaException{

        DelimitedFileReader reader = new DelimitedFileReader();

        try{
            reader.open(null);
            fail("should have thrown IllegalArgumentException");
        }
        catch (IllegalArgumentException expected){
        }

        reader.close();
        reader.close();

        String data = "a\tb\tc";

        IOContext ctx = new IOContextBuilder()
                .schema(get3FieldSchema())
                .in(new ByteArrayInputStream(data.getBytes()))
                .build();

        reader.open(ctx);
        reader.close();
        reader.close();
    }

    /**
     * Test of getType method, of class DelimitedFileParser.
     */
    @Test
    public void testGetType() throws IOException, SchemaException{

        DelimitedFileReader reader = new DelimitedFileReader();
        assertEquals("TAB", reader.getDefaults().get("type"));
        assertTrue('\t' == reader.getDelimiter());


        IOContext ctx = new IOContextBuilder()
                .attribute("type", "CSV")
                .in(new ByteArrayInputStream(new byte[1]))
                .build();
        reader.open(ctx);

        assertTrue(',' == reader.getDelimiter());
        ctx = new IOContextBuilder(ctx).attribute("type", "TAB").build();
        reader.open(ctx);
        assertTrue('\t' == reader.getDelimiter());

        ctx = new IOContextBuilder(ctx).attribute("type", "-").build();
        reader.open(ctx);
        assertTrue('-' == reader.getDelimiter());
    }

    /**
     * Test of next method, of class DelimitedFileParser.
     */
    @Test
    public void testNext() throws Exception{


        String data = "a\tb\tc\nd\te\tf";
        IOContext ctx = new IOContextBuilder()
                .schema(get3FieldSchema())
                .in(new ByteArrayInputStream(data.getBytes()))
                .build();

        DelimitedFileReader reader = new DelimitedFileReader();
        reader.open(ctx);

        Record record = reader.read();
        assertNotNull(record);
        record = reader.read();
        assertNotNull(record);
        record = reader.read();
        assertNull(record);

        reader.close();
    }

    @Test
    public void testCSV() throws Exception{

        String data = "\"a123\",\"b123\",\"c123\"";

        Map<String, String> attributes = new HashMap<>();
        attributes.put("type", "CSV");
        IOContext ctx = new IOContextBuilder()
                .schema(get3FieldSchema())
                .in(new ByteArrayInputStream(data.getBytes()))
                .attributes(attributes)
                .build();

        DelimitedFileReader reader = new DelimitedFileReader();
        reader.open(ctx);


        Record record = reader.read();
        assertNotNull(record);
        assertEquals("a123", record.get("a"));
        assertEquals("b123", record.get("b"));
        assertEquals("c123", record.get("c"));
        assertNull(reader.read());
        reader.close();

        //embedded comma
        data = "\"a123\",\"b1,23\",\"c123\"";
        ctx = new IOContextBuilder(ctx)
                .in(new ByteArrayInputStream(data.getBytes()))
                .build();
        reader.open(ctx);

        record = reader.read();
        assertNotNull(record);
        assertEquals("a123", record.get("a"));
        assertEquals("b1,23", record.get("b"));
        assertEquals("c123", record.get("c"));
        assertNull(reader.read());
        reader.close();


        //embedded comma
        data = "\"a123\",\"b1,23\",\"c123\"";
        ctx = new IOContextBuilder(ctx)
                .in(new ByteArrayInputStream(data.getBytes()))
                .build();
        reader.open(ctx);


        record = reader.read();
        assertNotNull(record);
        assertEquals("a123", record.get("a"));
        assertEquals("b1,23", record.get("b"));
        assertEquals("c123", record.get("c"));
        assertNull(reader.read());
        reader.close();

        //embedded quote
        data = "\"a123\",\"b1\"\"23\",\"c123\"";
        ctx = new IOContextBuilder(ctx)
                .in(new ByteArrayInputStream(data.getBytes()))
                .build();
        reader.open(ctx);

        record = reader.read();
        assertNotNull(record);
        assertEquals("a123", record.get("a"));
        assertEquals("b1\"23", record.get("b"));
        assertEquals("c123", record.get("c"));
        assertNull(reader.read());
        reader.close();

        //embedded quote at end
        data = "\"a123\",\"b123\"\"\",\"c123\"";
        ctx = new IOContextBuilder(ctx)
                .in(new ByteArrayInputStream(data.getBytes()))
                .build();
        reader.open(ctx);

        record = reader.read();
        assertNotNull(record);
        assertEquals("a123", record.get("a"));
        assertEquals("b123\"", record.get("b"));
        assertEquals("c123", record.get("c"));
        assertNull(reader.read());
        reader.close();

    }

    /**
     * Test of loadRecord method, of class DelimitedFileParser.
     */
    @Test
    public void testLoadRecordMissingFields() throws Exception{

        String[] data = new String[]{"a123", "b123"};
        FieldList fields = get3FieldSchema().getDefaultFieldList();

        IOContext ctx = new IOContextBuilder()
                .schema(get3FieldSchema())
                .in(new ByteArrayInputStream(new byte[10]))
                .build();

        DelimitedFileReader reader = new DelimitedFileReader();
        reader.open(ctx);
        try{
            reader.loadRecord(fields, data);
            fail("should have thrown ValidationException");
        }
        catch (ValidationException expected){
        }


        try{
            data = new String[]{"a123", "b123", "c123", "d123"};
            reader.loadRecord(fields, data);
            fail("should have thrown ValidationException");
        }
        catch (ValidationException expected){
        }

        try{
            data = new String[]{};
            reader.loadRecord(fields, data);
            fail("should have thrown ValidationException");
        }
        catch (ValidationException expected){
        }

    }

    protected Schema get3FieldSchema() throws SchemaException{

        Schema schema = new Schema();
        schema.setName("test");
        schema.setVersion("0");

        FieldList fields = new FieldList();
        fields.add(new Field("a", DataType.STRING));
        fields.add(new Field("b", DataType.STRING));
        fields.add(new Field("c", DataType.STRING));
        schema.addFieldList(fields);
        return schema;
    }

    @Test
    public void testUnTerminatedQuotedField() throws IOException, SchemaException, ValidationException, ConversionException{

        Schema schema = get3FieldSchema();
        schema.getFormat().put("header", "false");
        schema.getFormat().put("type", "CSV");
        schema.getFormat().put("strictQuotes", "true");
        IOContext ctx = new IOContextBuilder()
                .schema(schema)
                .in(this.getClass().getResourceAsStream("unTerminatedQuotedField.csv"))
                .build();

        DelimitedFileReader reader = new DelimitedFileReader();
        reader.open(ctx);
        Record record = reader.read();
        assertNotNull(record);
        assertEquals("CRYER, DUANE PETE\"", record.getString("b"));
        assertEquals("PANA", record.getString("c"));
        reader.close();

        schema = new Schema();
        schema.addField(new Field("a"));
        schema.addField(new Field("b"));
        schema.addField(new Field("c"));
        schema.addField(new Field("d"));
        schema.addField(new Field("e"));
        schema.addField(new Field("f"));
        schema.addField(new Field("g"));
        schema.addField(new Field("h"));
        schema.getFormat().put("header", "false");
        schema.getFormat().put("type", "CSV");
        schema.getFormat().put("strictQuotes", "true");
        ctx = new IOContextBuilder()
                .schema(schema)
                .in(this.getClass().getResourceAsStream("unTerminatedQuotedField2.csv"))
                .build();

        reader.open(ctx);
        record = reader.read();
        for (int x = 0; x < 5; x++){
            assertNotNull(record);
            assertEquals(8, record.size());
            record = reader.read();
        }
        reader.close();
    }



    @Test
    public void testUnterminatedQuotedPatterns(){
        Pattern problem = Pattern.compile("(.+)(\"\",\")");

        String data = "\"KYLE\"\",\"RENFRO\"";
        String repaired = problem.matcher(data).replaceAll("$1\\\\\"\",\"");
        assertEquals("\"KYLE\\\"\",\"RENFRO\"", repaired);
    }
}