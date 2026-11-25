package game.world.minigame.vasebreaker;

import app.enums.obstacle.ObstacleType;
import game.world.minigame.MultiLevelMinigame;

public record VaseBreakerLevelConfig(
    int level,
    int randomVaseChance,
    int plantVaseChance,
    int gargantuarVaseChance,
    int randomVaseZombieChance
) {
    public static VaseBreakerLevelConfig forLevel(int level) {
        int clamped = clamp(level);
        return switch (clamped) {
            case 2 -> new VaseBreakerLevelConfig(2, 55, 20, 25, 60);
            case 3 -> new VaseBreakerLevelConfig(3, 45, 12, 43, 75);
            default -> new VaseBreakerLevelConfig(1, 60, 28, 12, 40);
        };
    }

    public ObstacleType rollVaseType(int roll) {
        int chance = Math.floorMod(roll, 100);
        if (chance < randomVaseChance) {
            return ObstacleType.RANDOM_VASE;
        }
        if (chance < randomVaseChance + plantVaseChance) {
            return ObstacleType.PLANT_VASE;
        }
        return ObstacleType.GARGANTUAR_VASE;
    }

    public static int clamp(int level) {
        if (level < 1) {
            return 1;
        }
        if (level > MultiLevelMinigame.TOTAL_LEVELS) {
            return MultiLevelMinigame.TOTAL_LEVELS;
        }
        return level;
    }
}
