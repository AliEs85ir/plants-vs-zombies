package app.view.auth;

import app.view.loading.InternalFiles;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

public final class RegistrationTermsCatalog {
    public static final String DEFAULT_PATH = "ui/registration_terms.json";

    private RegistrationTermsCatalog() {
    }

    public static String loadText(String path) {
        String resolved = path == null || path.isBlank() ? DEFAULT_PATH : path;
        FileHandle file = InternalFiles.resolve(resolved);
        if (file == null || !file.exists()) {
            return defaultText();
        }
        try {
            Json json = new Json();
            json.setIgnoreUnknownFields(true);
            TermsData data = json.fromJson(TermsData.class, file);
            if (data == null || data.text == null || data.text.isBlank()) {
                return defaultText();
            }
            return data.text;
        } catch (Exception exception) {
            return defaultText();
        }
    }

    private static String defaultText() {
        return """
            Lawn Laws & Gardener Terms

            1. Defend your lawn with courage, sunflowers, and good manners.
            2. Do not share your password, plant food stash, or security answers.
            3. Usernames, nicknames, and emails must identify one gardener only.
            4. Accounts are personal. Trading progress or coins for real money is not allowed.
            5. Debug Mode may reveal extra tools, but it never excuses broken rules.
            6. Stay logged in only on devices you trust.
            7. Security questions are for recovery, not for jokes that you will forget.
            8. By creating an account you agree to keep the lawn fair for every neighbor.
            """;
    }

    public static class TermsData {
        public String title = "";
        public String text = "";
    }
}
