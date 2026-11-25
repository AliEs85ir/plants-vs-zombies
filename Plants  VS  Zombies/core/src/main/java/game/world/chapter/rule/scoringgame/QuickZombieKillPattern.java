package game.world.chapter.rule.scoringgame;

import game.component.physic.PhysicService;
import game.engine.time.GameClock;
import game.world.GameWorld;
import graphics.GraphicsEngine;

public class QuickZombieKillPattern extends ScoringPattern {
    private enum WindowState {
        WAITING_FOR_KILL,
        TIMER_ACTIVE
    }

    private final int count;
    private final float intervalSeconds;
    private final int mioPoints;
    private WindowState state = WindowState.WAITING_FOR_KILL;
    private long startTick;
    private int killsAtWindowStart;
    private int zombiesKilledInWindow;

    public QuickZombieKillPattern(int count, float intervalSeconds) {
        this.count = boundedCount(count);
        this.intervalSeconds = Math.max(0.1f, intervalSeconds);
        this.mioPoints = 8 * this.count;
    }

    @Override
    public void onChapterStart(GameWorld world, GraphicsEngine graphicsEngine, PhysicService physicService) {
        state = WindowState.WAITING_FOR_KILL;
        killsAtWindowStart = world == null ? 0 : world.getTotalDeadZombies();
        zombiesKilledInWindow = 0;
        startTick = 0;
    }

    @Override
    public void onTick(GameWorld world, long currentTick) {
        if (world == null) {
            return;
        }
        int currentTotalDead = world.getTotalDeadZombies();

        switch (state) {
            case WAITING_FOR_KILL:
                if (currentTotalDead > killsAtWindowStart) {
                    startTick = currentTick;
                    state = WindowState.TIMER_ACTIVE;
                    zombiesKilledInWindow = currentTotalDead - killsAtWindowStart;
                    if (zombiesKilledInWindow >= count) {
                        completeWindow();
                    }
                } else {
                    killsAtWindowStart = currentTotalDead;
                }
                break;

            case TIMER_ACTIVE:
                zombiesKilledInWindow = currentTotalDead - killsAtWindowStart;
                if (zombiesKilledInWindow >= count) {
                    completeWindow();
                } else if (currentTick - startTick >= windowTicks()) {
                    state = WindowState.WAITING_FOR_KILL;
                    killsAtWindowStart = currentTotalDead;
                    zombiesKilledInWindow = 0;
                }
                break;
        }
    }

    private void completeWindow() {
        award(
            count + " kills in " + formatInterval() + "! +" + mioPoints + " Meow",
            mioPoints
        );
        state = WindowState.WAITING_FOR_KILL;
        killsAtWindowStart = lastSeenDead();
        zombiesKilledInWindow = 0;
    }

    private int lastSeenDead() {
        return killsAtWindowStart + zombiesKilledInWindow;
    }

    private long windowTicks() {
        return Math.max(1L, Math.round(intervalSeconds * GameClock.TICKS_PER_SECOND));
    }

    private String formatInterval() {
        if (intervalSeconds == (int) intervalSeconds) {
            return (int) intervalSeconds + "s";
        }
        return intervalSeconds + "s";
    }

    public int getZombiesKilledInWindow() {
        return zombiesKilledInWindow;
    }

    public int getCount() {
        return count;
    }

    public float getIntervalSeconds() {
        return intervalSeconds;
    }
}
