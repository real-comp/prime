package com.realcomp.prime.schema;

import org.junit.Test;
import java.util.regex.Pattern;
import static org.junit.Assert.*;

/**
 *
 */
public class TestClassifierEquality{

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
}
