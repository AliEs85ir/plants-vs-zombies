package graphics.projectile;

import app.enums.plant.PlantName;
import graphics.general.animation.AnimationProfile;

public class ProjectileAnimationProfile extends AnimationProfile<PlantName, ProjectileVisualState, ProjectileAnimationVariant> {
    public ProjectileAnimationProfile(PlantName plantName, String pamPath) {
        super(plantName, pamPath, ProjectileVisualState.class);
    }

    public PlantName getPlantName() {
        return getKey();
    }
}
