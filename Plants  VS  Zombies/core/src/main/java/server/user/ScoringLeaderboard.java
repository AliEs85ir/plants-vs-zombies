package server.user;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import server.network.GameStateSerializer;
import server.network.Packets;

import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public final class ScoringLeaderboard {
    private static final Type LIST_TYPE = new TypeToken<ArrayList<UserAccount>>() {}.getType();
    private static final Gson GSON = new Gson();

    private ScoringLeaderboard() {
    }

    public static ArrayList<Packets.LeaderboardEntry> fromAccounts(Collection<UserAccount> accounts) {
        ArrayList<Packets.LeaderboardEntry> entries = new ArrayList<>();
        if (accounts == null) {
            return entries;
        }
        for (UserAccount account : accounts) {
            Packets.LeaderboardEntry entry = fromAccount(account);
            if (entry != null) {
                entries.add(entry);
            }
        }
        entries.sort(Comparator
            .comparingInt((Packets.LeaderboardEntry entry) -> entry.mioPoint)
            .reversed()
            .thenComparing(entry -> displayName(entry), String.CASE_INSENSITIVE_ORDER));
        return entries;
    }

    public static ArrayList<Packets.LeaderboardEntry> fromUsersFile() {
        return fromUsersFile(ServerUserRepository.USERS_FILE);
    }

    public static ArrayList<Packets.LeaderboardEntry> fromUsersFile(Path path) {
        if (path == null || !Files.exists(path)) {
            return new ArrayList<>();
        }
        try {
            List<UserAccount> loaded = GSON.fromJson(Files.readString(path), LIST_TYPE);
            return fromAccounts(loaded);
        } catch (Exception exception) {
            return new ArrayList<>();
        }
    }

    public static Packets.LeaderboardEntry fromAccount(UserAccount account) {
        if (account == null) {
            return null;
        }
        String username = account.getUsername();
        if (username == null || username.isBlank()) {
            return null;
        }
        Packets.LeaderboardEntry entry = new Packets.LeaderboardEntry();
        entry.username = username.trim();
        entry.nickname = account.getNickname() == null ? "" : account.getNickname().trim();
        entry.mioPoint = Math.max(0, account.getMioPoint());
        entry.gems = Math.max(0, account.getGems());
        entry.coins = Math.max(0, account.getCoins());
        entry.gamesPlayed = Math.max(0, account.getGamesPlayed());
        String picture = account.getProfilePictureId();
        if (picture == null || picture.isBlank()) {
            picture = GameStateSerializer.readProfilePictureId(account.getGameStateJson());
        }
        entry.pictureId = picture == null ? "" : picture;
        return entry;
    }

    public static String displayName(Packets.LeaderboardEntry entry) {
        if (entry == null) {
            return "Player";
        }
        if (entry.nickname != null && !entry.nickname.isBlank()) {
            return entry.nickname.trim();
        }
        if (entry.username != null && !entry.username.isBlank()) {
            return entry.username.trim();
        }
        return "Player";
    }
}
