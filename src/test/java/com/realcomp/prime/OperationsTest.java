package com.realcomp.prime;

import com.realcomp.prime.conversion.Trim;
import com.realcomp.prime.schema.AfterLastField;
import com.realcomp.prime.schema.BeforeFirstField;
import com.realcomp.prime.schema.Field;
import com.realcomp.prime.schema.Schema;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 */
public class OperationsTest{

    public OperationsTest(){
    }

    @Test
    public void getOperations(){

        Schema schema = new Schema();
        Field field = new Field("a field");

        assertTrue(Operations.getOperations(schema, field).isEmpty());

        field.addOperation(new Trim());
        assertEquals(1, Operations.getOperations(schema, field).size());

        //before first operations are not performed on the field, and not included in the operations
        schema.addBeforeFirstOperation(new Trim());
        assertEquals(1, Operations.getOperations(schema, field).size());

        schema.addBeforeOperation(new Trim());
        assertEquals(2, Operations.getOperations(schema, field).size());

        // after last operations are not performed on the field, and not included in the operations
        schema.addAfterLastOperation(new Trim());
        assertEquals(2, Operations.getOperations(schema, field).size());


        schema.addAfterOperation(new Trim());
        assertEquals(3, Operations.getOperations(schema, field).size());
    }

    @Test
    public void getBeforeFirst(){
        Schema schema = new Schema();
        Field field = new BeforeFirstField();

        assertTrue(Operations.getOperations(schema, field).isEmpty());

        field.addOperation(new Trim());
        assertEquals(1, Operations.getOperations(schema, field).size());

        schema.addBeforeFirstOperation(new Trim());
        assertEquals(2, Operations.getOperations(schema, field).size());
    }

    @Test
    public void getAfterLast(){
        Schema schema = new Schema();
        Field field = new AfterLastField();

        assertTrue(Operations.getOperations(schema, field).isEmpty());

        field.addOperation(new Trim());
        assertEquals(1, Operations.getOperations(schema, field).size());

        schema.addAfterLastOperation(new Trim());
        assertEquals(2, Operations.getOperations(schema, field).size());
    }

    @Test
    public void illegalArguments(){

        try{
            Operations.getOperations(null, new Field());
            fail("should have throws IAE");
        }
        catch (IllegalArgumentException expected){
        }

        try{
            Operations.getOperations(new Schema(), null);
            fail("should have thrown IAE");
        }
        catch (IllegalArgumentException expected){
        }

    }

    @Test
    public void contructor(){
        //dumb test to get code coverages
        assertNotNull(new Operations());
    }
}