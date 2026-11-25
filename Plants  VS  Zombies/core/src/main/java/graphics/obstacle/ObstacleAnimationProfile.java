package graphics.obstacle;

import app.enums.obstacle.ObstacleType;
import graphics.general.animation.AnimationProfile;

public class ObstacleAnimationProfile extends AnimationProfile<ObstacleType, ObstacleVisualState, ObstacleAnimationVariant> {
    public ObstacleAnimationProfile(ObstacleType type, String pamPath) {
        super(type, pamPath, ObstacleVisualState.class);
    }
    public ObstacleType getType() {
        return getKey();
    }
}
