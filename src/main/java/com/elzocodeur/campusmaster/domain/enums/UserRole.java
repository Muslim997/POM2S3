package com.elzocodeur.campusmaster.domain.enums;

import lombok.Getter;

@Getter
public enum UserRole {
    ADMIN("Admin", "Administrator with full access"),
    PROFESSOR("Professor", "Teaching staff member"),
    STUDENT("Student", "Enrolled student"),
    STAFF("Staff", "Administrative staff member");

    private final String displayName;
    private final String description;

    UserRole(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}
