package com.realcomp.prime.schema;

import java.util.logging.Logger;
import com.realcomp.prime.conversion.Concat;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.ArrayList;
import com.realcomp.prime.schema.xml.XStreamFactory;
import org.junit.Before;
import com.realcomp.prime.DataType;
import com.thoughtworks.xstream.XStream;
import org.junit.Test;

/**
 *
 */
public class MultiFieldOperationSchemaTest{

    private static final Logger logger = Logger.getLogger(MultiFieldOperationSchemaTest.class.getName());
    private XStream xstream;

    @Before
    public void init(){

        xstream = XStreamFactory.build();

    }

    protected Schema getSchema() throws SchemaException{

        Schema schema = new Schema();
        schema.setName("test");
        schema.setVersion("1.0");
        FieldList fields = new FieldList();
        fields.add(new Field("pid", DataType.LONG, 10));
        
        Field owner = new Field("owner", DataType.STRING, 20);

        Concat concat = new Concat();
        List<String> fieldNames = new ArrayList<>();
        fieldNames.add("a");
        fieldNames.add("b");
        concat.setFields(fieldNames);
        owner.addOperation(concat);

        fields.add(owner);
        fields.add(new Field("zip", DataType.INTEGER, 5));
        fields.add(new Field("value", DataType.FLOAT, 7));

        schema.addFieldList(fields);
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



    }
}
