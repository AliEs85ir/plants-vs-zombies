package app.repository;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class Repository {
    private static final String PREFS_NAME = "pvz-login-session";
    private static final String TOKEN_KEY = "sessionToken";
    private static final String USERNAME_KEY = "username";
    private static final Path TOKEN_FILE = Path.of("session_token.txt");
    private static final Path SESSION_FILE = Path.of("session.json");
    private static final Gson GSON = new GsonBuilder().create();

    private Repository() {
    }

    public static void saveLoginToken(String token) {
        saveRememberedLogin(token, loadRememberedUsername());
    }

    public static void saveRememberedLogin(String token, String username) {
        String safeToken = token == null ? "" : token.trim();
        String safeUsername = username == null ? "" : username.trim();

        if (safeToken.isBlank() && safeUsername.isBlank()) {
            clearLoginToken();
            return;
        }

        writePreferences(safeToken, safeUsername);
        writeSessionFile(safeToken, safeUsername);
        if (!safeToken.isBlank()) {
            writeLegacyTokenFile(safeToken);
        }
    }

    public static String loadLoginToken() {
        String fromPrefs = readPreference(TOKEN_KEY);
        if (fromPrefs != null && !fromPrefs.isBlank()) {
            return fromPrefs.trim();
        }

        SessionRecord record = readSessionFile();
        if (record != null && record.sessionToken != null && !record.sessionToken.isBlank()) {
            return record.sessionToken.trim();
        }

        return readLegacyTokenFile();
    }

    public static String loadRememberedUsername() {
        String fromPrefs = readPreference(USERNAME_KEY);
        if (fromPrefs != null && !fromPrefs.isBlank()) {
            return fromPrefs.trim();
        }

        SessionRecord record = readSessionFile();
        if (record != null && record.username != null && !record.username.isBlank()) {
            return record.username.trim();
        }

        return null;
    }

    public static boolean hasRememberedLogin() {
        String token = loadLoginToken();
        if (token != null && !token.isBlank()) {
            return true;
        }
        String username = loadRememberedUsername();
        return username != null && !username.isBlank();
    }

    public static void clearLoginToken() {
        try {
            if (Gdx.app != null) {
                Preferences preferences = Gdx.app.getPreferences(PREFS_NAME);
                preferences.clear();
                preferences.flush();
            }
        } catch (Exception e) {
            System.err.println("Could not clear login preferences: " + e.getMessage());
        }

        deleteFile(SESSION_FILE);
        deleteFile(TOKEN_FILE);

        if (Gdx.files != null) {
            try {
                FileHandle localSession = Gdx.files.local(SESSION_FILE.toString());
                if (localSession.exists()) {
                    localSession.delete();
                }
                FileHandle localToken = Gdx.files.local(TOKEN_FILE.toString());
                if (localToken.exists()) {
                    localToken.delete();
                }
            } catch (Exception e) {
                System.err.println("Could not clear local login files: " + e.getMessage());
            }
        }
    }

    private static void writePreferences(String token, String username) {
        if (Gdx.app == null) {
            return;
        }
        try {
            Preferences preferences = Gdx.app.getPreferences(PREFS_NAME);
            preferences.putString(TOKEN_KEY, token);
            preferences.putString(USERNAME_KEY, username);
            preferences.flush();
        } catch (Exception e) {
            System.err.println("Could not save login preferences: " + e.getMessage());
        }
    }

    private static String readPreference(String key) {
        if (Gdx.app == null) {
            return null;
        }
        try {
            Preferences preferences = Gdx.app.getPreferences(PREFS_NAME);
            String value = preferences.getString(key, "");
            return value == null || value.isBlank() ? null : value;
        } catch (Exception e) {
            return null;
        }
    }

    private static void writeSessionFile(String token, String username) {
        SessionRecord record = new SessionRecord();
        record.sessionToken = token;
        record.username = username;
        String json = GSON.toJson(record);

        if (Gdx.files != null) {
            try {
                Gdx.files.local(SESSION_FILE.toString()).writeString(json, false);
                return;
            } catch (Exception e) {
                System.err.println("Could not save local session file: " + e.getMessage());
            }
        }

        writePath(SESSION_FILE, json);
    }

    private static SessionRecord readSessionFile() {
        if (Gdx.files != null) {
            try {
                FileHandle file = Gdx.files.local(SESSION_FILE.toString());
                if (file.exists()) {
                    SessionRecord record = GSON.fromJson(file.readString(), SessionRecord.class);
                    if (record != null) {
                        return record;
                    }
                }
            } catch (Exception ignored) {
            }
        }

        if (!Files.exists(SESSION_FILE)) {
            return null;
        }
        try {
            return GSON.fromJson(Files.readString(SESSION_FILE), SessionRecord.class);
        } catch (Exception e) {
            return null;
        }
    }

    private static void writeLegacyTokenFile(String token) {
        if (Gdx.files != null) {
            try {
                Gdx.files.local(TOKEN_FILE.toString()).writeString(token, false);
                return;
            } catch (Exception e) {
                System.err.println("Could not save login token: " + e.getMessage());
            }
        }
        writePath(TOKEN_FILE, token);
    }

    private static String readLegacyTokenFile() {
        if (Gdx.files != null) {
            try {
                FileHandle file = Gdx.files.local(TOKEN_FILE.toString());
                if (file.exists()) {
                    String token = file.readString().trim();
                    return token.isEmpty() ? null : token;
                }
            } catch (Exception ignored) {
            }
        }

        try {
            if (!Files.exists(TOKEN_FILE)) {
                return null;
            }
            String token = Files.readString(TOKEN_FILE).trim();
            return token.isEmpty() ? null : token;
        } catch (IOException e) {
            return null;
        }
    }

    private static void writePath(Path path, String value) {
        try {
            Files.writeString(
                path,
                value,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
            );
        } catch (IOException e) {
            System.err.println("Could not save login data: " + e.getMessage());
        }
    }

    private static void deleteFile(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            System.err.println("Could not clear login file: " + e.getMessage());
        }
    }

    private static final class SessionRecord {
        String sessionToken;
        String username;
    }
}
