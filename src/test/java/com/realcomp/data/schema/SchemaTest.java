package com.realcomp.data.schema;

import com.realcomp.data.conversion.Trim;
import com.realcomp.data.record.Record;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krenfro
 */
public class SchemaTest {
    
    public SchemaTest() {
    }

    @Test
    public void addNull(){
        Schema schema = new Schema();
        try{
            schema.addField(null);
            fail("should have thrown IAE");
        }
        catch(IllegalArgumentException expected){}
        
        try{
            schema.addFieldList(null);
            fail("should have thrown IAE");
        }
        catch(IllegalArgumentException expected){}
        
    }
    
    @Test
    public void testClassification() throws SchemaException{
        
        Schema schema = new Schema();
        try{
            schema.classify((Record) null);
            fail("should have thrown IAE");
        }
        catch(IllegalArgumentException expected){}
        
        
        try{
            schema.classify((String[]) null);
            fail("should have thrown IAE");
        }
        catch(IllegalArgumentException expected){}
        
        try{
            schema.classify(new Record());  
            fail("should have thrown SchemaException");
        }
        catch(SchemaException expected){}
        
        FieldList fieldList = new FieldList();
        fieldList.add(new Field("a"));
        fieldList.setClassifier(FieldList.DEFAULT_CLASSIFIER);
        schema.addFieldList(fieldList);
        
        try{
            assertEquals(fieldList, schema.classify(new Record()));
        }
        catch(SchemaException expected){}
        
        Record record = new Record();
        record.put("a", "test");
        assertEquals(fieldList, schema.classify(record));
    }
    
    @Test
    public void testScrictClassification() throws SchemaException{
        
        Schema schema = new Schema();
        FieldList fieldList = new FieldList();
        fieldList.add(new Field("a"));
        fieldList.setClassifier(FieldList.DEFAULT_CLASSIFIER);
        
        try{
            assertEquals(fieldList, schema.classify(new Record()));
        }
        catch(SchemaException expected){}
        
        schema.addFieldList(fieldList);
        schema.setStrict(false);
        assertEquals(fieldList, schema.classify(new Record()));
        
        try{
            schema.setStrict(true);
            assertEquals(fieldList, schema.classify(new Record()));
        }
        catch(SchemaException expected){}
        
    }
  



    @Test
    public void testCopyConstructor() throws SchemaException{
        
        
        Schema a = new Schema();
        Field original = new Field("original");
        original.addOperation(new Trim());        
        a.addField(original);
        
        Schema b = new Schema(a);
        
        assertEquals(a, b);
        assertTrue(a.getDefaultFieldList().size() == 1);
        assertTrue(a.getDefaultFieldList().get(0).getOperations().size() == 1);
        assertTrue(b.getDefaultFieldList().size() == 1);
        assertTrue(b.getDefaultFieldList().get(0).getOperations().size() == 1);
        
        b.getDefaultFieldList().get(0).clearOperations();
        
        assertFalse(a.equals(b));
        assertTrue(a.getDefaultFieldList().size() == 1);
        assertTrue(a.getDefaultFieldList().get(0).getOperations().size() == 1);
        assertTrue(b.getDefaultFieldList().size() == 1);
        assertTrue(b.getDefaultFieldList().get(0).getOperations().isEmpty());
        
    }


}
