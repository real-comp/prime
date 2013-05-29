package com.realcomp.data.schema;

import com.realcomp.data.Operation;
import com.realcomp.data.conversion.ReplaceFirst;
import java.util.logging.Logger;
import com.realcomp.data.conversion.Concat;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.ArrayList;
import com.realcomp.data.schema.xml.XStreamFactory;
import org.junit.Before;
import com.realcomp.data.DataType;
import com.thoughtworks.xstream.XStream;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests special xml characters in serialized schema.
 * It is often necessary to include special reserved xml characters
 * as parameters to operations.
 *
 * @author krenfro
 */
public class DoubleQuote{

    private static final Logger logger = Logger.getLogger(DoubleQuote.class.getName());
    private XStream xstream;

    @Before
    public void init(){

        xstream = XStreamFactory.build();

    }

    protected Schema getSchema() throws SchemaException{

        Schema schema = new Schema();
        schema.setName("test");
        schema.setVersion("1.0");
        schema.addField(new Field("pid", DataType.LONG, 10));

        Field owner = new Field("owner", DataType.STRING, 20);

        Concat concat = new Concat();
        ReplaceFirst replaceFirst = new ReplaceFirst();
        replaceFirst.setRegex("\"");

        List<String> fieldNames = new ArrayList<String>();
        fieldNames.add("a");
        fieldNames.add("b");
        concat.setFields(fieldNames);
        owner.addOperation(concat);
        owner.addOperation(replaceFirst);

        schema.addField(owner);
        schema.addField(new Field("zip", DataType.INTEGER, 5));
        schema.addField(new Field("value", DataType.FLOAT, 7));

        schema.getFormat().put("type", "TAB");
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

        Field field = schema.getField("owner");
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
}
