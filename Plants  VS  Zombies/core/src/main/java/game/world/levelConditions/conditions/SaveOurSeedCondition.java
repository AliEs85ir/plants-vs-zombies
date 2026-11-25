package game.world.levelConditions.conditions;

import game.entity.plant.Plant;
import game.world.GameWorld;
import game.world.chapter.rule.ChapterRule;
import game.world.chapter.rule.speciallevelRules.SaveOurSeedRules;
import game.world.playground.Cell;

public class SaveOurSeedCondition implements LevelCondition {

    @Override
    public LevelOutcome evaluate(GameWorld world) {
        if (StageOutcomeRules.zombieReachedLoseLine(world) || isSeedLost(world)) {
            return LevelOutcome.PLAYER_LOST;
        }
        if (StageOutcomeRules.allWavesCleared(world)) {
            return LevelOutcome.PLAYER_WON;
        }
        return LevelOutcome.NONE;
    }

    private boolean isSeedLost(GameWorld world) {
        SaveOurSeedRules rule = findRule(world);
        if (rule == null || rule.getProtectedCells() == null) {
            return false;
        }
        for (Cell cell : rule.getProtectedCells()) {
            if (cell == null) {
                return true;
            }
            Plant plant = cell.getPlant();
            if (plant == null || plant.isDead()) {
                return true;
            }
        }
        return false;
    }

    private SaveOurSeedRules findRule(GameWorld world) {
        if (world == null || world.getCurrentChapter() == null || world.getCurrentChapter().getRules() == null) {
            return null;
        }
        for (ChapterRule rule : world.getCurrentChapter().getRules()) {
            if (rule instanceof SaveOurSeedRules saveOurSeedRules) {
                return saveOurSeedRules;
            }
        }
        return null;
    }
}
