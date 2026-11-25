package app.validator;

public final class AuthErrorCodes {
    public static final String USERNAME_ALREADY_EXISTS = "USERNAME_ALREADY_EXISTS";
    public static final String USERNAME_INVALID = "USERNAME_INVALID";
    public static final String INVALID_USERNAME = "INVALID_USERNAME";
    public static final String EMAIL_ALREADY_EXISTS = "EMAIL_ALREADY_EXISTS";
    public static final String EMAIL_INVALID = "EMAIL_INVALID";
    public static final String INVALID_EMAIL = "INVALID_EMAIL";
    public static final String PASSWORD_MISMATCH = "PASSWORD_MISMATCH";
    public static final String INVALID_NICKNAME = "INVALID_NICKNAME";
    public static final String INVALID_PASSWORD = "INVALID_PASSWORD";
    public static final String SECURITY_ANSWER_REQUIRED = "SECURITY_ANSWER_REQUIRED";
    public static final String SECURITY_ANSWER_INCORRECT = "SECURITY_ANSWER_INCORRECT";
    public static final String NOT_AUTHENTICATED = "NOT_AUTHENTICATED";
    public static final String SERVER_UNAVAILABLE = "SERVER_UNAVAILABLE";
    public static final String ACCOUNT_NOT_FOUND = "ACCOUNT_NOT_FOUND";

    private AuthErrorCodes() {
    }

    public static String localize(String errorCode, String fallback) {
        ValidationMessages messages = ValidationMessages.active();
        if (errorCode == null || errorCode.isBlank()) {
            return fallback;
        }
        return switch (errorCode) {
            case USERNAME_ALREADY_EXISTS -> messages.duplicateUsername;
            case USERNAME_INVALID, INVALID_USERNAME -> messages.invalidUsername;
            case EMAIL_ALREADY_EXISTS -> messages.duplicateEmail;
            case EMAIL_INVALID, INVALID_EMAIL -> messages.invalidEmail;
            case PASSWORD_MISMATCH -> messages.passwordMismatch;
            case INVALID_NICKNAME -> messages.invalidNickname;
            case INVALID_PASSWORD -> fallback != null && !fallback.isBlank() ? fallback : messages.invalidPassword;
            case SECURITY_ANSWER_REQUIRED -> fallback != null && !fallback.isBlank()
                ? fallback
                : "Security answer is required to change username or email.";
            case SECURITY_ANSWER_INCORRECT -> messages.incorrectSecurityAnswer;
            case NOT_AUTHENTICATED -> fallback != null && !fallback.isBlank() ? fallback : "Not authenticated.";
            case SERVER_UNAVAILABLE -> fallback != null && !fallback.isBlank() ? fallback : "Server connection is unavailable.";
            case ACCOUNT_NOT_FOUND -> fallback != null && !fallback.isBlank() ? fallback : "Account not found.";
            default -> fallback;
        };
    }
}
