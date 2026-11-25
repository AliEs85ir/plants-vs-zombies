package app.model.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserMioPointTest {
    @Test
    void recordBestKeepsTheHigherScoreOnly() {
        User user = new User();
        user.setMioPoint(40);

        assertFalse(user.recordBestMioPoint(12));
        assertEquals(40, user.getMioPoint());

        assertFalse(user.recordBestMioPoint(40));
        assertEquals(40, user.getMioPoint());

        assertTrue(user.recordBestMioPoint(41));
        assertEquals(41, user.getMioPoint());
    }

    @Test
    void firstPositiveSessionBecomesTheBest() {
        User user = new User();
        assertEquals(0, user.getMioPoint());
        assertTrue(user.recordBestMioPoint(18));
        assertEquals(18, user.getMioPoint());
    }
}
