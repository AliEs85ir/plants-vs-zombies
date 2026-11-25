package server.user;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class ServerUserRepository {
    public static final Path USERS_FILE = Path.of("server_users.json");
    private final Path usersFile;
    private static final Type LIST_TYPE = new TypeToken<ArrayList<UserAccount>>() {}.getType();

    private final Gson gson = new GsonBuilder()
        .setPrettyPrinting()
        .create();

    // key = lowercase username
    private final Map<String, UserAccount> accounts = new LinkedHashMap<>();

    public ServerUserRepository() {
        this(USERS_FILE);
    }

    public ServerUserRepository(Path usersFile) {
        this.usersFile = usersFile == null ? USERS_FILE : usersFile;
        load();
    }

    public synchronized Optional<UserAccount> findByUsername(String username) {
        if (username == null || username.isBlank()) {
            return Optional.empty();
        }

        return Optional.ofNullable(accounts.get(key(username)));
    }

    public synchronized Optional<UserAccount> findByEmail(String email) {
        if (email == null || email.isBlank()) {
            return Optional.empty();
        }

        String targetEmail = email.trim().toLowerCase(Locale.ROOT);

        return accounts.values()
            .stream()
            .filter(account ->
                account.getEmail() != null &&
                    account.getEmail().trim().toLowerCase(Locale.ROOT).equals(targetEmail))
            .findFirst();
    }

    public synchronized Optional<UserAccount> findBySessionToken(String sessionToken) {
        if (sessionToken == null || sessionToken.isBlank()) {
            return Optional.empty();
        }

        String token = sessionToken.trim();

        return accounts.values()
            .stream()
            .filter(account ->
                account.getSessionToken() != null &&
                    account.getSessionToken().equals(token))
            .findFirst();
    }

    public synchronized Collection<UserAccount> findAll() {
        return new ArrayList<>(accounts.values());
    }

    public synchronized boolean add(UserAccount account) {
        if (account == null ||
            account.getUsername() == null ||
            account.getUsername().isBlank() ||
            account.getEmail() == null ||
            account.getEmail().isBlank()) {
            return false;
        }

        String usernameKey = key(account.getUsername());

        if (accounts.containsKey(usernameKey)) {
            return false;
        }

        if (findByEmail(account.getEmail()).isPresent()) {
            return false;
        }

        accounts.put(usernameKey, account);
        save();
        return true;
    }

    /*
     * oldUsername is mandatory because the username may have changed.
     */
    public synchronized boolean update(String oldUsername, UserAccount updatedAccount) {
        if (oldUsername == null || oldUsername.isBlank() || updatedAccount == null) {
            return false;
        }

        String oldKey = key(oldUsername);
        String newKey = key(updatedAccount.getUsername());

        if (!accounts.containsKey(oldKey)) {
            return false;
        }

        if (!oldKey.equals(newKey) && accounts.containsKey(newKey)) {
            return false;
        }

        accounts.remove(oldKey);
        accounts.put(newKey, updatedAccount);
        save();
        return true;
    }

    public synchronized void save() {
        try {
            Path parent = usersFile.toAbsolutePath().getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            Files.writeString(
                usersFile,
                gson.toJson(new ArrayList<>(accounts.values())),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
            );
        } catch (IOException e) {
            System.err.println("ServerUserRepository: could not save users: " + e.getMessage());
        }
    }

    private synchronized void load() {
        if (!Files.exists(usersFile)) {
            return;
        }

        try {
            String json = Files.readString(usersFile);
            List<UserAccount> loaded = gson.fromJson(json, LIST_TYPE);

            if (loaded == null) {
                return;
            }

            for (UserAccount account : loaded) {
                if (account != null &&
                    account.getUsername() != null &&
                    !account.getUsername().isBlank()) {

                    accounts.put(key(account.getUsername()), account);
                }
            }
        } catch (Exception e) {
            System.err.println("ServerUserRepository: could not load users: " + e.getMessage());
        }
    }

    private static String key(String username) {
        return username.trim().toLowerCase(Locale.ROOT);
    }
}
