package com.realcomp.data.schema;


import java.util.regex.Pattern;
import com.realcomp.data.view.DummyView;
import com.realcomp.data.view.ExampleView;
import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.util.List;
import java.util.ArrayList;
import com.realcomp.data.schema.xml.XStreamFactory;
import com.realcomp.data.record.io.Delimiter;
import com.realcomp.data.validation.field.DoubleRangeValidator;
import com.realcomp.data.record.reader.DelimitedFileReader;
import com.realcomp.data.conversion.Replace;
import com.realcomp.data.conversion.UpperCase;
import com.realcomp.data.conversion.Trim;
import org.junit.Before;
import com.realcomp.data.DataType;
import com.thoughtworks.xstream.XStream;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krenfro
 */
public class XStreamFileSchemaTest {

    private XStream xstream;

    @Before
    public void init(){

        xstream = XStreamFactory.build();
        
    }


    protected FileSchema getSchema() throws SchemaException{
        FileSchema schema = new FileSchema();
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
        
        DelimitedFileReader reader = new DelimitedFileReader();
        reader.setDelimiter(Delimiter.TAB);
        schema.setReader(reader);

        return schema;
    }
    
    @Test
    public void testSerialization() throws SchemaException{
    
        String xml = xstream.toXML(getSchema());
        System.out.println(xml);

        FileSchema deserialized = (FileSchema) xstream.fromXML(xml);
        assertEquals(1, deserialized.getAfterOperations().size());
        assertTrue(deserialized.getAfterOperations().get(0).getClass().equals(Trim.class));
        assertEquals(5, deserialized.getDefaultFieldList().size());
        assertEquals(DataType.LONG, deserialized.getDefaultFieldList().get(0).getType());
        assertEquals(DataType.STRING, deserialized.getDefaultFieldList().get(1).getType());

        assertTrue(getSchema().equals(deserialized));
    }

    @Test
    public void testDeserialization() throws SchemaException{

        FileSchema schema = SchemaFactory.buildFileSchema(
                XStreamFileSchemaTest.class.getResourceAsStream("test_1.schema"));
        assertEquals(Delimiter.TAB, ((DelimitedFileReader) schema.getReader()).getDelimiter());
        
        assertEquals(2, schema.getFieldLists().size());
        assertEquals(6, schema.getDefaultFieldList().size());
        assertEquals(1, schema.getBeforeOperations().size());
        assertEquals(1, schema.getAfterOperations().size());
        
        schema = SchemaFactory.buildFileSchema(
                XStreamFileSchemaTest.class.getResourceAsStream("test_2.schema"));
        assertEquals(Delimiter.CSV, ((DelimitedFileReader) schema.getReader()).getDelimiter());
        
        schema = SchemaFactory.buildFileSchema(
                XStreamFileSchemaTest.class.getResourceAsStream("test_3.schema"));
        assertEquals(Delimiter.CSV, ((DelimitedFileReader) schema.getReader()).getDelimiter());

        schema = SchemaFactory.buildFileSchema(
                XStreamFileSchemaTest.class.getResourceAsStream("test_4.schema"));
        assertEquals(Delimiter.CSV, ((DelimitedFileReader) schema.getReader()).getDelimiter());

    }


    @Test
    public void testSampleSchema() throws SchemaException{

        FileSchema schema = SchemaFactory.buildFileSchema(
                XStreamFileSchemaTest.class.getResourceAsStream("test_1.schema"));
        Field field = schema.getField("data");
        assertTrue(field.getOperations().contains(new Trim()));
    }


    @Test
    public void testSchemaWithRecordViews() throws SchemaException{

        List<String> views = new ArrayList<String>();
        views.add("com.realcomp.data.view.ExampleView");
        views.add("com.realcomp.data.view.DummyView");
        
        FileSchema a = getSchema();
        a.getReader().setViews(views);
        assertTrue(a.getReader().supports(ExampleView.class));
        assertTrue(a.getReader().supports(DummyView.class));
        
        String xml = xstream.toXML(a);
        System.out.println(xml);

        FileSchema b = SchemaFactory.buildFileSchema(new ByteArrayInputStream(xml.getBytes()));
        assertEquals(a, b);
        assertTrue(b.getReader().supports(ExampleView.class));
        assertTrue(b.getReader().supports(DummyView.class));

    }

    
}
