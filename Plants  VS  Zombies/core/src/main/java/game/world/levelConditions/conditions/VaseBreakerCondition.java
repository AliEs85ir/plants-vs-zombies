package game.world.levelConditions.conditions;

import game.entity.obstacle.Obstacle;
import game.entity.obstacle.ability.VaseAbility;
import game.world.GameWorld;
import game.world.minigame.vasebreaker.VaseBreakerMode;

public class VaseBreakerCondition implements LevelCondition {
    private final NormalCondition normalLose = new NormalCondition();

    @Override
    public LevelOutcome evaluate(GameWorld world) {
        LevelOutcome loseCheck = normalLose.evaluate(world);
        if (loseCheck == LevelOutcome.PLAYER_LOST) {
            return LevelOutcome.PLAYER_LOST;
        }

        if (!allVasesCleared(world)) {
            return LevelOutcome.NONE;
        }
        if (!world.getZombies().isEmpty()
            || !world.getMap().getSpawnedZombiesDuringGame().isEmpty()) {
            return LevelOutcome.NONE;
        }
        return MinigameLevelOutcomes.cleared(world);
    }

    private boolean allVasesCleared(GameWorld world) {
        if (world.getObstacles() != null) {
            for (Obstacle obstacle : world.getObstacles()) {
                if (obstacle == null) {
                    continue;
                }
                for (var ability : obstacle.getAbilities()) {
                    if (ability instanceof VaseAbility) {
                        return false;
                    }
                }
            }
        }
        if (world.getCurrentChapter() != null
            && world.getCurrentChapter().getMinigameLogic() instanceof VaseBreakerMode mode
            && mode.hasWorld()
            && !mode.areAllVasesBroken()) {
            return false;
        }
        return true;
    }
}
