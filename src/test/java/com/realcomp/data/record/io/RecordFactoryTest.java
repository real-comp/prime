/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcomp.data.record.io;

import com.realcomp.data.record.Record;
import com.realcomp.data.conversion.Round;
import com.realcomp.data.DataType;
import com.realcomp.data.conversion.Resize;
import com.realcomp.data.conversion.Trim;
import com.realcomp.data.schema.Field;
import com.realcomp.data.schema.FileSchema;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krenfro
 */
public class RecordFactoryTest {
    
    public RecordFactoryTest() {
    }
    
    private FileSchema getSchema(){
        
        FileSchema schema = new FileSchema();
        Field a = new Field("a", DataType.STRING);
        a.addOperation(new Trim());
        Field b = new Field("b", DataType.STRING);
        b.addOperation(new Resize(10));
        Field c = new Field("c", DataType.INTEGER);
        c.addOperation(new Round());
        
        schema.addField(a);
        schema.addField(b);
        schema.addField(c);
        return schema;
    }

    @Test
    public void factoryTest() throws Exception {
        
        FileSchema schema = getSchema();
        RecordFactory factory = new RecordFactory(getSchema());
        
        String[] data = new String[]{"asdf ", "stuff", "1.4"};
        Record record = factory.build(data);
        
        assertEquals("asdf", record.get("a"));
        assertEquals("stuff     ", record.get("b"));
        assertEquals(1, record.get("c"));
        
        
    }

}
