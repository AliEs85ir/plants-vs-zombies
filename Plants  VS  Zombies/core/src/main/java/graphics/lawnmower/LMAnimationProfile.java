package graphics.lawnmower;

import app.enums.lawnmower.LawnMowerType;
import graphics.general.animation.AnimationProfile;

public class LMAnimationProfile extends AnimationProfile<LawnMowerType, LMVisualState, LMAnimationVariant> {
    public LMAnimationProfile(LawnMowerType type, String pamPath) {
        super(type, pamPath, LMVisualState.class);
    }
    public LawnMowerType getType() {
        return getKey();
    }
}
