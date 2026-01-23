package com.elzocodeur.campusmaster.domain.exception.business;

import com.elzocodeur.campusmaster.domain.exception.BusinessException;

public class ResourceAlreadyExistsException extends BusinessException {

    public ResourceAlreadyExistsException(String resourceName, String fieldName, Object fieldValue) {
        super(
            String.format("%s already exists with %s: '%s'", resourceName, fieldName, fieldValue),
            "RESOURCE_ALREADY_EXISTS"
        );
    }

    public ResourceAlreadyExistsException(String message) {
        super(message, "RESOURCE_ALREADY_EXISTS");
    }
}
