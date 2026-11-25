package game.world.chapter.rule.scoringgame;

import app.enums.GameState;
import game.component.physic.PhysicService;
import game.world.GameWorld;
import graphics.GraphicsEngine;

public class PerfectWavePattern extends ScoringPattern {
    private static final int MIO_POINTS = 50;
    private int trackedWave;
    private int plantsEatenAtWaveStart;
    private int killsAtWaveStart;
    private boolean awardedCurrentWave;

    @Override
    public void onChapterStart(GameWorld world, GraphicsEngine graphicsEngine, PhysicService physicService) {
        trackedWave = world == null ? 0 : world.getCurrentWaveNumber();
        plantsEatenAtWaveStart = plantsEaten(world);
        killsAtWaveStart = world == null ? 0 : world.getTotalDeadZombies();
        awardedCurrentWave = false;
    }

    @Override
    public void onTick(GameWorld world, long currentTick) {
        if (world == null) {
            return;
        }
        int wave = world.getCurrentWaveNumber();
        if (wave > trackedWave) {
            if (trackedWave > 0) {
                tryAward(world);
            }
            beginWave(world, wave);
        } else if (world.getGameState() == GameState.WON) {
            tryAward(world);
        }
    }

    private void beginWave(GameWorld world, int wave) {
        trackedWave = wave;
        plantsEatenAtWaveStart = plantsEaten(world);
        killsAtWaveStart = world.getTotalDeadZombies();
        awardedCurrentWave = false;
    }

    private void tryAward(GameWorld world) {
        if (awardedCurrentWave) {
            return;
        }
        if (world.getTotalDeadZombies() <= killsAtWaveStart) {
            return;
        }
        if (plantsEaten(world) > plantsEatenAtWaveStart) {
            awardedCurrentWave = true;
            return;
        }
        awardedCurrentWave = true;
        award("Perfect Wave! +" + MIO_POINTS + " Meow", MIO_POINTS);
    }
}
