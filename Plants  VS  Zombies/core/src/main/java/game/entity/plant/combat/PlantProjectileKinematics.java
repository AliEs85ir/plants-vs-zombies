package game.entity.plant.combat;

import app.enums.plant.PlantName;

public final class PlantProjectileKinematics {
    private PlantProjectileKinematics() {
    }

    public static double speedOf(PlantName plantName) {
        return 6.5 * switch (plantName) {
            case PEASHOOTER, THREEPEATER, FIRE_PEASHOOTER, SPLIT_PEA, FUME_SHROOM -> 0.4;
            case REPEATER -> 0.35;
            case GOO_PEASHOOTER -> 0.6;
            case ROTOBAGA, SNOW_PEA, CAULIPOWER -> 0.5;
            case SEA_SHROOM -> 0.3;
            case CITRON -> 0.25;
            case CACTUS -> 0.6;
            case STARFRUIT -> 0.37;
            case MEGA_GATLING_PEA -> 1.0;
            case PUFF_SHROOM -> 0.32;
            case MELON_PULT, WINTER_MELON, CABBAGE_PULT -> 1.2;
            case PEPPER_PULT -> 1.3;
            case KERNEL_PULT -> 1.35;
            default -> 0.5;
        };
    }
}
