package game.world.levelConditions.conditions;

import game.entity.zombie.Zombie;
import game.world.GameWorld;
import game.world.chapter.rule.ChapterRule;
import game.world.chapter.rule.speciallevelRules.DeadLineRules;
import game.world.playground.MapConfig;

public class DeadLineCondition implements LevelCondition {

    @Override
    public LevelOutcome evaluate(GameWorld world) {
        if (isZombieCrossedDeadline(world) || StageOutcomeRules.zombieReachedLoseLine(world)) {
            return LevelOutcome.PLAYER_LOST;
        }
        if (StageOutcomeRules.allWavesCleared(world)) {
            return LevelOutcome.PLAYER_WON;
        }
        return LevelOutcome.NONE;
    }

    private boolean isZombieCrossedDeadline(GameWorld world) {
        DeadLineRules rule = findRule(world);
        if (rule == null || world == null || world.getGrid() == null || world.getZombies() == null) {
            return false;
        }
        MapConfig config = world.getGrid().getConfig();
        if (config == null) {
            return false;
        }
        int redLine = rule.getDeadLineColumn();
        float redLineX = config.getPlaygroundX() + config.getCellWidth() * (redLine + 1);
        for (Zombie zombie : world.getZombies()) {
            if (zombie != null && zombie.getPosition() != null && zombie.getPosition().getX() < redLineX) {
                return true;
            }
        }
        return false;
    }

    private DeadLineRules findRule(GameWorld world) {
        if (world == null || world.getCurrentChapter() == null || world.getCurrentChapter().getRules() == null) {
            return null;
        }
        for (ChapterRule rule : world.getCurrentChapter().getRules()) {
            if (rule instanceof DeadLineRules deadLineRules) {
                return deadLineRules;
            }
        }
        return null;
    }
}
