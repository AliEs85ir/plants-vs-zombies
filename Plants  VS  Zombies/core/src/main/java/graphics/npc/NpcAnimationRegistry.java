package graphics.npc;

import game.dialogue.Speaker;
import graphics.general.animation.AnimationRegistry;

import java.util.LinkedHashSet;
import java.util.Set;

public class NpcAnimationRegistry
    extends AnimationRegistry<Speaker, NpcVisualState, NpcAnimationVariant, NpcAnimationProfile> {

    public NpcAnimationRegistry() {
        for (Speaker speaker : Speaker.values()) {
            registerProfile(createProfile(speaker));
        }
    }

    private NpcAnimationProfile createProfile(Speaker speaker) {
        NpcAnimationProfile profile = new NpcAnimationProfile(speaker, speaker.getPamPath());
        profile.addVariant(
            NpcVisualState.ENTERING,
            clip(speaker.getEnterClip(), false)
        );
        profile.addVariant(
            NpcVisualState.IDLE,
            clip(speaker.getIdleClip(), true)
        );
        for (String talkClip : talkClips(speaker)) {
            profile.addVariant(NpcVisualState.TALKING, talkClip(talkClip));
        }
        profile.addVariant(
            NpcVisualState.LEAVING,
            clip(speaker.getLeaveClip(), false)
        );
        return profile;
    }

    private static Set<String> talkClips(Speaker speaker) {
        Set<String> clips = new LinkedHashSet<>();
        clips.add(speaker.getSmallTalkClip());
        clips.add(speaker.getMediumTalkClip());
        clips.add(speaker.getBlahBlahClip());
        if (speaker.hasCrazyBlahBlah()) {
            clips.add(speaker.getCrazyBlahBlahClip());
        }
        return clips;
    }

    private NpcAnimationVariant clip(String clipName, boolean loop) {
        return new NpcAnimationVariant(clipName, loop, 0, NpcAnimationTag.DEFAULT);
    }

    private NpcAnimationVariant talkClip(String clipName) {
        return new NpcAnimationVariant(
            clipName,
            true,
            0,
            NpcAnimationTag.DEFAULT,
            presence -> clipName.equals(presence.getTalkClipName())
        );
    }
}
