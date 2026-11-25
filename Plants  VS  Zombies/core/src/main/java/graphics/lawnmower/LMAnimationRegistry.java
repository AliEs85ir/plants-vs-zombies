package graphics.lawnmower;

import app.enums.lawnmower.LawnMowerType;
import graphics.general.animation.AnimationRegistry;
import graphics.general.animation.loader.LMAnimationProfileLoader;

public class LMAnimationRegistry
    extends AnimationRegistry<
        LawnMowerType,
        LMVisualState,
        LMAnimationVariant,
        LMAnimationProfile
        > {

    public LMAnimationRegistry() {
        registerInitialProfiles();
    }

    private void registerInitialProfiles() {
        LMAnimationProfile beach = LMAnimationProfileLoader.load("lawnmower/beach.json");
        LMAnimationProfile carnival = LMAnimationProfileLoader.load("lawnmower/carnival.json");
        LMAnimationProfile dark = LMAnimationProfileLoader.load("lawnmower/dark.json");
        LMAnimationProfile egypt = LMAnimationProfileLoader.load("lawnmower/egypt.json");
        LMAnimationProfile roman = LMAnimationProfileLoader.load("lawnmower/roman.json");
        LMAnimationProfile iceage = LMAnimationProfileLoader.load("lawnmower/iceage.json");

        registerProfile(beach);
        registerProfile(carnival);
        registerProfile(dark);
        registerProfile(egypt);
        registerProfile(roman);
        registerProfile(iceage);
    }
}
