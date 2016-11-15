package com.realcomp.prime.validation.field;

import com.realcomp.prime.validation.ValidationException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 */
public class ValidateLengthTest{

    public ValidateLengthTest(){
    }

    @Test
    public void testValidator() throws ValidationException{

        LengthValidator validator = new LengthValidator();
        validator.validate("");
        validator.validate("asdf asdf");
        validator.setMin(1);

        try{
            validator.validate("");
            fail("should have thrown exception");
        }
        catch (ValidationException expected){
        }

        validator.validate("a");
        validator.validate("asdf");
        validator.setMax(4);

        try{
            validator.validate("");
            fail("should have thrown exception");
        }
        catch (ValidationException expected){
        }


        validator.validate("a");
        validator.validate("asdf");
        try{
            validator.validate("asdfa");
            fail("should have thrown exception");
        }
        catch (ValidationException expected){
        }

        try{
            validator.validate(" asdf");
            fail("should have thrown exception");
        }
        catch (ValidationException expected){
        }



    }

    @Test(expected = ValidationException.class)
    public void testNull() throws ValidationException{
        LengthValidator validator = new LengthValidator();
        validator.validate(null);
    }
}