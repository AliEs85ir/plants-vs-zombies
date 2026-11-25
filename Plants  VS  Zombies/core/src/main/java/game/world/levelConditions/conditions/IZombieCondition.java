package game.world.levelConditions.conditions;

import app.enums.obstacle.ObstacleType;
import game.entity.obstacle.Obstacle;
import game.entity.zombie.Zombie;
import game.world.GameMap;
import game.world.GameWorld;
import game.world.minigame.izombie.IZombieRules;
import game.world.minigame.izombie.IZombieSide;
import game.world.minigame.izombie.IZombieWorld;

public class IZombieCondition implements LevelCondition {
    @Override
    public LevelOutcome evaluate(GameWorld world) {
        boolean houseReached = anyZombieReachedTheHouse(world);
        boolean zombiesGone = allZombiesWipedOut(world);
        IZombieSide side = IZombieRules.localSide();
        if (side.isZombies()) {
            if (houseReached) {
                return LevelOutcome.PLAYER_WON;
            }
            if (zombiesGone) {
                return LevelOutcome.PLAYER_LOST;
            }
            return LevelOutcome.NONE;
        }
        if (houseReached) {
            return LevelOutcome.PLAYER_LOST;
        }
        if (zombiesGone) {
            return LevelOutcome.PLAYER_WON;
        }
        return LevelOutcome.NONE;
    }

    private boolean anyZombieReachedTheHouse(GameWorld world) {
        for(Obstacle obstacle : world.getObstacles()) {
            if(obstacle.getType() == ObstacleType.BRAIN) {
                return false;
            }
        }
        return true;
    }

    private boolean allZombiesWipedOut(GameWorld world) {
        return ((IZombieWorld) world).isTimeUp();
    }
}
