/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcomp.data.record;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krenfro
 */
public class RecordValueAssemblerTest {
    
    public RecordValueAssemblerTest() {
    }

    @Test
    public void testAssembly() throws RecordValueException, RecordKeyException{
 
        Map<String,Object> data = new HashMap<String,Object>();
        
        RecordValueAssembler.assemble(data, "name", "real-comp");
        assertEquals("real-comp", data.get("name"));
                
        RecordValueAssembler.assemble(data, "prop.imp_info.sqft", 1000);
        assertEquals(1000, RecordValueResolver.resolve(data, "prop.imp_info.sqft").get(0));
        
        Record record = new Record();
        RecordValueAssembler.assemble(record, "name", "real-comp");
        assertEquals("real-comp", record.get("name"));
        assertEquals("real-comp", record.getAll("name").get(0));
        
    }
    
    @Test
    public void testIndexedAssembly() throws RecordValueException{
 
        Map<String,Object> data = new HashMap<String,Object>();
        
        RecordValueAssembler.assemble(data, "name", "real-comp");
        assertEquals("real-comp", data.get("name"));
                
        RecordValueAssembler.assemble(data, "prop[0].imp_info[0].sqft", 1000);
        assertEquals(1000, RecordValueResolver.resolve(data, "prop.imp_info.sqft").get(0));
        assertEquals(1000, RecordValueResolver.resolve(data, "prop[0].imp_info[0].sqft").get(0));
        
    }
    
    @Test
    public void testWriteListIntoMissingKey() throws RecordValueException{
        
        Map<String,Object> data = new HashMap<String,Object>();
        List<Object> list = new ArrayList();
        list.add(1000);
        list.add(2000);

        //this works because there is currently no value for the key
        RecordValueAssembler.assemble(data, "prop.imp_info.sqft", list);
        
        assertEquals(1000, RecordValueResolver.resolve(data, "prop.imp_info.sqft").get(0));
        assertEquals(1000, RecordValueResolver.resolve(data, "prop.imp_info[0].sqft").get(0));
        
        assertEquals(2000, RecordValueResolver.resolve(data, "prop.imp_info.sqft").get(1));
        assertEquals(2000, RecordValueResolver.resolve(data, "prop.imp_info[1].sqft").get(0));

            
    }
    
    @Test
    public void testAssemblyFailure() throws RecordValueException{
        
        Map<String,Object> data = new HashMap<String,Object>();
        List<Object> list = new ArrayList();
        list.add(1000);
        list.add(2000);
        
        RecordValueAssembler.assemble(data, "prop.imp_info.sqft", "2");
        
        try{
            //this will fail because there is already a value for the key and indexed keys are not used.
            RecordValueAssembler.assemble(data, "prop.imp_info.sqft", list);
            fail("should have thrown RecordValueException");
        }
        catch(RecordValueException expected){
            System.out.println(expected.getMessage());
        }
    }
    
    @Test
    public void testAssemblyFailureValueExists() throws RecordValueException{
        Map<String,Object> data = new HashMap<String,Object>();
        List<Object> list = new ArrayList();
        list.add(1000);
        list.add(2000);

        //this works because there is currently no value for the key
        RecordValueAssembler.assemble(data, "prop.imp_info.sqft", list);
        
        try{
            RecordValueAssembler.assemble(data, "prop.imp_info.sqft.stuff", "asdf");
            fail("should have throws RecordValueException");
        }
        catch(RecordValueException expected){
            System.out.println(expected.getMessage());
        }
        
        
    }
    
