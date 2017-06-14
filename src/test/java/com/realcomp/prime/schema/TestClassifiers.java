package com.realcomp.prime.schema;

import org.junit.Test;

import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 *
 */
public class TestClassifiers{

    @Test
    public void testPatterns(){
        Pattern a = Pattern.compile(".*");
        Pattern b = Pattern.compile(".*");
        assertFalse(a.equals(b)); //surprisingly, this is false
        assertEquals(a.toString(), b.toString());
    }

    @Test
    public void testDefaultClassifier(){

        assertTrue(Schema.DEFAULT_CLASSIFIER.matcher("asdf").matches());
        assertTrue(Pattern.compile("(.){4}").matcher("asdf").matches());
        assertFalse(Pattern.compile("(.){5}").matcher("asdf").matches());

    }



    @Test
    public void testOdysseyOwnerSchema() throws SchemaException{

        Schema schema = SchemaFactory.buildSchema(
                XStreamFileSchemaTest.class.getResourceAsStream("classifier.schema"));

        assertEquals(4, schema.getFieldLists().size());
        FieldList f = schema.getFieldLists().get(1);
        assertNotNull(f.getClassifier());
    }
}
