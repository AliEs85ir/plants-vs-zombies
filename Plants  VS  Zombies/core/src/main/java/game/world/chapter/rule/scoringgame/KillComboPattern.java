package game.world.chapter.rule.scoringgame;

import game.component.physic.PhysicService;
import game.engine.time.GameClock;
import game.world.GameWorld;
import graphics.GraphicsEngine;

public class KillComboPattern extends ScoringPattern {
    static final int COMBO_TICKS = 2 * GameClock.TICKS_PER_SECOND;

    private int lastSeenDead;
    private int streak;
    private long lastKillTick = -1;

    @Override
    public void onChapterStart(GameWorld world, GraphicsEngine graphicsEngine, PhysicService physicService) {
        lastSeenDead = world == null ? 0 : world.getTotalDeadZombies();
        streak = 0;
        lastKillTick = -1;
    }

    @Override
    public void onTick(GameWorld world, long currentTick) {
        if (world == null) {
            return;
        }
        int currentDead = world.getTotalDeadZombies();
        int newlyDead = Math.max(0, currentDead - lastSeenDead);
        lastSeenDead = currentDead;
        if (newlyDead <= 0) {
            return;
        }
        if (lastKillTick < 0 || currentTick - lastKillTick > COMBO_TICKS) {
            streak = newlyDead;
        } else {
            streak += newlyDead;
        }
        lastKillTick = currentTick;
        String label = comboLabel(streak - newlyDead, streak);
        if (label != null) {
            int points = 12 * streak;
            award(label + " +" + points + " Meow", points);
        }
    }

    static String comboLabel(int previousStreak, int streak) {
        if (previousStreak < 5 && streak >= 5) {
            return "Overkill!";
        }
        if (previousStreak < 4 && streak >= 4) {
            return "Quad Kill!";
        }
        if (previousStreak < 3 && streak >= 3) {
            return "Triple Kill!";
        }
        if (previousStreak < 2 && streak >= 2) {
            return "Double Kill!";
        }
        return null;
    }
}
