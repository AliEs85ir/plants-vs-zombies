package app.validator;

import java.util.ArrayList;
import java.util.List;

public final class PasswordRules {
    /** Allowed specials only (aligned with ValidatingCommands.PASSWORD_FORMAT / PASSWORD_STRENGTH). */
    public static final String SPECIALS = "!#$%^&*)(=+{}][|/\\:;'\",<>?";
    public static final int MIN_LENGTH = 8;

    private PasswordRules() {
    }

    public static boolean isAllowedChar(char c) {
        return (c >= 'a' && c <= 'z')
            || (c >= 'A' && c <= 'Z')
            || (c >= '0' && c <= '9')
            || SPECIALS.indexOf(c) >= 0;
    }

    public static boolean hasOnlyAllowedChars(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        for (int i = 0; i < password.length(); i++) {
            if (!isAllowedChar(password.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static List<String> failures(String password) {
        List<String> failures = new ArrayList<>();
        ValidationMessages messages = ValidationMessages.active();
        if (password == null) {
            password = "";
        }
        if (password.length() < MIN_LENGTH) {
            failures.add(messages.passwordTooShort);
        }
        boolean lower = false;
        boolean upper = false;
        boolean digit = false;
        boolean special = false;
        boolean allowedOnly = true;
        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (c >= 'a' && c <= 'z') {
                lower = true;
            } else if (c >= 'A' && c <= 'Z') {
                upper = true;
            } else if (c >= '0' && c <= '9') {
                digit = true;
            } else if (SPECIALS.indexOf(c) >= 0) {
                special = true;
            } else {
                allowedOnly = false;
            }
        }
        if (!allowedOnly) {
            failures.add(messages.invalidPassword);
        }
        if (!lower) {
            failures.add(messages.passwordNeedLower);
        }
        if (!upper) {
            failures.add(messages.passwordNeedUpper);
        }
        if (!digit) {
            failures.add(messages.passwordNeedDigit);
        }
        if (!special) {
            failures.add(messages.passwordNeedSpecial);
        }
        return failures;
    }

    public static boolean isStrong(String password) {
        return failures(password).isEmpty();
    }

    public static String joinedFailures(String password) {
        return String.join("\n", failures(password));
    }
}
