package com.realcomp.prime.validation.field;

import com.realcomp.prime.validation.ValidationException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krenfro
 */
public class LongRangeValidatorTest {

    public LongRangeValidatorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of validate method, of class LongRangeValidator.
     */
    @Test
    public void testValidate() throws ValidationException{

        LongRangeValidator validator = new LongRangeValidator();
        validator.validate("0");
        validator.validate("00000");
        validator.validate("100000");
        validator.validate("-100000");        
        validator.validate("");

        validator.setMin(0);
        validator.validate("0");

        try{
            validator.validate("-1");
            fail("should have thrown exception");
        }
        catch(ValidationException ok){}

        validator.validate("1");
        validator.setMax(10);
        validator.validate("0");

        try{
            validator.validate("-1");
            fail("should have thrown exception");
        }
        catch(ValidationException ok){}

        validator.validate("1");
        validator.validate("10");

        try{
            validator.validate("11");
            fail("should have thrown exception");
        }
        catch(ValidationException ok){}
        
        
        validator.setMin(1);
        
        try{
            validator.validate("");
            fail("should have thrown exception");
        }
        catch(ValidationException ok){}

        
    }

    @Test(expected=ValidationException.class)
    public void testOutOfOrder() throws ValidationException{
        
        LongRangeValidator validator = new LongRangeValidator();
        validator.setMin(100);
        validator.setMax(99);
        validator.validate("100");

    }

    /**
     * Test of parseLong method, of class LongRangeValidator.
     */
    @Test
    public void testParseLong(){
        LongRangeValidator validator = new LongRangeValidator();
        assertEquals(0, validator.parseLong("0"));
        assertEquals(0, validator.parseLong("00"));
        assertEquals(1, validator.parseLong("1"));
        assertEquals(-1, validator.parseLong("-1"));
    }

    @Test(expected=NumberFormatException.class)
    public void testParseInvalidLong(){
        LongRangeValidator validator = new LongRangeValidator();
        validator.parseLong("");
    }

}