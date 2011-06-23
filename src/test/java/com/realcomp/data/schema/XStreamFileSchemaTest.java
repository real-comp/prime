package com.realcomp.data.schema;


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
        schema.addField(new SchemaField("pid", DataType.LONG, 10));

        SchemaField owner = new SchemaField("owner", DataType.STRING, 20);
        Replace replace = new Replace(":", "-");
        owner.addOperation(replace);
        
        schema.addField(owner);
        schema.addField(new SchemaField("zip", DataType.INTEGER, 5));
        schema.addField(new SchemaField("value", DataType.FLOAT, 7));

        SchemaField area = new SchemaField("area", DataType.DOUBLE);
        DoubleRangeValidator validator = new DoubleRangeValidator();
        validator.setMin(1000);
        validator.setMax(2000);
        area.addOperation(validator);
        schema.addField(area);

        schema.addBeforeOperation(new UpperCase());
        schema.addAfterOperation(new Trim());

        Classifier classifier = new Classifier("recordtypeb", ".{19}");
        classifier.addSchemaField(new SchemaField("pid", DataType.LONG, 10));
        classifier.addSchemaField(new SchemaField("zip5", DataType.INTEGER, 5));
        classifier.addSchemaField(new SchemaField("zip4", DataType.INTEGER, 4));
        schema.addClassifier(classifier);

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
        deserialized.getAfterOperations().get(0).getClass().equals(Trim.class);
        assertEquals(5, deserialized.getFields().size());
        assertEquals(DataType.LONG, deserialized.getFields().get(0).getType());
        assertEquals(DataType.STRING, deserialized.getFields().get(1).getType());

        assertTrue(getSchema().equals(deserialized));
    }

    @Test
    public void testDeserialization() throws SchemaException{

        FileSchema schema = SchemaFactory.buildFileSchema(
                XStreamFileSchemaTest.class.getResourceAsStream("test_1.schema"));
        assertEquals(Delimiter.TAB, ((DelimitedFileReader) schema.getReader()).getDelimiter());
        
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
        SchemaField field = schema.getField("data");
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

        FileSchema b = SchemaFactory.buildFileSchema(new ByteArrayInputStream(xml.getBytes()));
        assertEquals(a, b);
        assertTrue(b.getReader().supports(ExampleView.class));
        assertTrue(b.getReader().supports(DummyView.class));

    }

    
}
