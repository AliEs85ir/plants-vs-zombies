package graphics.npc;

import game.dialogue.Speaker;
import graphics.general.animation.AnimationProfile;

public class NpcAnimationProfile extends AnimationProfile<Speaker, NpcVisualState, NpcAnimationVariant> {
    public NpcAnimationProfile(Speaker speaker, String pamPath) {
        super(speaker, pamPath, NpcVisualState.class);
    }
}
