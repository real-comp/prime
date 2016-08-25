package com.realcomp.data.record.io;

import com.realcomp.data.conversion.Concat;
import com.realcomp.data.schema.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 *
 * @author krenfro
 */
public class ParsePlanTest{

    @Test
    public void testSimplePlan() throws ParsePlanException{

        FieldList simple = new FieldList();
        simple.add(new Field("a"));
        simple.add(new Field("b"));
        simple.add(new Field("c"));

        List<Field> expected = new ArrayList<Field>();
        expected.addAll(simple);

        assertEquals(expected, new ParsePlan(simple));
    }

    @Test
    public void testComplexPlan() throws ParsePlanException{

        Field a = new Field("a");
        Field z = new Field("z");
        z.addOperation(new Concat(Arrays.asList(new String[]{"b", "c"})));
        Field b = new Field("b");
        Field c = new Field("c");

        FieldList complex = new FieldList();
        complex.add(a);
        complex.add(z);
        complex.add(b);
        complex.add(c);

        List<Field> expected = new ArrayList<Field>();
        expected.add(a);
        expected.add(b);
        expected.add(c);
        expected.add(z); //multi-field parsed last

        assertEquals(expected, new ParsePlan(complex));
    }

    @Test
    public void testImpossiblePlan() throws ParsePlanException{

        Field a = new Field("a");
        Field z = new Field("z");
        z.addOperation(new Concat(Arrays.asList(new String[]{"b", "c"}))); //z and b depend on each other
        Field b = new Field("b");
        b.addOperation(new Concat(Arrays.asList(new String[]{"z", "c"}))); //z and b depend on each other
        Field c = new Field("c");

        FieldList impossible = new FieldList();
        impossible.add(a);
        impossible.add(z);
        impossible.add(b);
        impossible.add(c);


        try{
            new ParsePlan(impossible);
            fail("Should have trown ParsePlanException");
        }
        catch (ParsePlanException expected){
        }
    }



    @Test
    public void testAnotherPlan() throws ParsePlanException{

        Schema inputSchema = SchemaFactory.buildSchema(
                SchemaTest.class.getResourceAsStream("parse-plan-input.schema"));
        Schema outputSchema = SchemaFactory.buildSchema(
                SchemaTest.class.getResourceAsStream("parse-plan-output.schema"));

        FieldList inputFieldList = inputSchema.getDefaultFieldList();

        FieldList simple = new FieldList();
        simple.add(new Field("a"));
        simple.add(new Field("b"));
        simple.add(new Field("c"));

        List<Field> expected = new ArrayList<Field>();
        expected.addAll(simple);

        assertEquals(expected, new ParsePlan(simple));
    }


}
