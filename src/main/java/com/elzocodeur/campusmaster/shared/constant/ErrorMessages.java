package com.elzocodeur.campusmaster.shared.constant;

public final class ErrorMessages {

    private ErrorMessages() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static final String RESOURCE_NOT_FOUND = "Resource not found with %s: %s";
    public static final String RESOURCE_ALREADY_EXISTS = "Resource already exists with %s: %s";
    public static final String INVALID_INPUT = "Invalid input provided: %s";
    public static final String UNAUTHORIZED_ACCESS = "Unauthorized access to resource";
    public static final String FORBIDDEN_ACCESS = "Access forbidden for this resource";
    public static final String INTERNAL_SERVER_ERROR = "An internal server error occurred";
    public static final String VALIDATION_FAILED = "Validation failed for field: %s";
    public static final String INVALID_CREDENTIALS = "Invalid username or password";
    public static final String ACCOUNT_DISABLED = "Account is disabled";
    public static final String ACCOUNT_LOCKED = "Account is locked";
    public static final String TOKEN_EXPIRED = "Authentication token has expired";
    public static final String TOKEN_INVALID = "Invalid authentication token";
    public static final String EMAIL_ALREADY_EXISTS = "Email already exists";
    public static final String USERNAME_ALREADY_EXISTS = "Username already exists";
}
