package com.realcomp.data.schema;


import java.util.logging.Logger;
import com.realcomp.data.record.io.ParsePlanException;
import com.realcomp.data.conversion.Concat;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.ArrayList;
import com.realcomp.data.schema.xml.XStreamFactory;
import com.realcomp.data.record.io.Delimiter;
import com.realcomp.data.record.reader.DelimitedFileReader;
import org.junit.Before;
import com.realcomp.data.DataType;
import com.thoughtworks.xstream.XStream;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krenfro
 */
public class MultiFieldOperationSchemaTest {

    private static final Logger logger =  Logger.getLogger(MultiFieldOperationSchemaTest.class.getName());
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

        Concat concat = new Concat();
        List<String> fieldNames = new ArrayList<String>();
        fieldNames.add("a");
        fieldNames.add("b");
        concat.setFields(fieldNames);
        owner.addOperation(concat);
        
        schema.addField(owner);
        schema.addField(new SchemaField("zip", DataType.INTEGER, 5));
        schema.addField(new SchemaField("value", DataType.FLOAT, 7));

        DelimitedFileReader reader = new DelimitedFileReader();
        reader.setDelimiter(Delimiter.TAB);
        try{
            schema.setReader(reader);
        }
        catch(ParsePlanException e){
            logger.warning(e.getMessage());
        }


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

        FileSchema schema = SchemaFactory.buildFileSchema(new ByteArrayInputStream(xml.getBytes()));

        

    }

    
}
