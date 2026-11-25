package game.world.levelConditions.conditions;

import game.entity.lawnmower.LawnMower;
import game.entity.zombie.Zombie;
import game.world.GameMap;
import game.world.GameWorld;
import game.world.minigame.beghouled.BeghouledWorld;

import java.util.ArrayList;

public class BeghouledCondition implements LevelCondition {
    @Override
    public LevelOutcome evaluate(GameWorld world) {
        if (hasZombieAtTheHouse(world)) {
            return LevelOutcome.PLAYER_LOST;
        }
        if (!(world instanceof BeghouledWorld beghouled)) {
            return LevelOutcome.NONE;
        }
        if (!beghouled.hasReachedTarget()) {
            return LevelOutcome.NONE;
        }
        if (!beghouled.isWinArmed()) {
            killLiveZombies(world);
            beghouled.armWin();
        }
        return MinigameLevelOutcomes.cleared(world);
    }

    private boolean hasZombieAtTheHouse(GameWorld world) {
        GameMap map = world.getMap();
        for (Zombie zombie : map.getZombies()) {
            if (!map.hasCrossedLoseLine(zombie)) {
                continue;
            }
            int row = (int) zombie.getPosition().getY();
            LawnMower mower = map.getLawnMowerInRow(row);
            if (mower == null || mower.isUsed()) {
                return true;
            }
        }
        return false;
    }

    private void killLiveZombies(GameWorld world) {
        for (Zombie zombie : new ArrayList<>(world.getZombies())) {
            if (zombie != null && !zombie.isDead()) {
                zombie.die();
            }
        }
    }
}
