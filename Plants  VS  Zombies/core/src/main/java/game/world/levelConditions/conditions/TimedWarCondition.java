package game.world.levelConditions.conditions;

import game.world.GameWorld;
import game.world.chapter.rule.speciallevelRules.TimedWarRules;

public class TimedWarCondition implements LevelCondition {
    private final NormalCondition fallback = new NormalCondition();

    @Override
    public LevelOutcome evaluate(GameWorld world) {
        LevelOutcome normal = fallback.evaluate(world);
        if (normal == LevelOutcome.PLAYER_LOST) {
            return LevelOutcome.PLAYER_LOST;
        }

        TimedWarRules timed = findTimedWar(world);
        if (timed != null && timed.getCurrentState() == TimedWarRules.RuleState.COMPLETED) {
            return LevelOutcome.PLAYER_WON;
        }
        if (normal == LevelOutcome.PLAYER_WON) {
            return LevelOutcome.PLAYER_WON;
        }
        return LevelOutcome.NONE;
    }

    private TimedWarRules findTimedWar(GameWorld world) {
        if (world.getCurrentChapter() == null || world.getCurrentChapter().getRules() == null) {
            return null;
        }
        for (var rule : world.getCurrentChapter().getRules()) {
            if (rule instanceof TimedWarRules timedWarRules) {
                return timedWarRules;
            }
        }
        return null;
    }
}
