package com.realcomp.prime.schema;

import com.realcomp.prime.DataType;
import com.realcomp.prime.conversion.Replace;
import com.realcomp.prime.conversion.Trim;
import com.realcomp.prime.conversion.UpperCase;
import com.realcomp.prime.schema.xml.XStreamFactory;
import com.realcomp.prime.validation.field.DoubleRangeValidator;
import com.thoughtworks.xstream.XStream;
import org.junit.Before;
import org.junit.Test;

import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 *
 * @author krenfro
 */
public class XStreamFileSchemaTest{

    private XStream xstream;

    @Before
    public void init(){
        xstream = XStreamFactory.build();
    }

    @Test
    public void testConstructor(){
        new XStreamFactory();
    }

    protected Schema getSchema() throws SchemaException{
        Schema schema = new Schema();
        schema.setName("test");
        schema.setVersion("1.0");
        FieldList fields = new FieldList();
        fields.add(new Field("pid", DataType.LONG, 10));

        Field owner = new Field("owner", DataType.STRING, 20);
        Replace replace = new Replace(":", "-");
        owner.addOperation(replace);

        fields.add(owner);
        fields.add(new Field("zip", DataType.INTEGER, 5));
        fields.add(new Field("value", DataType.FLOAT, 7));

        Field area = new Field("area", DataType.DOUBLE);
        DoubleRangeValidator validator = new DoubleRangeValidator();
        validator.setMin(1000);
        validator.setMax(2000);
        area.addOperation(validator);
        fields.add(area);
        schema.addFieldList(fields);

        schema.addBeforeOperation(new UpperCase());
        schema.addAfterOperation(new Trim());

        FieldList typeB = new FieldList(Pattern.compile(".{19}"));
        typeB.add(new Field("pid", DataType.LONG, 10));
        typeB.add(new Field("zip5", DataType.INTEGER, 5));
        typeB.add(new Field("zip4", DataType.INTEGER, 4));

        schema.addFieldList(typeB);

        schema.getFormat().put("type", "TAB");

        return schema;
    }

    @Test
    public void testSerialization() throws SchemaException{

        String xml = xstream.toXML(getSchema());
        System.out.println(xml);

        Schema deserialized = (Schema) xstream.fromXML(xml);
        assertEquals(1, deserialized.getAfterOperations().size());
        assertTrue(deserialized.getAfterOperations().get(0).getClass().equals(Trim.class));
        assertEquals(5, deserialized.getDefaultFieldList().size());
        assertEquals(DataType.LONG, deserialized.getDefaultFieldList().get(0).getType());
        assertEquals(DataType.STRING, deserialized.getDefaultFieldList().get(1).getType());
        assertNull(deserialized.getFieldLists().get(0).getClassifier());
        assertEquals(".{19}", deserialized.getFieldLists().get(1).getClassifier().toString());

        assertTrue(getSchema().equals(deserialized));
    }

    @Test
    public void testAnotherDeserialization() throws SchemaException{

        Schema schema = SchemaFactory.buildSchema(
                XStreamFileSchemaTest.class.getResourceAsStream("facl.schema"));
        
        Field storiesNumber = schema.getDefaultFieldList().get("storiesNumber");
        assertEquals(DataType.FLOAT, storiesNumber.getType());
        
        
        Field totalValue = schema.getDefaultFieldList().get("totalValue");
        assertEquals(DataType.LONG, totalValue.getType());
        
        
    }

    @Test
    public void testDeserialization() throws SchemaException{

        Schema schema = SchemaFactory.buildSchema(
                XStreamFileSchemaTest.class.getResourceAsStream("test_1.schema"));
        assertEquals("TAB", schema.getFormat().get("type"));

        assertEquals(2, schema.getFieldLists().size());
        assertEquals(6, schema.getDefaultFieldList().size());
        assertEquals(1, schema.getBeforeOperations().size());
        assertEquals(1, schema.getAfterOperations().size());

        schema = SchemaFactory.buildSchema(
                XStreamFileSchemaTest.class.getResourceAsStream("test_2.schema"));
        assertEquals("CSV", schema.getFormat().get("type"));

        schema = SchemaFactory.buildSchema(
                XStreamFileSchemaTest.class.getResourceAsStream("test_3.schema"));
        assertEquals("CSV", schema.getFormat().get("type"));
        assertNull(schema.getFormat().get("header"));

        schema = SchemaFactory.buildSchema(
                XStreamFileSchemaTest.class.getResourceAsStream("test_4.schema"));
        assertEquals("CSV", schema.getFormat().get("type"));
        assertEquals("true", schema.getFormat().get("header"));


        Field f = schema.getDefaultFieldList().get("name");
        assertEquals(0, f.getOperations().size());

    }

    @Test
    public void testSampleSchema() throws SchemaException{

        Schema schema = SchemaFactory.buildSchema(
                XStreamFileSchemaTest.class.getResourceAsStream("test_1.schema"));
        Field field = schema.getDefaultFieldList().get("prime");

        //assertTrue(field.getOperations().contains(new Trim()));
    }
}
