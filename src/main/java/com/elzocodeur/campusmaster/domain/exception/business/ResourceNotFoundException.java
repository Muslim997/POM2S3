package com.elzocodeur.campusmaster.domain.exception.business;

import com.elzocodeur.campusmaster.domain.exception.BusinessException;

public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(
            String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue),
            "RESOURCE_NOT_FOUND"
        );
    }

    public ResourceNotFoundException(String message) {
        super(message, "RESOURCE_NOT_FOUND");
    }

    public ResourceNotFoundException(String resourceName, Long id) {
        super(
            String.format("%s non trouvé(e) avec l'ID : %d", resourceName, id),
            "RESOURCE_NOT_FOUND"
        );
    }
}
