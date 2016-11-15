/*
 */
package com.realcomp.prime.validation.field;

import com.realcomp.prime.validation.ValidationException;
import org.junit.Test;

/**
 *
 * @author krenfro
 */
public class ZipcodeValidatorTest{

    public ZipcodeValidatorTest(){
    }

    @Test
    public void testValidate() throws ValidationException{

        ZipcodeValidator instance = new ZipcodeValidator();
        instance.validate("12345");
        instance.validate("12345-6789");
    }

    @Test(expected = ValidationException.class)
    public void testValidateNull() throws ValidationException{
        ZipcodeValidator instance = new ZipcodeValidator();
        instance.validate(null);
    }

    @Test(expected = ValidationException.class)
    public void testValidateEmptyString() throws ValidationException{
        ZipcodeValidator instance = new ZipcodeValidator();
        instance.validate("");
    }

    @Test(expected = ValidationException.class)
    public void testValidateBadZip1() throws ValidationException{
        ZipcodeValidator instance = new ZipcodeValidator();
        instance.validate("1");
    }

    @Test(expected = ValidationException.class)
    public void testValidateBadZip2() throws ValidationException{
        ZipcodeValidator instance = new ZipcodeValidator();
        instance.validate("abcde");
    }

    @Test(expected = ValidationException.class)
    public void testValidateBadZip3() throws ValidationException{
        ZipcodeValidator instance = new ZipcodeValidator();
        instance.validate("a1234");
    }

    @Test(expected = ValidationException.class)
    public void testValidateBadZip4() throws ValidationException{
        ZipcodeValidator instance = new ZipcodeValidator();
        instance.validate("12345-");
    }

    @Test(expected = ValidationException.class)
    public void testValidateBadZip5() throws ValidationException{
        ZipcodeValidator instance = new ZipcodeValidator();
        instance.validate("12345-1");
    }

    @Test(expected = ValidationException.class)
    public void testValidateBadZip6() throws ValidationException{
        ZipcodeValidator instance = new ZipcodeValidator();
        instance.validate("123456");
    }

    @Test(expected = ValidationException.class)
    public void testValidateBadZip7() throws ValidationException{
        ZipcodeValidator instance = new ZipcodeValidator();
        instance.validate("12345-67890");
    }
}