package game.world.chapter.rule.scoringgame;

import app.model.App;
import app.model.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScoringSessionTest {
    @BeforeEach
    void startFresh() {
        ScoringSession.begin();
        App.setCurrentUser(null);
    }

    @AfterEach
    void clearUser() {
        App.setCurrentUser(null);
        ScoringSession.begin();
    }

    @Test
    void addsOnlyToThisMatchTotal() {
        ScoringSession.add(10);
        ScoringSession.add(22);
        ScoringSession.add(0);
        ScoringSession.add(-4);
        assertEquals(32, ScoringSession.points());
    }

    @Test
    void beginResetsTheMatchTotal() {
        ScoringSession.add(50);
        ScoringSession.begin();
        assertEquals(0, ScoringSession.points());
        ScoringSession.add(7);
        assertEquals(7, ScoringSession.points());
    }

    @Test
    void finishStoresANewPersonalBest() {
        User user = new User();
        user.setMioPoint(8);
        App.setCurrentUser(user);

        ScoringSession.add(20);
        assertTrue(ScoringSession.finish());
        assertEquals(20, user.getMioPoint());
        assertFalse(ScoringSession.isActive());
        assertFalse(ScoringSession.finish());
    }

    @Test
    void finishDoesNotLowerAnExistingBest() {
        User user = new User();
        user.setMioPoint(50);
        App.setCurrentUser(user);

        ScoringSession.add(12);
        assertFalse(ScoringSession.finish());
        assertEquals(50, user.getMioPoint());
    }

    @Test
    void finishGenerationIgnoresAStaleMatch() {
        User user = new User();
        App.setCurrentUser(user);
        int oldId = ScoringSession.begin();
        ScoringSession.add(15);
        int newId = ScoringSession.begin();
        ScoringSession.add(9);

        assertFalse(ScoringSession.finishGeneration(oldId));
        assertEquals(0, user.getMioPoint());
        assertTrue(ScoringSession.finishGeneration(newId));
        assertEquals(9, user.getMioPoint());
    }
}
