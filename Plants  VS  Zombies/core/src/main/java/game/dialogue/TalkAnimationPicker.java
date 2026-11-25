package game.dialogue;

import graphics.npc.NpcAnimationClips;

public final class TalkAnimationPicker {
    private final float smalltalkMaxSeconds;
    private final float mediumtalkMaxSeconds;
    private final float blahblahMaxSeconds;

    public TalkAnimationPicker() {
        this(
            DialogueConfig.SMALLTALK_MAX_SECONDS,
            DialogueConfig.MEDIUMTALK_MAX_SECONDS,
            DialogueConfig.BLAHBLAH_MAX_SECONDS
        );
    }

    public TalkAnimationPicker(
        float smalltalkMaxSeconds,
        float mediumtalkMaxSeconds,
        float blahblahMaxSeconds
    ) {
        this.smalltalkMaxSeconds = smalltalkMaxSeconds;
        this.mediumtalkMaxSeconds = mediumtalkMaxSeconds;
        this.blahblahMaxSeconds = blahblahMaxSeconds;
    }

    public String pick(Speaker speaker, float durationSeconds) {
        if (speaker == null) {
            return NpcAnimationClips.BLAHBLAH;
        }
        if (durationSeconds <= smalltalkMaxSeconds) {
            return speaker.getSmallTalkClip();
        }
        if (durationSeconds <= mediumtalkMaxSeconds) {
            return speaker.getMediumTalkClip();
        }
        if (durationSeconds <= blahblahMaxSeconds || !speaker.hasCrazyBlahBlah()) {
            return speaker.getBlahBlahClip();
        }
        return speaker.getCrazyBlahBlahClip();
    }
}
