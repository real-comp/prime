package com.realcomp.prime.validation;

import com.google.common.base.Optional;
import com.realcomp.prime.record.Record;

import java.util.Objects;

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
 */
public class ValidationException extends Exception{

    private static final long serialVersionUID = -6194444803112488818L;
    private Severity severity = Severity.MEDIUM;
    private Optional<Record> record;

    private ValidationException(String message, Object value, Record record, Severity severity, Throwable cause){
        super(message, cause);
    }

    public static class Builder{

        private String message;
        private Throwable cause;
        private Severity severity = Severity.MEDIUM;
        private Record record;
        private Object value;

        public Builder(){
        }

        public Builder(ValidationException original){
            message = original.getMessage();
            cause = original.getCause();
            severity = original.getSeverity();
            record = original.getRecord().get();
        }

        public Builder message(String message){
            Objects.requireNonNull(message);
            this.message = message;
            return this;
        }

        public Builder cause(Throwable cause){
            this.cause = cause;

            return this;
        }

        public Builder severity(Severity severity){
            if (severity != null){
                this.severity = severity;
            }
            return this;
        }

        public Builder record(Record record){
            this.record = record;
            return this;
        }

        public Builder value(Object value){
            this.value = value;
            return this;
        }

        public ValidationException build(){
            if (value != null){
                String s = value.toString();
                if (s.length() > 30){
                    s = s.substring(0, 30);
                }
                message = String.format("%s [%s]", message, s);
            }
            return new ValidationException(message, value, record, severity, cause);
        }
    }

    public Severity getSeverity(){
        return severity;
    }

    public Optional<Record> getRecord(){
        return record;
    }

}
