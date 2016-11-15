package com.realcomp.prime.schema;

import com.realcomp.prime.DataType;
import com.realcomp.prime.Operation;
import com.realcomp.prime.conversion.Concat;
import com.realcomp.prime.conversion.ConversionException;
import com.realcomp.prime.conversion.ReplaceFirst;
import com.realcomp.prime.record.Record;
import com.realcomp.prime.record.io.*;
import com.realcomp.prime.schema.xml.XStreamFactory;
import com.realcomp.prime.validation.ValidationException;
import com.thoughtworks.xstream.XStream;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.*;

/**
 * Tests special xml characters in serialized schema.
 * It is often necessary to include special reserved xml characters
 * as parameters to operations.
 *
 */
public class DoubleQuoteTest{

    private static final Logger logger = Logger.getLogger(DoubleQuoteTest.class.getName());
    private XStream xstream;

    @Before
    public void init(){

        xstream = XStreamFactory.build();

    }

    protected Schema getSchema() throws SchemaException{

        Schema schema = new Schema();
        schema.setName("test");
        schema.setVersion("1.0");
        FieldList fieldList = new FieldList();
        fieldList.add(new Field("pid", DataType.LONG, 10));
        

        Field owner = new Field("owner", DataType.STRING, 20);

        Concat concat = new Concat();
        ReplaceFirst replaceFirst = new ReplaceFirst();
        replaceFirst.setRegex("\"");

        List<String> fieldNames = new ArrayList<>();
        fieldNames.add("a");
        fieldNames.add("b");
        concat.setFields(fieldNames);
        owner.addOperation(concat);
        owner.addOperation(replaceFirst);

        fieldList.add(owner);
        fieldList.add(new Field("zip", DataType.INTEGER, 5));
        fieldList.add(new Field("value", DataType.FLOAT, 7));

        schema.getFormat().put("type", "TAB");
        schema.addFieldList(fieldList);
        return schema;
    }

    @Test
    public void testSerialization() throws SchemaException{

        String xml = xstream.toXML(getSchema());
        System.out.println(xml);

    }

    @Test
    public void testDeserialization() throws SchemaException{

        String xml = xstream.toXML(getSchema());
        System.out.println(xml);

        Schema schema = SchemaFactory.buildSchema(new ByteArrayInputStream(xml.getBytes()));

        Field field = schema.getDefaultFieldList().get("owner");
        boolean foundIt = false;
        for (Operation op : field.getOperations()){

            if (op instanceof ReplaceFirst){
                assertEquals("\"", ((ReplaceFirst) op).getRegex());
                foundIt = true;
            }
        }

        if (!foundIt){
            fail("did not contain ReplaceFirst operation");
        }


    }


    @Test
    public void testQuoteEscapeCharacterDefinedInSchema() throws IOException, SchemaException, ValidationException, ConversionException{

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Schema schema = SchemaFactory.buildSchema(
                DoubleQuoteTest.class.getResourceAsStream("csvEscapeCharacter.schema"));

        assertEquals("\"", schema.getFormat().get("escapeCharacter"));
        IOContext ctx = new IOContextBuilder().schema(schema).out(output).build();
        RecordWriter writer = RecordWriterFactory.build(schema);
        writer.open(ctx);
        Record r = new Record();
        r.put("a", "asdf");
        r.put("b", "as\"df");
        r.put("c", "\"\"");

        writer.write(r);
        writer.write(r);
        writer.write(r);
        writer.close();

        byte[] bytes = output.toByteArray();
        System.out.println(new String(bytes));


        ctx = new IOContextBuilder().schema(schema).in(new ByteArrayInputStream(bytes)).build();
        schema.getFormat().put("escapeCharacter", "\\");
        RecordReader reader = new RecordReaderFactory().build(schema);
        reader.open(ctx);
        r = reader.read();
        assertEquals("asdf", r.get("a"));
        assertEquals("as\"df", r.get("b"));
        assertEquals("\"\"", r.get("c"));
        assertNotNull(reader.read());
        assertNotNull(reader.read());
        assertNull(reader.read());


    }

}
