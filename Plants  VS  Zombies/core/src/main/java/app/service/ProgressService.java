package app.service;

import app.model.App;
import app.model.user.User;
import server.network.GameStateSerializer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Locale;

public class ProgressService {
    private static final Path ROOT = Path.of("userdata");
    private static final String PROGRESS_FILE_NAME = "progress.json";

    private ProgressService() {
    }

    public static boolean saveCurrentProgress() {
        return saveUserProgress(App.getCurrentUser());
    }

    public static boolean saveUserProgress(User user) {
        if (user == null || user.getUsername() == null || user.getUsername().isBlank()) {
            return false;
        }

        try {
            Path file = progressFile(user.getUsername());
            Path parent = file.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            String json = GameStateSerializer.serialize(user);
            if (json == null || json.isBlank()) {
                return false;
            }

            Path temp = file.resolveSibling(PROGRESS_FILE_NAME + ".tmp");
            Files.writeString(
                temp,
                json,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
            );
            Files.move(
                temp,
                file,
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.ATOMIC_MOVE
            );
            return true;
        } catch (IOException e) {
            try {
                Path file = progressFile(user.getUsername());
                String json = GameStateSerializer.serialize(user);
                if (json == null || json.isBlank()) {
                    return false;
                }
                Files.writeString(
                    file,
                    json,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE
                );
                return true;
            } catch (Exception fallback) {
                System.err.println(
                    "ProgressService: could not save progress: " + fallback.getMessage()
                );
                return false;
            }
        } catch (Exception e) {
            System.err.println(
                "ProgressService: could not save progress: " + e.getMessage()
            );
            return false;
        }
    }

    public static boolean loadUserProgress(User user) {
        if (user == null || user.getUsername() == null || user.getUsername().isBlank()) {
            return false;
        }

        Path file = progressFile(user.getUsername());
        if (!Files.exists(file)) {
            return false;
        }

        try {
            String json = Files.readString(file);
            if (json == null || json.isBlank()) {
                System.err.println("ProgressService: empty save for " + user.getUsername());
                return false;
            }

            boolean applied = GameStateSerializer.applyToUser(user, json);
            if (!applied) {
                quarantineCorruptSave(file);
            }
            return applied;
        } catch (Exception e) {
            System.err.println(
                "ProgressService: could not load progress: " + e.getMessage()
            );
            quarantineCorruptSave(file);
            return false;
        }
    }

    public static boolean hasUserProgress(String username) {
        if (username == null || username.isBlank()) {
            return false;
        }
        return Files.exists(progressFile(username));
    }

    public static void renameUserProgress(String oldUsername, String newUsername) {
        if (oldUsername == null || oldUsername.isBlank()
            || newUsername == null || newUsername.isBlank()) {
            return;
        }

        if (sanitize(oldUsername).equals(sanitize(newUsername))) {
            return;
        }

        Path oldDir = userDirectory(oldUsername);
        Path newDir = userDirectory(newUsername);

        if (!Files.exists(oldDir)) {
            return;
        }

        try {
            Path newParent = newDir.getParent();
            if (newParent != null) {
                Files.createDirectories(newParent);
            }

            if (Files.exists(newDir)) {
                Path oldFile = progressFile(oldUsername);
                Path newFile = progressFile(newUsername);
                if (Files.exists(oldFile)) {
                    Files.move(
                        oldFile,
                        newFile,
                        StandardCopyOption.REPLACE_EXISTING
                    );
                }
                deleteRecursively(oldDir);
            } else {
                Files.move(oldDir, newDir);
            }
        } catch (IOException e) {
            System.err.println(
                "ProgressService: could not rename progress: " + e.getMessage()
            );
        }
    }

    public static Path progressFile(String username) {
        return userDirectory(username).resolve(PROGRESS_FILE_NAME);
    }

    private static Path userDirectory(String username) {
        return ROOT.resolve(sanitize(username));
    }

    private static String sanitize(String username) {
        String trimmed = username.trim().toLowerCase(Locale.ROOT);
        String cleaned = trimmed.replaceAll("[^a-z0-9._-]", "_");
        return cleaned.isEmpty() ? "user" : cleaned;
    }

    private static void quarantineCorruptSave(Path file) {
        try {
            if (!Files.exists(file)) {
                return;
            }
            Path backup = file.resolveSibling(
                PROGRESS_FILE_NAME + ".corrupt." + System.currentTimeMillis()
            );
            Files.move(file, backup, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.err.println(
                "ProgressService: could not quarantine corrupt save: " + e.getMessage()
            );
        }
    }

    private static void deleteRecursively(Path path) throws IOException {
        if (!Files.exists(path)) {
            return;
        }
        if (Files.isDirectory(path)) {
            try (var stream = Files.list(path)) {
                for (Path child : stream.toList()) {
                    deleteRecursively(child);
                }
            }
        }
        Files.deleteIfExists(path);
    }
}
