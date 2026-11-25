package graphics.plant;

import app.enums.plant.PlantName;
import graphics.general.animation.AnimationProfile;

public class PlantAnimationProfile extends AnimationProfile<PlantName, PlantVisualState, PlantAnimationVariant> {
    public PlantAnimationProfile(PlantName plantName, String pamPath) {
        super(plantName, pamPath, PlantVisualState.class);
    }
    public PlantName getPlantName() {
        return getKey();
    }
}
