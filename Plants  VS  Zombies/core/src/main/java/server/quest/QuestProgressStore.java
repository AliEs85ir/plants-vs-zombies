package server.quest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import game.service.quest.QuestFactory;
import game.service.quest.QuestManager;
import server.network.GameStateSerializer;
import server.user.UserAccount;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Locale;

public class QuestProgressStore {
    public static final Path DIRECTORY = Path.of("server_quests");

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public String relativePathFor(String username) {
        return DIRECTORY.resolve(sanitize(username) + ".json").toString().replace('\\', '/');
    }

    public boolean ensureAssigned(UserAccount account) {
        if (account == null) {
            return false;
        }
        if (account.getQuestProgressPath() != null && !account.getQuestProgressPath().isBlank()) {
            return false;
        }
        account.setQuestProgressPath(relativePathFor(account.getUsername()));
        return true;
    }

    public void initializeNewAccount(UserAccount account) {
        if (account == null) {
            return;
        }
        ensureAssigned(account);
        QuestManager manager = new QuestManager();
        QuestFactory.populateNewPlayer(manager);
        write(account, manager);
    }

    public QuestManager loadOrCreate(UserAccount account) {
        if (account == null) {
            QuestManager manager = new QuestManager();
            QuestFactory.populateNewPlayer(manager);
            return manager;
        }
        ensureAssigned(account);
        Path file = resolve(account.getQuestProgressPath());
        if (Files.exists(file)) {
            QuestManager loaded = read(file);
            if (loaded != null) {
                loaded.checkDailyReset();
                loaded.ensureEventQuests();
                write(account, loaded);
                return loaded;
            }
        }
        QuestManager manager = new QuestManager();
        QuestFactory.populateNewPlayer(manager);
        write(account, manager);
        return manager;
    }

    public void save(UserAccount account, QuestManager manager) {
        if (account == null || manager == null) {
            return;
        }
        ensureAssigned(account);
        manager.checkDailyReset();
        write(account, manager);
    }

    public void saveFromGameState(UserAccount account, String gameStateJson) {
        QuestManager manager = GameStateSerializer.readQuestManager(gameStateJson);
        if (manager != null) {
            save(account, manager);
        }
    }

    public void relocate(String oldUsername, UserAccount account) {
        if (account == null) {
            return;
        }
        Path previous = resolve(
            account.getQuestProgressPath() != null && !account.getQuestProgressPath().isBlank()
                ? account.getQuestProgressPath()
                : relativePathFor(oldUsername)
        );
        String nextRelative = relativePathFor(account.getUsername());
        Path next = resolve(nextRelative);
        try {
            if (Files.exists(previous) && !previous.equals(next)) {
                Path parent = next.getParent();
                if (parent != null) {
                    Files.createDirectories(parent);
                }
                Files.move(previous, next, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            System.err.println("QuestProgressStore: could not relocate quests: " + e.getMessage());
        }
        account.setQuestProgressPath(nextRelative);
    }

    private void write(UserAccount account, QuestManager manager) {
        Path file = resolve(account.getQuestProgressPath());
        try {
            Path parent = file.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(
                file,
                gson.toJson(QuestProgressMapper.toDto(manager)),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
            );
        } catch (IOException e) {
            System.err.println("QuestProgressStore: could not save quests: " + e.getMessage());
        }
    }

    private QuestManager read(Path file) {
        try {
            String json = Files.readString(file);
            if (json == null || json.isBlank()) {
                return null;
            }
            QuestManagerSaveDTO dto = gson.fromJson(json, QuestManagerSaveDTO.class);
            return QuestProgressMapper.fromDto(dto);
        } catch (Exception e) {
            System.err.println("QuestProgressStore: could not load quests: " + e.getMessage());
            return null;
        }
    }

    private static Path resolve(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) {
            return DIRECTORY.resolve("unknown.json");
        }
        return Path.of(relativePath);
    }

    private static String sanitize(String username) {
        if (username == null || username.isBlank()) {
            return "user";
        }
        String cleaned = username.trim().toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9._-]", "_");
        return cleaned.isEmpty() ? "user" : cleaned;
    }
}
