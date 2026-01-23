package com.elzocodeur.campusmaster.application.usecase;

import com.elzocodeur.campusmaster.application.dto.request.CreateUserRequest;
import com.elzocodeur.campusmaster.application.dto.request.UpdateUserRequest;
import com.elzocodeur.campusmaster.application.dto.response.PageResponse;
import com.elzocodeur.campusmaster.application.dto.response.UserResponse;
import com.elzocodeur.campusmaster.domain.enums.UserRole;
import com.elzocodeur.campusmaster.domain.enums.UserStatus;

public interface UserService {

    UserResponse createUser(CreateUserRequest request);

    UserResponse getUserById(Long id);

    UserResponse getUserByEmail(String email);

    UserResponse getUserByUsername(String username);

    UserResponse updateUser(Long id, UpdateUserRequest request);

    void deleteUser(Long id);

    PageResponse<UserResponse> getAllUsers(int page, int size, String sortBy, String sortDirection);

    PageResponse<UserResponse> getUsersByRole(UserRole role, int page, int size);

    PageResponse<UserResponse> getUsersByStatus(UserStatus status, int page, int size);

    PageResponse<UserResponse> searchUsers(String keyword, int page, int size);

    long countUsersByRole(UserRole role);

    long countUsersByStatus(UserStatus status);

    void activateUser(Long id);

    void deactivateUser(Long id);

    void suspendUser(Long id);
}
