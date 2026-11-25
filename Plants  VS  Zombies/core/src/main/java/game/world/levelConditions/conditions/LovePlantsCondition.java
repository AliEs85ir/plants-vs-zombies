package game.world.levelConditions.conditions;

import game.entity.plant.Plant;
import game.world.GameMap;
import game.world.GameWorld;
import game.world.chapter.rule.ChapterRule;
import game.world.chapter.rule.speciallevelRules.LovePlantsRules;
import game.world.playground.Cell;

public class LovePlantsCondition implements LevelCondition{

    @Override
    public LevelOutcome evaluate(GameWorld world) {
        if (isAllPlantsKilled(world)) {
            return LevelOutcome.PLAYER_LOST;
        }
        if (allWavesCleared(world)) {
            return LevelOutcome.PLAYER_WON;
        }
        return LevelOutcome.NONE;
    }

    private boolean isAllPlantsKilled(GameWorld world) {
        LovePlantsRules rule = findRule(world);
        return rule.getCount() == 0;
    }

    private LovePlantsRules findRule(GameWorld world) {
        for (ChapterRule rule : world.getCurrentChapter().getRules()) {
            if (rule instanceof LovePlantsRules lovePlants) {
                return lovePlants;
            }
        }
        return null;
    }

    private boolean allWavesCleared(GameWorld world) {
        GameMap map = world.getMap();
        return map.getCurrentWave().isFinalWave()
            && map.getZombies().isEmpty()
            && map.getSpawnedZombiesDuringGame().isEmpty();
    }
}
