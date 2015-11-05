package com.realcomp.data.record.io;

import com.realcomp.data.record.Record;
import com.realcomp.data.conversion.Round;
import com.realcomp.data.DataType;
import com.realcomp.data.conversion.LeftPad;
import com.realcomp.data.conversion.RightPad;
import com.realcomp.data.conversion.Trim;
import com.realcomp.data.schema.Field;
import com.realcomp.data.schema.FieldList;
import com.realcomp.data.schema.Schema;
import com.realcomp.data.schema.SchemaException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import static org.junit.Assert.*;

public class RecordFactoryTest{

    public RecordFactoryTest(){
    }

    private Schema getSchema(){

        Schema schema = new Schema();
        Field a = new Field("a", DataType.STRING);
        a.addOperation(new Trim());
        Field b = new Field("b", DataType.STRING);
        b.addOperation(new RightPad(10));
        Field c = new Field("c", DataType.INTEGER);
        c.addOperation(new Round());

        FieldList fieldList = new FieldList();
        fieldList.add(a);
        fieldList.add(b);
        fieldList.add(c);
            schema.addFieldList(fieldList);
        
        return schema;
    }

    @Test
    public void factoryTest() throws Exception{

        Schema schema = getSchema();
        RecordFactory factory = new RecordFactory(getSchema());

        String[] data = new String[]{"asdf ", "stuff", "1.4"};
        Record record = factory.build(data);

        assertEquals("asdf", record.get("a"));
        assertEquals("stuff     ", record.get("b"));
        assertEquals(1, record.get("c"));


    }
}
