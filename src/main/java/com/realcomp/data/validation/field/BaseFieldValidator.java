
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

}
