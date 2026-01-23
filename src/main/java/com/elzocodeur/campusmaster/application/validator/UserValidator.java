package com.elzocodeur.campusmaster.application.validator;

import com.elzocodeur.campusmaster.application.dto.request.CreateUserRequest;
import com.elzocodeur.campusmaster.domain.exception.business.ValidationException;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.UserRepository;
import com.elzocodeur.campusmaster.shared.constant.AppConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(AppConstants.Validation.EMAIL_REGEX);
    private static final Pattern PHONE_PATTERN = Pattern.compile(AppConstants.Validation.PHONE_REGEX);

    public void validateCreateUser(CreateUserRequest request) {
        Map<String, String> errors = new HashMap<>();

        if (userRepository.existsByEmail(request.getEmail())) {
            errors.put("email", "Email already exists");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            errors.put("username", "Username already exists");
        }

        if (!EMAIL_PATTERN.matcher(request.getEmail()).matches()) {
            errors.put("email", "Invalid email format");
        }

        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isEmpty()) {
            if (!PHONE_PATTERN.matcher(request.getPhoneNumber()).matches()) {
                errors.put("phoneNumber", "Invalid phone number format");
            }
        }

        if (!isPasswordStrong(request.getPassword())) {
            errors.put("password",
                    "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    public void validateUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new ValidationException("userId", "User ID must be a positive number");
        }
    }

    private boolean isPasswordStrong(String password) {
        if (password == null || password.length() < AppConstants.Validation.MIN_PASSWORD_LENGTH) {
            return false;
        }

        boolean hasUpperCase = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLowerCase = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecialChar = password.chars().anyMatch(ch -> !Character.isLetterOrDigit(ch));

        return hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar;
    }
}
