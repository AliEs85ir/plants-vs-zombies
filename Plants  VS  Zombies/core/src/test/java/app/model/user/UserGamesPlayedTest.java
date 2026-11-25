package app.model.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserGamesPlayedTest {
    @Test
    void incrementGamesPlayedAddsOne() {
        User user = new User();
        assertEquals(0, user.getGamesPlayed());
        user.incrementGamesPlayed();
        assertEquals(1, user.getGamesPlayed());
        user.incrementGamesPlayed();
        assertEquals(2, user.getGamesPlayed());
    }

    @Test
    void incrementGamesPlayedCatchesUpToTotalPlayed() {
        User user = new User();
        user.getGameProgress().setTotalGamesPlayed(4);
        user.incrementGamesPlayed();
        assertEquals(4, user.getGamesPlayed());
    }
}
