package com.elzocodeur.campusmaster.web.controller;

import com.elzocodeur.campusmaster.application.dto.request.CreateUserRequest;
import com.elzocodeur.campusmaster.application.dto.request.UpdateUserRequest;
import com.elzocodeur.campusmaster.application.dto.response.ApiResponse;
import com.elzocodeur.campusmaster.application.dto.response.PageResponse;
import com.elzocodeur.campusmaster.application.dto.response.UserResponse;
import com.elzocodeur.campusmaster.application.usecase.UserService;
import com.elzocodeur.campusmaster.domain.enums.UserRole;
import com.elzocodeur.campusmaster.domain.enums.UserStatus;
import com.elzocodeur.campusmaster.shared.constant.AppConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(AppConstants.API.USER_PATH)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody CreateUserRequest request) {
        log.info("REST request to create user: {}", request.getEmail());
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User created successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        log.info("REST request to get user by ID: {}", id);
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByEmail(@PathVariable String email) {
        log.info("REST request to get user by email: {}", email);
        UserResponse response = userService.getUserByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByUsername(@PathVariable String username) {
        log.info("REST request to get user by username: {}", username);
        UserResponse response = userService.getUserByUsername(username);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        log.info("REST request to update user with ID: {}", id);
        UserResponse response = userService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.success("User updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        log.info("REST request to delete user with ID: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = AppConstants.Pagination.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.Pagination.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = AppConstants.Pagination.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(defaultValue = AppConstants.Pagination.DEFAULT_SORT_DIRECTION) String sortDirection) {
        log.info("REST request to get all users - page: {}, size: {}", page, size);
        PageResponse<UserResponse> response = userService.getAllUsers(page, size, sortBy, sortDirection);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getUsersByRole(
            @PathVariable UserRole role,
            @RequestParam(defaultValue = AppConstants.Pagination.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.Pagination.DEFAULT_PAGE_SIZE) int size) {
        log.info("REST request to get users by role: {}", role);
        PageResponse<UserResponse> response = userService.getUsersByRole(role, page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getUsersByStatus(
            @PathVariable UserStatus status,
            @RequestParam(defaultValue = AppConstants.Pagination.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.Pagination.DEFAULT_PAGE_SIZE) int size) {
        log.info("REST request to get users by status: {}", status);
        PageResponse<UserResponse> response = userService.getUsersByStatus(status, page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> searchUsers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = AppConstants.Pagination.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.Pagination.DEFAULT_PAGE_SIZE) int size) {
        log.info("REST request to search users with keyword: {}", keyword);
        PageResponse<UserResponse> response = userService.searchUsers(keyword, page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<Void>> activateUser(@PathVariable Long id) {
        log.info("REST request to activate user with ID: {}", id);
        userService.activateUser(id);
        return ResponseEntity.ok(ApiResponse.success("User activated successfully", null));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<Void>> deactivateUser(@PathVariable Long id) {
        log.info("REST request to deactivate user with ID: {}", id);
        userService.deactivateUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deactivated successfully", null));
    }

    @PatchMapping("/{id}/suspend")
    public ResponseEntity<ApiResponse<Void>> suspendUser(@PathVariable Long id) {
        log.info("REST request to suspend user with ID: {}", id);
        userService.suspendUser(id);
        return ResponseEntity.ok(ApiResponse.success("User suspended successfully", null));
    }

    @GetMapping("/count/role/{role}")
    public ResponseEntity<ApiResponse<Long>> countUsersByRole(@PathVariable UserRole role) {
        log.info("REST request to count users by role: {}", role);
        long count = userService.countUsersByRole(role);
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @GetMapping("/count/status/{status}")
    public ResponseEntity<ApiResponse<Long>> countUsersByStatus(@PathVariable UserStatus status) {
        log.info("REST request to count users by status: {}", status);
        long count = userService.countUsersByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}
