
package com.realcomp.data.validation.field;

import com.realcomp.data.validation.Severity;
import com.realcomp.data.validation.Validator;

/**
 * Base implementation of a Validator that handles the severity level
 *
 * @author krenfro
 */
public abstract class BaseFieldValidator implements FieldValidator {

    protected Severity severity = Validator.DEFAULT_SEVERITY;


    @Override
    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    @Override
    public Severity getSeverity() {
        return severity;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final BaseFieldValidator other = (BaseFieldValidator) obj;
        if (this.severity != other.severity)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + (this.severity != null ? this.severity.hashCode() : 0);
        return hash;
    }

}
