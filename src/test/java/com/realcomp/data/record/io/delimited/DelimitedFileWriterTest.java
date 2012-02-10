package com.realcomp.data.record.io.delimited;

import com.realcomp.data.DataType;
import com.realcomp.data.record.Record;
import com.realcomp.data.record.io.IOContext;
import com.realcomp.data.schema.Field;
import com.realcomp.data.schema.Schema;
import com.realcomp.data.schema.SchemaException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import static org.junit.Assert.*;
import org.junit.Test;

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
    public void testOpenClose() throws IOException, SchemaException{

        DelimitedFileWriter writer = new DelimitedFileWriter();
        try{
            writer.open(null);
            fail("should have thrown IllegalArgumentException");
        }
        catch(IllegalArgumentException expected){}

        writer.close();
        writer.close();
    }


    /**
     * Test of getType method, of class DelimitedFileParser.
     */
    @Test
    public void testGetType() throws IOException, SchemaException {

        DelimitedFileWriter writer = new DelimitedFileWriter();
        assertEquals("TAB", writer.getDefaults().get("type"));
        assertTrue('\t' == writer.getDelimiter());
        
        IOContext ctx = new IOContext.Builder()
            .attribute("type", "CSV")
            .out(new ByteArrayOutputStream())
            .build();
        writer.open(ctx);
        
        assertTrue(',' == writer.getDelimiter());
        
        ctx = new IOContext.Builder(ctx)
            .attribute("type", "TAB")
            .build();
        writer.open(ctx);
        assertTrue('\t' == writer.getDelimiter());
        
        ctx = new IOContext.Builder(ctx)
            .attribute("type", "-")
            .build();
        writer.open(ctx);
        assertTrue('-' == writer.getDelimiter());
    }
    


    @Test
    public void testCSV() throws Exception {

        String data = "\"a123\",\"b123\",\"c123\"";
        
        IOContext ctx = new IOContext.Builder()
            .schema(get3FieldSchema())
            .attribute("type", "CSV")
            .out(new ByteArrayOutputStream())
            .in(new ByteArrayInputStream(data.getBytes()))
            .build();
         
        
        DelimitedFileReader reader = new DelimitedFileReader();
        reader.open(ctx);
        
        Record a = reader.read();
        assertNotNull(a);
        assertEquals("a123", a.get("a"));
        assertEquals("b123", a.get("b"));
        assertEquals("c123", a.get("c"));
        
        DelimitedFileWriter writer = new DelimitedFileWriter();
        writer.open(ctx);

        writer.write(a);
        writer.write(a);
        writer.write(a);
        
        writer.close();
        reader.close();
        
        //copy output to new input
        byte[] bytes = ((ByteArrayOutputStream) ctx.getOut()).toByteArray();
        ctx = new IOContext.Builder(ctx).in(new ByteArrayInputStream(bytes)).build();
        reader.open(ctx);
        Record b = reader.read();
        assertEquals(a, b);
        assertEquals(a, reader.read());
        assertEquals(a, reader.read());
        assertNull(reader.read());
        reader.close();
    }


    @Test
    public void testClassification() throws SchemaException{

        Schema schema = get3FieldSchema();
        
        Record good = new Record();
        good.put("a", "1");
        good.put("b", "2");
        good.put("c", "3");

        assertTrue(schema.getDefaultFieldList().equals(schema.classify(good)));
        
        Record bad = new Record();
        bad.put("foo","bar");

        
        try{
            //with (default) strict matching, exception is thrown for failed classification.
            schema.classify(bad);
            fail("should have throws SchemaException");
        }
        catch(SchemaException ok){}

        
        //the record does not classify, so default is returned.
        schema.setStrict(false);
        assertEquals(schema.getDefaultFieldList(), schema.classify(bad));
        
    }


    protected Schema get3FieldSchema() throws SchemaException{
        
        Schema schema = new Schema();
        schema.setName("test");
        schema.setVersion("0");
        schema.addField(new Field("a", DataType.STRING));
        schema.addField(new Field("b", DataType.STRING));
        schema.addField(new Field("c", DataType.STRING));
        return schema;
    }

}