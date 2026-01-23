package com.elzocodeur.campusmaster.service.impl;

import com.elzocodeur.campusmaster.application.dto.request.CreateUserRequest;
import com.elzocodeur.campusmaster.application.dto.request.UpdateUserRequest;
import com.elzocodeur.campusmaster.application.dto.response.PageResponse;
import com.elzocodeur.campusmaster.application.dto.response.UserResponse;
import com.elzocodeur.campusmaster.application.mapper.UserMapper;
import com.elzocodeur.campusmaster.application.usecase.UserService;
import com.elzocodeur.campusmaster.application.validator.UserValidator;
import com.elzocodeur.campusmaster.domain.entity.User;
import com.elzocodeur.campusmaster.domain.enums.UserRole;
import com.elzocodeur.campusmaster.domain.enums.UserStatus;
import com.elzocodeur.campusmaster.domain.exception.business.ResourceNotFoundException;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserValidator userValidator;

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        log.info("Creating new user with email: {}", request.getEmail());

        userValidator.validateCreateUser(request);

        User user = userMapper.toEntity(request);
        User savedUser = userRepository.save(user);

        log.info("User created successfully with ID: {}", savedUser.getId());
        return userMapper.toResponse(savedUser);
    }

    @Override
    public UserResponse getUserById(Long id) {
        log.info("Fetching user by ID: {}", id);

        userValidator.validateUserId(id);

        User user = userRepository.findById(id)
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        log.info("Fetching user by email: {}", email);

        User user = userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse getUserByUsername(String username) {
        log.info("Fetching user by username: {}", username);

        User user = userRepository.findByUsernameAndDeletedFalse(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        log.info("Updating user with ID: {}", id);

        userValidator.validateUserId(id);

        User user = userRepository.findById(id)
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        userMapper.updateEntity(user, request);
        User updatedUser = userRepository.save(user);

        log.info("User updated successfully with ID: {}", updatedUser.getId());
        return userMapper.toResponse(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        log.info("Soft deleting user with ID: {}", id);

        userValidator.validateUserId(id);

        User user = userRepository.findById(id)
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        user.setDeleted(true);
        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);

        log.info("User soft deleted successfully with ID: {}", id);
    }

    @Override
    public PageResponse<UserResponse> getAllUsers(int page, int size, String sortBy, String sortDirection) {
        log.info("Fetching all users - page: {}, size: {}", page, size);

        Sort sort = sortDirection.equalsIgnoreCase("DESC")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<User> userPage = userRepository.findAllActive(pageable);

        return buildPageResponse(userPage);
    }

    @Override
    public PageResponse<UserResponse> getUsersByRole(UserRole role, int page, int size) {
        log.info("Fetching users by role: {} - page: {}, size: {}", role, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findByRole(role, pageable);

        return buildPageResponse(userPage);
    }

    @Override
    public PageResponse<UserResponse> getUsersByStatus(UserStatus status, int page, int size) {
        log.info("Fetching users by status: {} - page: {}, size: {}", status, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findByStatus(status, pageable);

        return buildPageResponse(userPage);
    }

    @Override
    public PageResponse<UserResponse> searchUsers(String keyword, int page, int size) {
        log.info("Searching users with keyword: {} - page: {}, size: {}", keyword, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.searchUsers(keyword, pageable);

        return buildPageResponse(userPage);
    }

    @Override
    public long countUsersByRole(UserRole role) {
        log.info("Counting users by role: {}", role);
        return userRepository.countByRole(role);
    }

    @Override
    public long countUsersByStatus(UserStatus status) {
        log.info("Counting users by status: {}", status);
        return userRepository.countByStatus(status);
    }

    @Override
    @Transactional
    public void activateUser(Long id) {
        log.info("Activating user with ID: {}", id);
        updateUserStatus(id, UserStatus.ACTIVE);
    }

    @Override
    @Transactional
    public void deactivateUser(Long id) {
        log.info("Deactivating user with ID: {}", id);
        updateUserStatus(id, UserStatus.INACTIVE);
    }

    @Override
    @Transactional
    public void suspendUser(Long id) {
        log.info("Suspending user with ID: {}", id);
        updateUserStatus(id, UserStatus.SUSPENDED);
    }

    private void updateUserStatus(Long id, UserStatus status) {
        userValidator.validateUserId(id);

        User user = userRepository.findById(id)
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        user.setStatus(status);
        userRepository.save(user);

        log.info("User status updated to {} for ID: {}", status, id);
    }

    private PageResponse<UserResponse> buildPageResponse(Page<User> userPage) {
        return PageResponse.<UserResponse>builder()
                .content(userMapper.toResponseList(userPage.getContent()))
                .pageNumber(userPage.getNumber())
                .pageSize(userPage.getSize())
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .last(userPage.isLast())
                .first(userPage.isFirst())
                .empty(userPage.isEmpty())
                .build();
    }
}
