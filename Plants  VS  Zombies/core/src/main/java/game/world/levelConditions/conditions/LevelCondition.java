package game.world.levelConditions.conditions;

import game.world.GameWorld;

public interface LevelCondition {
    LevelOutcome evaluate(GameWorld world);
}
