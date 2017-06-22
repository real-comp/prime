package com.realcomp.prime.validation;


import java.util.Optional;

/**
 * <p>Validation failure reading raw data into a Record.</p>
 * Validation problem information along with the raw data that had the issue.
 */
public class RawValidationException extends ValidationException{

    private Optional<String> raw;

    public RawValidationException(ValidationException original, String raw){
        super(original);
        this.raw = Optional.ofNullable(raw);
    }

    public Optional<String> getRaw() {return raw; }

    public void setRaw(String raw){
        this.raw = Optional.ofNullable(raw);
    }
}
