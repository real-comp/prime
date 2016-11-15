package com.realcomp.prime.record.io;

import com.realcomp.prime.record.Record;
import com.realcomp.prime.conversion.Round;
import com.realcomp.prime.DataType;
import com.realcomp.prime.conversion.RightPad;
import com.realcomp.prime.conversion.Trim;
import com.realcomp.prime.schema.Field;
import com.realcomp.prime.schema.FieldList;
import com.realcomp.prime.schema.Schema;
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
