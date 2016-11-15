package com.realcomp.prime.transform;

import com.realcomp.prime.conversion.Round;
import com.realcomp.prime.conversion.Trim;
import com.realcomp.prime.schema.Field;
import com.realcomp.prime.schema.xml.XStreamFactory;
import com.realcomp.prime.validation.field.Key;
import com.thoughtworks.xstream.XStream;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author krenfro
 */
public class TransformerXMLSerializationTest{

    public TransformerXMLSerializationTest(){
    }

    @Test
    public void testXML(){

        Transformer t = new Transformer();
        t.addAfter(new Trim());

        Field a = new Field("a");
        a.addOperation(new Round());

        Field b = new Field("b");
        b.addOperation(new Trim());

        Field c = new Field("c");
        c.addOperation(new Key());

        t.addField(a);
        t.addField(b);
        t.addField(c);


        XStream xstream = XStreamFactory.build();
        String xml = xstream.toXML(t);
        System.out.println(xml);


        Transformer fromXML = (Transformer) xstream.fromXML(xml);

        assertEquals(3, fromXML.getFields().size());
        assertEquals(new Trim(), fromXML.getAfter().get(0));
        assertEquals(new Trim(), fromXML.getFields().get(1).getOperations().get(0));
    }
}
