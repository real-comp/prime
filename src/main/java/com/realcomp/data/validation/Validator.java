package com.realcomp.data.validation;

import com.realcomp.data.Operation;

/**
 * 
 * @author krenfro
 */
public interface Validator<T> extends Operation{

    static final Severity DEFAULT_SEVERITY = Severity.MEDIUM;

    /**
     * Set the severity of this Validator.
     * @param severity ignored if null.
     */
    void setSeverity(Severity severity);

    /**
     *
     * @return the Severity level of this Validator. (Default: MEDIUM)
     */
    Severity getSeverity();

    void validate(T value) throws ValidationException;
}
