package server.auth;

import app.validator.EmailValidator;
import app.validator.NicknameRules;
import app.validator.PasswordRules;
import app.validator.ValidationMessages;

public class ServerValidator {
    public static final String USERNAME_PATTERN = "^[A-Za-z0-9\\-]{3,20}$";

    private ServerValidator() {}

    public static String validateUsername(String username) {
        if (username == null || username.isBlank()) return "Username is required.";
        if (!username.matches(USERNAME_PATTERN)) {
            return "Username must be 3-20 characters (letters, digits, hyphen).";
        }
        return null;
    }

    public static String validatePassword(String password) {
        if (password == null || password.isEmpty()) return "Password is required.";
        var failures = PasswordRules.failures(password);
        if (!failures.isEmpty()) {
            return PasswordRules.joinedFailures(password);
        }
        return null;
    }

    public static String validateNickname(String nickname) {
        return validateNickname(nickname, null);
    }

    public static String validateNickname(String nickname, String username) {
        if (nickname == null || nickname.isBlank()) return "Nickname is required.";
        if (!NicknameRules.isValid(nickname, username)) {
            return ValidationMessages.active().invalidNickname;
        }
        return null;
    }

    public static String validateEmail(String email) {
        if (email == null || email.isBlank()) return "Email is required.";
        if (!EmailValidator.isValid(email)) return "Email format is invalid.";
        return null;
    }

    public static String validateGender(String gender) {
        if (gender == null || gender.isBlank()) return "Gender is required.";
        String g = gender.trim().toLowerCase();
        if (!g.equals("male") && !g.equals("female") && !g.equals("other")) {
            return "Gender must be male, female, or other.";
        }
        return null;
    }
}
