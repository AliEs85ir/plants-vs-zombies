package graphics.sun;

import app.enums.sun.SunType;
import graphics.general.animation.AnimationProfile;

public class SunAnimationProfile extends AnimationProfile<SunType, SunVisualState, SunAnimationVariant> {
    public SunAnimationProfile(SunType type, String pamPath) {
        super(type, pamPath, SunVisualState.class);
    }

    public SunType getSunType() {
        return getKey();
    }
}
