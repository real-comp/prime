package com.realcomp.prime.validation;

import com.realcomp.prime.Operation;

/**
 *
 * @author krenfro
 */
public interface Validator extends Operation{

    static final Severity DEFAULT_SEVERITY = Severity.MEDIUM;

    /**
     * Set the severity of this Validator.
     *
     * @param severity ignored if null.
     */
    void setSeverity(Severity severity);

    /**
     *
     * @return the Severity level of this Validator. (Default: MEDIUM)
     */
    Severity getSeverity();

    /**
     * Validates the provided value; throwing a ValidationException if the value is not valid.
     *
     * @param value to be validated. not null.
     * @throws ValidationException if the provided value is not valid.
     */
    void validate(Object value) throws ValidationException;
}
