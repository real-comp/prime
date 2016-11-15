package com.realcomp.prime.record;

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
public class RecordValueAssemblerTest{

    public RecordValueAssemblerTest(){
    }

    @Test
    public void testAssembly() throws RecordValueException{

        Map<String, Object> data = new HashMap<>();

        RecordValueAssembler.assemble(data, "name", "real-comp");
        assertEquals("real-comp", data.get("name"));

        RecordValueAssembler.assemble(data, "prop.imp_info.sqft", 1000);
        assertEquals(1000, RecordValueResolver.resolve(data, "prop.imp_info.sqft"));

        Record record = new Record();
        RecordValueAssembler.assemble(record, "name", "real-comp");
        assertEquals("real-comp", record.get("name"));
        //assertEquals("real-comp", record.getAll("name").get(0));

    }

    @Test
    public void testIndexedAssembly() throws RecordValueException{

        Map<String, Object> data = new HashMap<>();

        RecordValueAssembler.assemble(data, "name", "real-comp");
        assertEquals("real-comp", data.get("name"));

        RecordValueAssembler.assemble(data, "prop[0].imp_info[0].sqft", 1000);
        assertEquals(1000, RecordValueResolver.resolve(data, "prop.imp_info.sqft"));
        assertEquals(1000, RecordValueResolver.resolve(data, "prop[0].imp_info[0].sqft"));

    }

    @Test
    public void testWriteListIntoMissingKey() throws RecordValueException{

        Map<String, Object> data = new HashMap<>();
        List<Object> list = new ArrayList();
        list.add(1000);
        list.add(2000);

        //this works because there is currently no value for the key
        RecordValueAssembler.assemble(data, "prop.imp_info.sqft", list);

        assertEquals(1000, ((List) RecordValueResolver.resolve(data, "prop.imp_info.sqft")).get(0));
        assertEquals(1000, ((List) RecordValueResolver.resolve(data, "prop.imp_info[0].sqft")).get(0));

        assertEquals(2000, ((List) RecordValueResolver.resolve(data, "prop.imp_info.sqft")).get(1));

        //imp_info[1] does not exist. this should return null
        assertEquals(null, RecordValueResolver.resolve(data, "prop.imp_info[1].sqft"));


    }

    @Test
    public void testAssemblyOverwrite() throws RecordValueException{

        Map<String, Object> data = new HashMap<>();
        List<Object> list = new ArrayList();
        list.add(1000);
        list.add(2000);

        RecordValueAssembler.assemble(data, "prop.imp_info.sqft", "2");
        assertEquals("2", RecordValueResolver.resolve(data, "prop.imp_info.sqft"));

        RecordValueAssembler.assemble(data, "prop.imp_info.sqft", list);
        assertEquals(1000, ((List) RecordValueResolver.resolve(data, "prop.imp_info.sqft")).get(0));

        RecordValueAssembler.assemble(data, "prop.imp_info", null);
        assertEquals(null, RecordValueResolver.resolve(data, "prop.imp_info"));
        assertEquals(null, RecordValueResolver.resolve(data, "prop.imp_info.sqft"));

    }

    @Test
    public void testAssemblyFailureValueExists() throws RecordValueException{
        Map<String, Object> data = new HashMap<>();
        List<Object> list = new ArrayList();
        list.add(1000);
        list.add(2000);

        //this works because there is currently no value for the key
        RecordValueAssembler.assemble(data, "prop.imp_info.sqft", list);

        try{
            RecordValueAssembler.assemble(data, "prop.imp_info.sqft.stuff", "asdf");
            fail("should have throws RecordValueException");
        }
        catch (RecordValueException expected){
            System.out.println(expected.getMessage());
        }


    }

    @Test
    public void testAssemblyFailureForNonStandardRecord() throws RecordValueException{

        Map<String, Object> data = new HashMap<>();
        List<Object> list = new ArrayList();
        list.add(1000);
        list.add(2000);

        //this works because there is currently no value for the key
        RecordValueAssembler.assemble(data, "prop.imp_info.sqft", list);

        try{
            RecordValueAssembler.assemble(data, "prop.imp_info[0].sqft.stuff", "asdf");
            fail("should have throws RecordValueException");
        }
        catch (RecordValueException expected){
            System.out.println(expected.getMessage());
        }

        data = new HashMap<String, Object>();
        RecordValueAssembler.assemble(data, "prop.imp_info", list);
        data.put("prop", list);

        try{
            RecordValueAssembler.assemble(data, "prop.imp_info.sqft", list);
            fail("should have throws RecordValueException");
        }
        catch (RecordValueException expected){
            System.out.println(expected.getMessage());
        }
    }

    @Test
    public void testAssemblyFailureForNonStandardRecord2() throws RecordValueException{

        Map<String, Object> data = new HashMap<>();
        RecordValueAssembler.assemble(data, "prop.land[0].sqft", 1000);
        RecordValueAssembler.assemble(data, "prop.land[1].sqft", 1000);
        RecordValueAssembler.assemble(data, "prop.land[2].sqft", 1000);

        try{
            RecordValueAssembler.assemble(data, "prop.land.sqft.wakawaka", "asdf");
            fail("should have throws RecordValueException");
        }
        catch (RecordValueException expected){
            System.out.println(expected.getMessage());
        }
    }

    @Test
    public void testNullParameters() throws RecordValueException{

        Map<String, Object> data = new HashMap<>();

        try{
            RecordValueAssembler.assemble(data, (String) null, "real-comp");
            fail("should have thrown IAE");
        }
        catch (IllegalArgumentException expected){
        }

        try{
            RecordValueAssembler.assemble((Map) null, "name", "real-comp");
            fail("should have thrown IAE");
        }
        catch (IllegalArgumentException expected){
        }

        try{
            RecordValueAssembler.assemble(data, (String) null, new ArrayList());
            fail("should have thrown IAE");
        }
        catch (IllegalArgumentException expected){
        }

        try{
            RecordValueAssembler.assemble((Map) null, "name", new ArrayList());
            fail("should have thrown IAE");
        }
        catch (IllegalArgumentException expected){
        }



        Record record = new Record();

        try{
            RecordValueAssembler.assemble(record, (String) null, "real-comp");
            fail("should have thrown IAE");
        }
        catch (IllegalArgumentException expected){
        }

        try{
            RecordValueAssembler.assemble((Record) null, "name", "real-comp");
            fail("should have thrown IAE");
        }
        catch (IllegalArgumentException expected){
        }

        try{
            RecordValueAssembler.assemble(record, (String) null, new ArrayList());
            fail("should have thrown IAE");
        }
        catch (IllegalArgumentException expected){
        }

        try{
            RecordValueAssembler.assemble((Record) null, "name", new ArrayList());
            fail("should have thrown IAE");
        }
        catch (IllegalArgumentException expected){
        }

    }
}
