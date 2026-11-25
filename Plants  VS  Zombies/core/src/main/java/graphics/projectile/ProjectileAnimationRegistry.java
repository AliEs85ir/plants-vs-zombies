package graphics.projectile;

import app.enums.plant.PlantName;
import graphics.general.animation.AnimationRegistry;

public class ProjectileAnimationRegistry
    extends AnimationRegistry<PlantName, ProjectileVisualState, ProjectileAnimationVariant, ProjectileAnimationProfile> {

    private static final PlantName[] PROJECTILE_PLANTS = {
        PlantName.PEASHOOTER,
        PlantName.REPEATER,
        PlantName.THREEPEATER,
        PlantName.SNOW_PEA,
        PlantName.ROTOBAGA,
        PlantName.PEA_POD,
        PlantName.SPLIT_PEA,
        PlantName.CITRON,
        PlantName.CAULIPOWER,
        PlantName.ELECTRIC_BLUEBERRY,
        PlantName.BOWLING_BULB,
        PlantName.CACTUS,
        PlantName.FIRE_PEASHOOTER,
        PlantName.TORCHWOOD,
        PlantName.STARFRUIT,
        PlantName.GOO_PEASHOOTER,
        PlantName.MEGA_GATLING_PEA,
        PlantName.SEA_SHROOM,
        PlantName.PUFF_SHROOM,
        PlantName.FUME_SHROOM,
        PlantName.CABBAGE_PULT,
        PlantName.KERNEL_PULT,
        PlantName.MELON_PULT,
        PlantName.WINTER_MELON,
        PlantName.PEPPER_PULT,
        PlantName.GRAPESHOT,
        PlantName.CAT_TAIL,
        PlantName.CAT_TAIL_MINT
    };

    public ProjectileAnimationRegistry() {
        registerInitialProfiles();
    }

    private void registerInitialProfiles() {
        for (PlantName plantName : PROJECTILE_PLANTS) {
            registerProfile(ProjectileAnimationProfileLoader.loadFor(plantName));
        }
    }
}
