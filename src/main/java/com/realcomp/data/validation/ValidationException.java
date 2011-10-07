package com.realcomp.data.validation;

/**
 * <p>An exception thrown when validation fails.</p>
 *
 * An exception should be thrown by a Validator with information about why the validation
 * failed.
 * Note:
 * It would be helpful to know, for example, what record had the validation problem.
 * A Validator will typically not know this, so the ValidationException can be thrown and caught
 * by an object that does know, and can handle the problem.
 *
 *
 * @author krenfro
 */
public class ValidationException extends Exception {

    private static final long serialVersionUID = -6194444803112488818L;

    private Severity severity = Severity.MEDIUM;

    public ValidationException() {
        super();
    }

    public ValidationException(String message, String value){
        this(buildMessage(message, value));
    }

    public ValidationException(String message, String value, Severity severity){
        this(message, value);
        if (severity == null)
            throw new IllegalArgumentException("severity is null");
        
        this.severity = severity;
    }

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidationException(Throwable cause) {
        super(cause);
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    private static String buildMessage(String message, String value){
        if (value == null)
            return message;
        else if (value.length() <= 30)
            return String.format("%s [%s]", message, value);
        else
            return String.format("%s [%s...]", message, value.substring(0,30));
    }

}
