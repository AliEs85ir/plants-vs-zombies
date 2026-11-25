package game.world.chapter.rule.scoringgame;

import app.model.App;
import app.model.user.User;
import game.engine.time.GameClock;
import game.world.GameWorld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScoringPatternTest {
    @BeforeEach
    void startScoringSession() {
        ScoringSession.begin();
    }

    @Test
    void groupKillOnceAwardsWhenEnoughZombiesDieTogether() {
        GameWorld world = new GameWorld();
        GroupKillOncePattern pattern = new GroupKillOncePattern(3);
        pattern.onChapterStart(world, null, null);

        world.setTotalDeadZombies(3);
        pattern.onTick(world, 0);

        assertEquals(1, pattern.getMatchCount());
    }

    @Test
    void groupKillOnceAllowsKillsSpreadAcrossAShortWindow() {
        GameWorld world = new GameWorld();
        GroupKillOncePattern pattern = new GroupKillOncePattern(3);
        pattern.onChapterStart(world, null, null);

        world.setTotalDeadZombies(1);
        pattern.onTick(world, 0);
        world.setTotalDeadZombies(3);
        pattern.onTick(world, GroupKillOncePattern.WINDOW_TICKS - 1);

        assertEquals(1, pattern.getMatchCount());
    }

    @Test
    void groupKillOnceResetsWhenTheWindowExpires() {
        GameWorld world = new GameWorld();
        GroupKillOncePattern pattern = new GroupKillOncePattern(3);
        pattern.onChapterStart(world, null, null);

        world.setTotalDeadZombies(1);
        pattern.onTick(world, 0);
        pattern.onTick(world, GroupKillOncePattern.WINDOW_TICKS);
        world.setTotalDeadZombies(2);
        pattern.onTick(world, GroupKillOncePattern.WINDOW_TICKS + 1);

        assertEquals(0, pattern.getMatchCount());
    }

    @Test
    void quickZombieKillAwardsWhenTargetIsReachedBeforeTimeout() {
        GameWorld world = new GameWorld();
        QuickZombieKillPattern pattern = new QuickZombieKillPattern(3, 1);
        pattern.onChapterStart(world, null, null);

        world.setTotalDeadZombies(1);
        pattern.onTick(world, 0);
        world.setTotalDeadZombies(3);
        pattern.onTick(world, 5);

        assertEquals(1, pattern.getMatchCount());
        assertEquals(0, pattern.getZombiesKilledInWindow());
    }

    @Test
    void quickZombieKillResetsKillCountWhenTheTimerExpires() {
        GameWorld world = new GameWorld();
        QuickZombieKillPattern pattern = new QuickZombieKillPattern(3, 1);
        pattern.onChapterStart(world, null, null);

        world.setTotalDeadZombies(1);
        pattern.onTick(world, 0);
        pattern.onTick(world, GameClock.TICKS_PER_SECOND);
        world.setTotalDeadZombies(2);
        pattern.onTick(world, GameClock.TICKS_PER_SECOND + 1);

        assertEquals(0, pattern.getMatchCount());
        assertEquals(1, pattern.getZombiesKilledInWindow());
    }

    @Test
    void killComboAwardsDoubleKillThenTripleKill() {
        GameWorld world = new GameWorld();
        KillComboPattern pattern = new KillComboPattern();
        pattern.onChapterStart(world, null, null);

        world.setTotalDeadZombies(2);
        pattern.onTick(world, 0);
        assertEquals(1, pattern.getMatchCount());
        assertEquals("Double Kill!", KillComboPattern.comboLabel(0, 2));

        world.setTotalDeadZombies(3);
        pattern.onTick(world, 1);
        assertEquals(2, pattern.getMatchCount());
    }

    @Test
    void explodonatorAwardsWhenTenZombiesDieTogether() {
        GameWorld world = new GameWorld();
        ExplodonatorPattern pattern = new ExplodonatorPattern();
        pattern.onChapterStart(world, null, null);

        world.setTotalDeadZombies(10);
        pattern.onTick(world, 0);

        assertEquals(1, pattern.getMatchCount());
    }

    @Test
    void perfectWaveAwardsWhenAWaveIsClearedWithoutLostPlants() {
        GameWorld world = new GameWorld();
        PerfectWavePattern pattern = new PerfectWavePattern();
        pattern.onChapterStart(world, null, null);

        world.setCurrentWaveNumber(1);
        pattern.onTick(world, 0);
        world.setTotalDeadZombies(4);
        world.setCurrentWaveNumber(2);
        pattern.onTick(world, 1);

        assertEquals(1, pattern.getMatchCount());
    }

    @Test
    void awardsAccumulateOnTheSessionInsteadOfTheSavedBest() {
        User user = new User();
        user.setMioPoint(40);
        App.setCurrentUser(user);
        try {
            GameWorld world = new GameWorld();
            GroupKillOncePattern pattern = new GroupKillOncePattern(3);
            pattern.onChapterStart(world, null, null);

            world.setTotalDeadZombies(3);
            pattern.onTick(world, 0);

            assertEquals(1, pattern.getMatchCount());
            assertEquals(30, ScoringSession.points());
            assertEquals(40, user.getMioPoint());
        } finally {
            App.setCurrentUser(null);
        }
    }
}
