package game.world.levelConditions.conditions;

import game.entity.lawnmower.LawnMower;
import game.entity.zombie.Zombie;
import game.world.GameMap;
import game.world.GameWorld;

public final class StageOutcomeRules {
    private StageOutcomeRules() {
    }

    public static boolean zombieReachedLoseLine(GameWorld world) {
        if (world == null || world.getMap() == null) {
            return false;
        }
        GameMap map = world.getMap();
        if (map.getZombies() == null) {
            return false;
        }
        for (Zombie zombie : map.getZombies()) {
            if (zombie == null || zombie.getPosition() == null) {
                continue;
            }
            if (zombie.persistAfterDeath() && zombie.isDead()) {
                continue;
            }
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

    public static boolean allWavesCleared(GameWorld world) {
        if (world == null || world.getMap() == null) {
            return false;
        }
        GameMap map = world.getMap();
        if (map.getCurrentWave() == null || !map.getCurrentWave().isFinalWave()) {
            return false;
        }
        boolean noLiving = map.getZombies() == null || map.getZombies().isEmpty();
        boolean noPending = map.getSpawnedZombiesDuringGame() == null
            || map.getSpawnedZombiesDuringGame().isEmpty();
        return noLiving && noPending;
    }
}
