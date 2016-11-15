package com.realcomp.prime.record.io.fixed;

import com.realcomp.prime.schema.SchemaException;
import com.realcomp.prime.DataType;
import com.realcomp.prime.schema.Schema;
import com.realcomp.prime.record.Record;
import com.realcomp.prime.record.io.string.StringIOContext;
import com.realcomp.prime.record.io.string.StringIOContextBuilder;
import com.realcomp.prime.schema.Field;
import com.realcomp.prime.schema.FieldList;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 */
public class FixedStringReaderTest{

    public FixedStringReaderTest(){
    }

    /**
     * Test of next method, of class DelimitedFileParser.
     */
    @Test
    public void testNext() throws Exception{

        String data = "abcdef\nghijkl";
        StringIOContext ctx = (StringIOContext) new StringIOContextBuilder().schema(get3FieldSchema()).build();

        FixedFileReader reader = new FixedFileReader();
        reader.open(ctx);

        ctx.append(data);

        Record record = reader.read();
        assertNotNull(record);
        record = reader.read();
        assertNotNull(record);
        record = reader.read();
        assertNull(record);

        ctx.append("abcdef");
        record = reader.read();
        assertNotNull(record);


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
}