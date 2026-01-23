package com.elzocodeur.campusmaster.shared.constant;

public final class AppConstants {

    private AppConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static final class API {
        public static final String API_VERSION = "/api/v1";
        public static final String AUTH_PATH = API_VERSION + "/auth";
        public static final String USER_PATH = API_VERSION + "/users";

        private API() {
            throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
        }
    }

    public static final class Security {
        public static final String TOKEN_PREFIX = "Bearer ";
        public static final String HEADER_STRING = "Authorization";
        public static final long EXPIRATION_TIME = 864_000_000; // 10 jours

        private Security() {
            throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
        }
    }

    public static final class Pagination {
        public static final String DEFAULT_PAGE_NUMBER = "0";
        public static final String DEFAULT_PAGE_SIZE = "20";
        public static final String DEFAULT_SORT_BY = "id";
        public static final String DEFAULT_SORT_DIRECTION = "ASC";
        public static final int MAX_PAGE_SIZE = 100;

        private Pagination() {
            throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
        }
    }

    public static final class Validation {
        public static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        public static final String PHONE_REGEX = "^\\+?[0-9]{10,15}$";
        public static final int MIN_PASSWORD_LENGTH = 8;
        public static final int MAX_PASSWORD_LENGTH = 100;

        private Validation() {
            throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
        }
    }
}
