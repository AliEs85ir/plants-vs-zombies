package game.dialogue;

public final class DialogueLine {
    private final Speaker speaker;
    private final String text;

    public DialogueLine(Speaker speaker, String text) {
        this.speaker = speaker;
        this.text = text != null ? text : "";
    }

    public Speaker getSpeaker() {
        return speaker;
    }

    public String getText() {
        return text;
    }
}
