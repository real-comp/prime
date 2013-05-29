package com.realcomp.data.validation.field;

import com.realcomp.data.validation.ValidationException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krenfro
 */
public class RequiredValidatorTest{

    public RequiredValidatorTest(){
    }

    /**
     * Test of validate method, of class RequiredValidator.
     */
    @Test
    public void testValidate() throws ValidationException{
        RequiredValidator validator = new RequiredValidator();
        validator.validate("a");

        try{
            validator.validate("");
            fail("should have thrown exception");
        }
        catch (ValidationException expected){
        }
    }

    @Test(expected = ValidationException.class)
    public void testNull() throws ValidationException{
        RequiredValidator validator = new RequiredValidator();
        validator.validate(null);
    }
}