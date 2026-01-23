package com.elzocodeur.campusmaster.application.dto.user;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    private String email;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private String status;

    private Set<Long> roleIds;
}
