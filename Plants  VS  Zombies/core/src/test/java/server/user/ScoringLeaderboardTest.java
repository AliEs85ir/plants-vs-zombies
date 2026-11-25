package server.user;

import org.junit.jupiter.api.Test;
import server.network.Packets;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScoringLeaderboardTest {
    @Test
    void onlyAccountsWithUsernamesAreRanked() {
        List<UserAccount> accounts = List.of(
            account("zero", "Zero", 0),
            account("ihan", "Ihan", 108),
            account("reza", "awli", 18),
            account("", "Ghost", 0)
        );

        ArrayList<Packets.LeaderboardEntry> entries = ScoringLeaderboard.fromAccounts(accounts);

        assertEquals(3, entries.size());
        assertEquals("ihan", entries.get(0).username);
        assertEquals("Ihan", entries.get(0).nickname);
        assertEquals(108, entries.get(0).mioPoint);
        assertEquals(12, entries.get(0).gems);
        assertEquals(40, entries.get(0).coins);
        assertEquals(7, entries.get(0).gamesPlayed);
        assertEquals("reza", entries.get(1).username);
        assertEquals(18, entries.get(1).mioPoint);
        assertEquals("zero", entries.get(2).username);
        assertEquals(0, entries.get(2).mioPoint);
    }

    @Test
    void missingUsersFileYieldsEmptyLeaderboard() {
        Path missing = Path.of("definitely-not-server-users-" + System.nanoTime() + ".json");
        assertTrue(Files.notExists(missing));
        assertTrue(ScoringLeaderboard.fromUsersFile(missing).isEmpty());
    }

    private static UserAccount account(String username, String nickname, int mioPoint) {
        UserAccount account = new UserAccount();
        account.setUsername(username);
        account.setNickname(nickname);
        account.setMioPoint(mioPoint);
        if ("ihan".equals(username)) {
            account.setGems(12);
            account.setCoins(40);
            account.setGamesPlayed(7);
        }
        return account;
    }
}
