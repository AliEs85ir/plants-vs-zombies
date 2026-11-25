package graphics.sun;

import app.enums.sun.SunType;
import graphics.general.animation.AnimationRegistry;
import graphics.general.animation.loader.SunAnimationProfileLoader;

public class SunAnimationRegistry
    extends AnimationRegistry<SunType, SunVisualState, SunAnimationVariant, SunAnimationProfile> {

    public SunAnimationRegistry() {
        registerInitialProfiles();
    }

    private void registerInitialProfiles() {
        SunAnimationProfile normal = SunAnimationProfileLoader.load("sun/normal.json");
        registerProfile(normal);
        registerProfile(SunAnimationProfileLoader.load("sun/special.json"));
        registerProfile(SunAnimationProfileLoader.load("sun/radioactive.json"));
    }
}
