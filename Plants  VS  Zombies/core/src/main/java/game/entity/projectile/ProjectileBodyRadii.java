package game.entity.projectile;

import app.enums.plant.PlantName;

public final class ProjectileBodyRadii {
    private static final float DEFAULT_RADIUS = 15f;

    private ProjectileBodyRadii() {
    }

    public static float forPlant(PlantName plantName) {
        if (plantName == null) {
            return DEFAULT_RADIUS;
        }
        return switch (plantName) {
            case PUFF_SHROOM, SEA_SHROOM -> 10f;
            case KERNEL_PULT, STARFRUIT, CACTUS -> 12f;
            case PEASHOOTER, REPEATER, THREEPEATER, SNOW_PEA, SPLIT_PEA,
                 FIRE_PEASHOOTER, GOO_PEASHOOTER, MEGA_GATLING_PEA, PEA_POD -> 15f;
            case CABBAGE_PULT, PEPPER_PULT, ROTOBAGA -> 18f;
            case FUME_SHROOM -> 20f;
            case MELON_PULT, WINTER_MELON -> 24f;
            case CITRON, CAULIPOWER -> 28f;
            default -> DEFAULT_RADIUS;
        };
    }
}
