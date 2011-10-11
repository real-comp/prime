/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcomp.data.transform;

import com.thoughtworks.xstream.XStream;
import com.realcomp.data.schema.xml.XStreamFactory;
import com.realcomp.data.validation.field.Key;
import com.realcomp.data.conversion.Round;
import com.realcomp.data.schema.Field;
import com.realcomp.data.conversion.Trim;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krenfro
 */
public class TransformerXMLSerializationTest {
    
    public TransformerXMLSerializationTest() {
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
