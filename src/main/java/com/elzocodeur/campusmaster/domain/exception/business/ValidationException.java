package com.elzocodeur.campusmaster.domain.exception.business;

import com.elzocodeur.campusmaster.domain.exception.BusinessException;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ValidationException extends BusinessException {

    private final Map<String, String> errors;

    public ValidationException(String message) {
        super(message, "VALIDATION_ERROR");
        this.errors = new HashMap<>();
    }

    public ValidationException(Map<String, String> errors) {
        super("Validation failed", "VALIDATION_ERROR");
        this.errors = errors;
    }

    public ValidationException(String field, String error) {
        super("Validation failed", "VALIDATION_ERROR");
        this.errors = new HashMap<>();
        this.errors.put(field, error);
    }
}
