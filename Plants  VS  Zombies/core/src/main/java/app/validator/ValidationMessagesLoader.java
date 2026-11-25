package app.validator;

import app.service.ConfigFiles;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;

public final class ValidationMessagesLoader {
    public static final String CONFIG_PATH = "ui/validation_messages.json";

    private ValidationMessagesLoader() {
    }

    public static ValidationMessages load() {
        return load(CONFIG_PATH);
    }

    public static ValidationMessages load(String path) {
        File file = ConfigFiles.resolve(path);
        if (file == null || !file.isFile()) {
            return ValidationMessages.defaults();
        }
        try (FileReader reader = new FileReader(file)) {
            ValidationMessages messages = new Gson().fromJson(reader, ValidationMessages.class);
            if (messages == null) {
                return ValidationMessages.defaults();
            }
            return messages.sanitize();
        } catch (Exception exception) {
            return ValidationMessages.defaults();
        }
    }
}
