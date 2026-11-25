package app.validator;

/**
 * Shared email rules used by client and server. Extends the historical
 * AppValidator checks: exactly one {@code @}, local/domain charset, TLD.
 */
public final class EmailValidator {
    static final String FORBIDDEN_SPECIALS = "?><,\"';:\\/|[]}{+=()*&^%$# !";

    private EmailValidator() {
    }

    public static boolean isValid(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        if (email.contains("..")) {
            return false;
        }
        int firstAt = email.indexOf('@');
        int lastAt = email.lastIndexOf('@');
        if (firstAt <= 0 || firstAt != lastAt || firstAt == email.length() - 1) {
            return false;
        }
        for (int i = 0; i < email.length(); i++) {
            if (FORBIDDEN_SPECIALS.indexOf(email.charAt(i)) >= 0) {
                return false;
            }
        }
        String local = email.substring(0, firstAt);
        String domain = email.substring(firstAt + 1);
        return isLocalValid(local) && isDomainValid(domain);
    }

    private static boolean isLocalValid(String local) {
        if (local.isEmpty() || local.startsWith(".") || local.endsWith(".")) {
            return false;
        }
        if (!isAlnum(local.charAt(0)) || !isAlnum(local.charAt(local.length() - 1))) {
            return false;
        }
        for (int i = 0; i < local.length(); i++) {
            char c = local.charAt(i);
            if (!(isAlnum(c) || c == '.' || c == '_' || c == '-')) {
                return false;
            }
        }
        return true;
    }

    private static boolean isDomainValid(String domain) {
        int lastDot = domain.lastIndexOf('.');
        if (lastDot <= 0 || lastDot == domain.length() - 1) {
            return false;
        }
        String tld = domain.substring(lastDot + 1);
        if (tld.length() < 2) {
            return false;
        }
        for (int i = 0; i < tld.length(); i++) {
            if (!isLetter(tld.charAt(i))) {
                return false;
            }
        }
        String[] labels = domain.split("\\.");
        if (labels.length < 2) {
            return false;
        }
        for (String label : labels) {
            if (label.isEmpty() || !isAlnum(label.charAt(0)) || !isAlnum(label.charAt(label.length() - 1))) {
                return false;
            }
            for (int i = 0; i < label.length(); i++) {
                char c = label.charAt(i);
                if (!(isAlnum(c) || c == '-')) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean isAlnum(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9');
    }

    private static boolean isLetter(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
    }
}
