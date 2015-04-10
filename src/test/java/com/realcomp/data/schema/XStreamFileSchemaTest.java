package com.realcomp.data.schema;

import com.realcomp.data.record.io.Format;
import com.realcomp.data.conversion.FirstName;
import java.util.regex.Pattern;
import com.realcomp.data.schema.xml.XStreamFactory;
import com.realcomp.data.validation.field.DoubleRangeValidator;
import com.realcomp.data.conversion.Replace;
import com.realcomp.data.conversion.UpperCase;
import com.realcomp.data.conversion.Trim;
import org.junit.Before;
import com.realcomp.data.DataType;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import java.io.StringWriter;
import org.junit.Test;
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
        schema.addField(new Field("pid", DataType.LONG, 10));

        Field owner = new Field("owner", DataType.STRING, 20);
        Replace replace = new Replace(":", "-");
        owner.addOperation(replace);

        schema.addField(owner);
        schema.addField(new Field("zip", DataType.INTEGER, 5));
        schema.addField(new Field("value", DataType.FLOAT, 7));

        Field area = new Field("area", DataType.DOUBLE);
        DoubleRangeValidator validator = new DoubleRangeValidator();
        validator.setMin(1000);
        validator.setMax(2000);
        area.addOperation(validator);
        schema.addField(area);

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

        assertTrue(getSchema().equals(deserialized));
    }

    @Test
    public void testAnotherDeserialization() throws SchemaException{

        Schema schema = SchemaFactory.buildSchema(
                XStreamFileSchemaTest.class.getResourceAsStream("facl.schema"));
        
        Field storiesNumber = schema.getField("storiesNumber");
        assertEquals(DataType.FLOAT, storiesNumber.getType());
        
        
        Field totalValue = schema.getField("totalValue");
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


        Field f = schema.getField("name");
        assertEquals(1, f.getOperations().size());
        FirstName firstName = (FirstName) f.getOperations().get(0);
        assertFalse(firstName.isLastNameFirst());

    }

    @Test
    public void testSampleSchema() throws SchemaException{

        Schema schema = SchemaFactory.buildSchema(
                XStreamFileSchemaTest.class.getResourceAsStream("test_1.schema"));
        Field field = schema.getField("data");

        //assertTrue(field.getOperations().contains(new Trim()));
    }
}
