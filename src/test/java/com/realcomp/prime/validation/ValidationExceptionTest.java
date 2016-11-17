package com.realcomp.prime.validation;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 */
public class ValidationExceptionTest{

    /**
     * Handy test method that asserts that the specified validation will throw
     * a ValidationException. If it doesn't, an AssertionError is thrown.
     *
     * @param validator
     * @param test
     */
    public static void assertException(Validator validator, String test){
        try{
            validator.validate(test);
            throw new AssertionError("ValidationException should have been thrown.");
        }
        catch (ValidationException expected){
        }
    }

    @Test
    public void testSeverity(){
        ValidationException ex = new ValidationException.Builder().build();
        assertEquals(Severity.getDefault(), ex.getSeverity());
        ex = new ValidationException.Builder().severity(Severity.HIGH).build();
        assertEquals(Severity.HIGH, ex.getSeverity());
    }
}
