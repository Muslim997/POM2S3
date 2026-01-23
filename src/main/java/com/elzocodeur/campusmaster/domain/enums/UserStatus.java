package com.elzocodeur.campusmaster.domain.enums;

import lombok.Getter;

@Getter
public enum UserStatus {
    ACTIVE("Active", "User account is active"),
    INACTIVE("Inactive", "User account is inactive"),
    SUSPENDED("Suspended", "User account is suspended"),
    PENDING("Pending", "User account is pending approval");

    private final String displayName;
    private final String description;

    UserStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}
