package graphics.plant;

import app.enums.plant.PlantName;
import graphics.general.animation.AnimationRegistry;
import graphics.general.animation.loader.PlantAnimationProfileLoader;

public class PlantAnimationRegistry
    extends AnimationRegistry<
    PlantName,
    PlantVisualState,
    PlantAnimationVariant,
    PlantAnimationProfile
    > {

    public PlantAnimationRegistry() {
        registerInitialProfiles();
    }

    private void registerInitialProfiles() {
        for (PlantName plantName : PlantName.values()) {
            registerProfile(PlantAnimationProfileLoader.loadFor(plantName));
        }
    }

    public void replaceProfile(PlantAnimationProfile profile) {
        registerProfile(profile);
    }
}
