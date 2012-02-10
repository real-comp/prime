package com.realcomp.data.schema;

import com.realcomp.data.conversion.Trim;
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
