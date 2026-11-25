package game.world.chapter.rule.scoringgame;

import game.component.physic.PhysicService;
import game.world.GameWorld;
import graphics.GraphicsEngine;

public class GroupKillOncePattern extends ScoringPattern {
    static final int WINDOW_TICKS = 4;

    private final int count;
    private final int mioPoints;
    private final String title;
    private int lastSeenDead;
    private int killsInWindow;
    private long windowStartTick = -1;

    public GroupKillOncePattern(int count) {
        this(count, null, 10 * boundedCount(count));
    }

    public GroupKillOncePattern(int count, String title, int mioPoints) {
        this.count = boundedCount(count);
        this.title = title;
        this.mioPoints = Math.max(1, mioPoints);
    }

    @Override
    public void onChapterStart(GameWorld world, GraphicsEngine graphicsEngine, PhysicService physicService) {
        lastSeenDead = world == null ? 0 : world.getTotalDeadZombies();
        killsInWindow = 0;
        windowStartTick = -1;
    }

    @Override
    public void onTick(GameWorld world, long currentTick) {
        if (world == null) {
            return;
        }
        int currentDead = world.getTotalDeadZombies();
        int newlyDead = Math.max(0, currentDead - lastSeenDead);
        lastSeenDead = currentDead;

        if (newlyDead > 0) {
            if (windowStartTick < 0) {
                windowStartTick = currentTick;
                killsInWindow = newlyDead;
            } else {
                killsInWindow += newlyDead;
            }
        }

        if (windowStartTick < 0) {
            return;
        }

        if (killsInWindow >= count) {
            award(message(), mioPoints);
            resetWindow();
            return;
        }

        long elapsed = currentTick - windowStartTick;
        if (elapsed >= WINDOW_TICKS) {
            resetWindow();
        }
    }

    private String message() {
        if (title != null && !title.isBlank()) {
            return title + " +" + mioPoints + " Meow";
        }
        return count + " zombies down at once! +" + mioPoints + " Meow";
    }

    private void resetWindow() {
        killsInWindow = 0;
        windowStartTick = -1;
    }

    public int getCount() {
        return count;
    }
}
