package server.auth;

import java.util.LinkedHashMap;
import java.util.Map;

public class ServerSecurityQuestions {
    private static final Map<Integer, String> QUESTIONS = new LinkedHashMap<>();
    static {
        QUESTIONS.put(1, "What is your favorite plant?");
        QUESTIONS.put(2, "What was the name of your first pet?");
        QUESTIONS.put(3, "What is your mother's maiden name?");
        QUESTIONS.put(4, "In which city were you born?");
    }
    private ServerSecurityQuestions() {}

    public static boolean isValidNumber(int n) { return QUESTIONS.containsKey(n); }

    public static String questionText(int n) { return QUESTIONS.get(n); }

    public static int parse(String numberText) {
        try { return Integer.parseInt(numberText.trim()); }
        catch (Exception e) { return -1; }
    }
}
