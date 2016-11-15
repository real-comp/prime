package com.realcomp.prime.validation;

/**
 * Some validations may be more critical than others.
 * This severity level allows ValidationExceptions to be thrown with an indicator of
 * how severe the problem is. Applications may choose to handle this severity in different ways.
 *
 */
public enum Severity{

    LOW, MEDIUM, HIGH; //Note: the ordinal order of these is important!

    public static Severity getDefault(){
        return MEDIUM;
    }
}