    @Test
    public void testAssemblyFailureForNonStandardRecord() throws RecordValueException{
        
        Map<String,Object> data = new HashMap<String,Object>();
        List<Object> list = new ArrayList();
        list.add(1000);
        list.add(2000);

        //this works because there is currently no value for the key
        RecordValueAssembler.assemble(data, "prop.imp_info.sqft", list);
        
        try{
            RecordValueAssembler.assemble(data, "prop.imp_info[0].sqft.stuff", "asdf");
            fail("should have throws RecordValueException");
        }
        catch(RecordValueException expected){
            System.out.println(expected.getMessage());
        }
        
        data = new HashMap<String,Object>();        
        RecordValueAssembler.assemble(data, "prop.imp_info", list);
        data.put("prop", list);
        
        try{
            RecordValueAssembler.assemble(data, "prop.imp_info.sqft", list);
            fail("should have throws RecordValueException");
        }
        catch(RecordValueException expected){
            System.out.println(expected.getMessage());
        }
    }
     
    
    @Test
    public void testAssemblyFailureForNonStandardRecord2() throws RecordValueException{
        
        Map<String,Object> data = new HashMap<String,Object>();        
        RecordValueAssembler.assemble(data, "prop.land[0].sqft", 1000);
        RecordValueAssembler.assemble(data, "prop.land[1].sqft", 1000);
        RecordValueAssembler.assemble(data, "prop.land[2].sqft", 1000);
        
        try{
            RecordValueAssembler.assemble(data, "prop.land.sqft.wakawaka", "asdf");
            fail("should have throws RecordValueException");
        }
        catch(RecordValueException expected){
            System.out.println(expected.getMessage());
        }
    }
    
    
    @Test
    public void testEmptyKeys() throws RecordValueException{
        
        Map<String,Object> data = new HashMap<String,Object>();
        Map<String,Object> copy = new HashMap<String,Object>();
        copy.putAll(data);
        
        RecordValueAssembler.assemble(data, "", "");
        assertEquals(data, copy);
        RecordValueAssembler.assemble(data, "", new ArrayList<Object>());
        assertEquals(data, copy);
        
    }
     
    
    @Test
    public void testNullParameters() throws RecordValueException{
        
        Map<String,Object> data = new HashMap<String,Object>();
        try{
            RecordValueAssembler.assemble(data, "name", (String) null);
            fail("should have thrown IAE");
        }
        catch(IllegalArgumentException expected){}
        
        try{
            RecordValueAssembler.assemble(data, null, "real-comp");
            fail("should have thrown IAE");
        }
        catch(IllegalArgumentException expected){}
        
        try{
            RecordValueAssembler.assemble( (Map) null, "name", "real-comp");
            fail("should have thrown IAE");
        }
        catch(IllegalArgumentException expected){}
        
        try{
            RecordValueAssembler.assemble(data, "name", (List) null);
            fail("should have thrown IAE");
        }
        catch(IllegalArgumentException expected){}
        
        try{
            RecordValueAssembler.assemble(data, null, new ArrayList());
            fail("should have thrown IAE");
        }
        catch(IllegalArgumentException expected){}
        
        try{
            RecordValueAssembler.assemble( (Map) null, "name", new ArrayList());
            fail("should have thrown IAE");
        }
        catch(IllegalArgumentException expected){}
        
        
        
        Record record = new Record();
        
        try{
            RecordValueAssembler.assemble(record, "name", (String) null);
            fail("should have thrown IAE");
        }
        catch(IllegalArgumentException expected){}
        
        try{
            RecordValueAssembler.assemble(record, null, "real-comp");
            fail("should have thrown IAE");
        }
        catch(IllegalArgumentException expected){}
        
        try{
            RecordValueAssembler.assemble( (Record) null, "name", "real-comp");
            fail("should have thrown IAE");
        }
        catch(IllegalArgumentException expected){}
        
         try{
            RecordValueAssembler.assemble(record, "name", (List) null);
            fail("should have thrown IAE");
        }
        catch(IllegalArgumentException expected){}
        
        try{
            RecordValueAssembler.assemble(record, null, new ArrayList());
            fail("should have thrown IAE");
        }
        catch(IllegalArgumentException expected){}
        
        try{
            RecordValueAssembler.assemble( (Record) null, "name", new ArrayList());
            fail("should have thrown IAE");
        }
        catch(IllegalArgumentException expected){}
        
    }
    
}
