package graphics.npc;

import common.model.Position;
import game.dialogue.Speaker;

public class NpcPresence {
    private final Speaker speaker;
    private final Position position;
    private NpcVisualState visualState;
    private String talkClipName;

    public NpcPresence(Speaker speaker) {
        this.speaker = speaker;
        this.position = new Position(0, 0);
        this.visualState = NpcVisualState.ENTERING;
        this.talkClipName = speaker.getSmallTalkClip();
    }

    public Speaker getSpeaker() {
        return speaker;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(float x, float y) {
        position.setX(x);
        position.setY(y);
    }

    public NpcVisualState getVisualState() {
        return visualState;
    }

    public void setVisualState(NpcVisualState visualState) {
        this.visualState = visualState;
    }

    public String getTalkClipName() {
        return talkClipName;
    }

    public void setTalkClipName(String talkClipName) {
        this.talkClipName = talkClipName;
    }
}
