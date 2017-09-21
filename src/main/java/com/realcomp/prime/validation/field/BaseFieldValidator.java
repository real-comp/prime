package com.realcomp.prime.validation.field;

import com.realcomp.prime.validation.Severity;
import com.realcomp.prime.validation.Validator;

/**
 * Base implementation of a Validator that handles the severity level *
 */
public abstract class BaseFieldValidator implements Validator{

    protected Severity severity = Validator.DEFAULT_SEVERITY;

    @Override
    public void setSeverity(Severity severity){
        this.severity = severity;
    }

    @Override
    public Severity getSeverity(){
        return severity;
    }

    @Override
    public boolean equals(Object obj){
        if (obj == null){
            return false;
        }
        if (getClass() != obj.getClass()){
            return false;
        }
        final BaseFieldValidator other = (BaseFieldValidator) obj;
        return this.severity == other.severity;
    }

    @Override
    public int hashCode(){
        int hash = 5;
        hash = 67 * hash + (this.severity != null ? this.severity.hashCode() : 0);
        return hash;
    }
}
