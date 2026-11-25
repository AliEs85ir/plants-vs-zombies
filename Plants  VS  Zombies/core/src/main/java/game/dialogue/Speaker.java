package game.dialogue;

import graphics.npc.NpcAnimationClips;

public enum Speaker {
    CRAZY_DAVE(
        "Crazy Dave",
        Side.LEFT,
        "768/INITIAL/CRAZYDAVE/CRAZYDAVE/CRAZYDAVE.PAM",
        true
    ),
    PENNY(
        "Penny",
        Side.RIGHT,
        "768/INITIAL/WINNIE/WINNIE/WINNIE.PAM",
        false
    ),
    ZOMBOSS(
        "Dr. Zomboss",
        Side.RIGHT,
        "768/FULL/NPC/ZOMBOSS/ZOMBOSS.PAM",
        true,
        NpcAnimationClips.ZOMBOSS_ENTER,
        NpcAnimationClips.ZOMBOSS_IDLE,
        NpcAnimationClips.ZOMBOSS_TALK,
        NpcAnimationClips.ZOMBOSS_TALK,
        NpcAnimationClips.ZOMBOSS_TALK,
        NpcAnimationClips.ZOMBOSS_SHOUT,
        NpcAnimationClips.ZOMBOSS_EXIT
    );

    public enum Side {
        LEFT,
        RIGHT
    }

    private final String displayName;
    private final Side side;
    private final String pamPath;
    private final boolean hasCrazyBlahBlah;
    private final String enterClip;
    private final String idleClip;
    private final String smallTalkClip;
    private final String mediumTalkClip;
    private final String blahBlahClip;
    private final String crazyBlahBlahClip;
    private final String leaveClip;

    Speaker(String displayName, Side side, String pamPath, boolean hasCrazyBlahBlah) {
        this(
            displayName,
            side,
            pamPath,
            hasCrazyBlahBlah,
            NpcAnimationClips.ENTER,
            NpcAnimationClips.IDLE,
            NpcAnimationClips.SMALLTALK,
            NpcAnimationClips.MEDIUMTALK,
            NpcAnimationClips.BLAHBLAH,
            NpcAnimationClips.CRAZY_BLAHBLAH,
            NpcAnimationClips.LEAVE
        );
    }

    Speaker(
        String displayName,
        Side side,
        String pamPath,
        boolean hasCrazyBlahBlah,
        String enterClip,
        String idleClip,
        String smallTalkClip,
        String mediumTalkClip,
        String blahBlahClip,
        String crazyBlahBlahClip,
        String leaveClip
    ) {
        this.displayName = displayName;
        this.side = side;
        this.pamPath = pamPath;
        this.hasCrazyBlahBlah = hasCrazyBlahBlah;
        this.enterClip = enterClip;
        this.idleClip = idleClip;
        this.smallTalkClip = smallTalkClip;
        this.mediumTalkClip = mediumTalkClip;
        this.blahBlahClip = blahBlahClip;
        this.crazyBlahBlahClip = crazyBlahBlahClip;
        this.leaveClip = leaveClip;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Side getSide() {
        return side;
    }

    public String getPamPath() {
        return pamPath;
    }

    public boolean hasCrazyBlahBlah() {
        return hasCrazyBlahBlah;
    }

    public String getEnterClip() {
        return enterClip;
    }

    public String getIdleClip() {
        return idleClip;
    }

    public String getSmallTalkClip() {
        return smallTalkClip;
    }

    public String getMediumTalkClip() {
        return mediumTalkClip;
    }

    public String getBlahBlahClip() {
        return blahBlahClip;
    }

    public String getCrazyBlahBlahClip() {
        return crazyBlahBlahClip;
    }

    public String getLeaveClip() {
        return leaveClip;
    }
}
