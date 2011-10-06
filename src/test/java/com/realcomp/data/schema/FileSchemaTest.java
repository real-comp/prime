package com.realcomp.data.schema;

import com.realcomp.data.conversion.Trim;
import com.realcomp.data.DataType;
import com.realcomp.data.Operation;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krenfro
 */
public class FileSchemaTest {
    
    public FileSchemaTest() {
    }




    @Test
    public void testCopyConstructor() throws SchemaException{
        
        
        FileSchema a = new FileSchema();
        Field original = new Field("original");
        original.addOperation(new Trim());        
        a.addField(original);
        
        FileSchema b = new FileSchema(a);
        
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
        assertTrue(b.getDefaultFieldList().get(0).getOperations().size() == 0);
        
    }


}
