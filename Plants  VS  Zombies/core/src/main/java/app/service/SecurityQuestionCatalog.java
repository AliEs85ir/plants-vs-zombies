package app.service;

import app.enums.SecurityQuestions;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class SecurityQuestionCatalog {
    public static final String CONFIG_PATH = "ui/security_questions.json";

    private SecurityQuestionCatalog() {
    }

    public static List<Entry> list() {
        Map<Integer, String> overlay = loadOverlay(CONFIG_PATH);
        List<Entry> entries = new ArrayList<>();
        for (SecurityQuestions question : SecurityQuestions.values()) {
            String text = overlay.getOrDefault(question.getNumber(), question.getQuestion());
            if (text == null || text.isBlank()) {
                text = question.getQuestion();
            }
            entries.add(new Entry(question.getNumber(), text));
        }
        return entries;
    }

    public static String textForNumber(int number) {
        for (Entry entry : list()) {
            if (entry.number == number) {
                return entry.text;
            }
        }
        SecurityQuestions question = SecurityQuestions.getQuestionByNumber(number);
        return question == null ? "" : question.getQuestion();
    }

    public static boolean contains(int number) {
        return SecurityQuestions.getQuestionByNumber(number) != null;
    }

    public static String numberedList() {
        StringBuilder questions = new StringBuilder();
        questions.append("Please choose and answer a security question :").append("\n");
        for (Entry entry : list()) {
            questions.append(entry.number).append(". ");
            questions.append(entry.text).append("\n");
        }
        return questions.toString();
    }

    private static Map<Integer, String> loadOverlay(String path) {
        Map<Integer, String> overlay = new LinkedHashMap<>();
        File file = ConfigFiles.resolve(path);
        if (file == null || !file.isFile()) {
            return overlay;
        }
        try (FileReader reader = new FileReader(file)) {
            FileData data = new Gson().fromJson(reader, FileData.class);
            if (data == null || data.questions == null) {
                return overlay;
            }
            for (QuestionData question : data.questions) {
                if (question == null || question.number <= 0) {
                    continue;
                }
                if (question.text == null || question.text.isBlank()) {
                    continue;
                }
                overlay.put(question.number, question.text.trim());
            }
        } catch (Exception ignored) {
            return overlay;
        }
        return overlay;
    }

    public static final class Entry {
        public final int number;
        public final String text;

        public Entry(int number, String text) {
            this.number = number;
            this.text = text;
        }

        public String display() {
            return number + ". " + text;
        }
    }

    public static class FileData {
        public ArrayList<QuestionData> questions = new ArrayList<>();
    }

    public static class QuestionData {
        public int number;
        public String text = "";
    }
}
