package graphics.zombie;

import graphics.general.animation.AnimationProfile;
import graphics.zombie.visibility.VisibilityConfig;

public class ZombieAnimationProfile extends AnimationProfile<String, ZombieVisualState, ZombieAnimationVariant> {
    private VisibilityConfig visibilityConfig;
    public ZombieAnimationProfile(String alias, String pamPath) {
        super(alias, pamPath, ZombieVisualState.class);
        this.visibilityConfig = new VisibilityConfig();
    }
    public ZombieAnimationProfile(String alias, String pamPath, VisibilityConfig visibilityConfig) {
        super(alias, pamPath, ZombieVisualState.class);
        this.visibilityConfig = visibilityConfig;
    }
    public String getAlias() {
        return getKey();
    }

    public VisibilityConfig getVisibilityConfig() {
        return visibilityConfig;
    }

    public void setVisibilityConfig(VisibilityConfig visibilityConfig) {
        this.visibilityConfig = visibilityConfig;
    }
}
