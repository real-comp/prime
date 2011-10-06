package com.realcomp.data.schema;

import org.junit.Test;
import java.util.regex.Pattern;
import static org.junit.Assert.*;


/**
 *
 * @author krenfro
 */
public class TestClassifierEquality {
 
    
    @Test
    public void testPatterns(){
        Pattern a = Pattern.compile(".*");
        Pattern b = Pattern.compile(".*");
        assertFalse(a.equals(b)); //surprisingly, this is false
        assertEquals(a.toString(), b.toString());
    }
}
