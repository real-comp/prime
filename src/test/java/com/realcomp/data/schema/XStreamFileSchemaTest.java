package com.realcomp.data.schema;


import com.realcomp.data.validation.Severity;
import com.realcomp.data.validation.field.DoubleRangeValidator;
import com.realcomp.data.schema.xml.OperationConverter;
import com.realcomp.data.record.parser.FixedFileParser;
import com.realcomp.data.record.parser.RecordParser;
import com.realcomp.data.record.parser.DelimitedFileParser;
import java.util.regex.Pattern;
import java.util.List;
import java.util.ArrayList;
import com.realcomp.data.conversion.Replace;
import com.realcomp.data.conversion.UpperCase;
import com.realcomp.data.conversion.Trim;
import org.junit.Before;
import com.realcomp.data.DataType;
import com.realcomp.data.annotation.Converter;
import com.realcomp.data.annotation.Validator;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.util.Set;
import org.reflections.Configuration;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
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


        //use reflection to find all Validatior and Converter annotated classes.
        Configuration conf = new ConfigurationBuilder()
            .setUrls(ClasspathHelper.getUrlsForPackagePrefix("com.realcomp"));
            //.setScanners(new TypeElementsScanner());

        Reflections reflections = new Reflections(conf);
        Set<Class<?>> validators = reflections.getTypesAnnotatedWith(Validator.class);
        Set<Class<?>> converters = reflections.getTypesAnnotatedWith(Converter.class);
        
        xstream = new XStream(new DomDriver());
        xstream.processAnnotations(FileSchema.class);
        xstream.processAnnotations(SchemaField.class);

        xstream.registerConverter(new OperationConverter());
        
        for (Class c: validators){
            Validator annotation = (Validator) c.getAnnotation(Validator.class);
            xstream.alias(annotation.value(), c);
            xstream.processAnnotations(c);
        }

        for (Class c: converters){
            Converter annotation = (Converter) c.getAnnotation(Converter.class);
            xstream.alias(annotation.value(), c);
            xstream.processAnnotations(c);
        }
        
    }


    protected FileSchema getSchema() throws SchemaException{
        FileSchema schema = new FileSchema();
        schema.setName("test");
        schema.setVersion("1.0");
        schema.addSchemaField(new SchemaField("pid", DataType.LONG, 10));

        SchemaField owner = new SchemaField("owner", DataType.STRING, 20);
        Replace replace = new Replace(":", "-");
        owner.addOperation(replace);
        
        schema.addSchemaField(owner);
        schema.addSchemaField(new SchemaField("zip", DataType.INTEGER, 5));
        schema.addSchemaField(new SchemaField("value", DataType.FLOAT, 7));

        SchemaField area = new SchemaField("area", DataType.DOUBLE);
        DoubleRangeValidator validator = new DoubleRangeValidator();
        validator.setMin(1000);
        validator.setMax(2000);
        area.addOperation(validator);
        schema.addSchemaField(area);

        schema.addPreop(new UpperCase());
        schema.addPostop(new Trim());

        Classifier classifier = new Classifier("recordtypeb", ".{19}");
        classifier.addSchemaField(new SchemaField("pid", DataType.LONG, 10));
        classifier.addSchemaField(new SchemaField("zip5", DataType.INTEGER, 5));
        classifier.addSchemaField(new SchemaField("zip4", DataType.INTEGER, 4));
        schema.addClassifier(classifier);

        DelimitedFileParser parser = new DelimitedFileParser();
        parser.setType(DelimitedFileParser.Type.TAB);        
        schema.setParser(parser);

        return schema;
    }
    
    @Test
    public void testSerialization() throws SchemaException{

        String xml = xstream.toXML(getSchema());
        System.out.println(xml);

        FileSchema deserialized = (FileSchema) xstream.fromXML(xml);
        assertEquals(1, deserialized.getPostops().size());
        deserialized.getPostops().get(0).getClass().equals(Trim.class);

        assertTrue(getSchema().equals(deserialized));
    }

    
}
