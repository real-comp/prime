/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcomp.data.schema;

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
public class SchemaFieldTest {
    
    public SchemaFieldTest() {
    }




    /**
     * Test of checkName method, of class SchemaField.
     */
    @Test
    public void testCheckName() {
        SchemaField instance = new SchemaField();
        instance.checkName("asdf");
        
        
        try{
            instance.checkName(null);
            fail("should have thrown IllegalArgumentException");
        }
        catch(IllegalArgumentException expected){}
        
        try{
            instance.checkName("");
            fail("should have thrown IllegalArgumentException");
        }
        catch(IllegalArgumentException expected){}
        
        try{
            instance.checkName("asdf.asdf");
            fail("should have thrown IllegalArgumentException");
        }
        catch(IllegalArgumentException expected){}
        
        try{
            instance.checkName("asdf[");
            fail("should have thrown IllegalArgumentException");
        }
        catch(IllegalArgumentException expected){}
        
        try{
            instance.checkName("asdf]");
            fail("should have thrown IllegalArgumentException");
        }
        catch(IllegalArgumentException expected){}

    }

}
