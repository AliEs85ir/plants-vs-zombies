package app.validator;

public final class NicknameRules {
    public static final int MIN_LENGTH = 3;
    public static final int MAX_LENGTH = 30;
    private static final String USERNAME_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-";

    private NicknameRules() {
    }

    public static boolean isValid(String nickname, String username) {
        if (nickname == null) {
            return false;
        }
        String value = nickname.trim();
        if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            return false;
        }
        if (username != null && value.equalsIgnoreCase(username.trim())) {
            return isUsernameCharset(value);
        }
        return true;
    }

    public static boolean isUsernameCharset(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        for (int i = 0; i < value.length(); i++) {
            if (USERNAME_CHARS.indexOf(value.charAt(i)) < 0) {
                return false;
            }
        }
        return true;
    }
}
