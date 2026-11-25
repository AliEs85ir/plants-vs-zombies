package game.dialogue;

import java.util.List;

public final class DialogueScripts {
    private DialogueScripts() {}

    public static List<DialogueLine> firstChapter() {
        return List.of(
            new DialogueLine(Speaker.CRAZY_DAVE, "Wabby Wabbo!"),
            new DialogueLine(Speaker.CRAZY_DAVE, "Bling-blong! Plants on the lawn, brains in danger!"),
            new DialogueLine(Speaker.PENNY, "Those are zombies, Dave. Plant sunflowers to collect sun."),
            new DialogueLine(Speaker.PENNY, "Spend that sun on defenders, then tap a tile to plant them."),
            new DialogueLine(Speaker.CRAZY_DAVE, "Wooohooo!")
        );
    }

    public static List<DialogueLine> bossIntro() {
        return List.of(
            new DialogueLine(Speaker.ZOMBOSS, "Ahahaha! The plants think they can stop ME?"),
            new DialogueLine(Speaker.ZOMBOSS, "I am Dr. Edgar George Zomboss — and this lawn is already lost!"),
            new DialogueLine(Speaker.ZOMBOSS, "Crush them! Trample them! Leave no sunflower standing!"),
            new DialogueLine(Speaker.CRAZY_DAVE, "Wabby Wabbo! Big meanie zombie!"),
            new DialogueLine(Speaker.PENNY, "A boss zombie is on this lawn. Keep your defenses healthy and focus your fire."),
            new DialogueLine(Speaker.PENNY, "We should continue.")
        );
    }

    public static List<DialogueLine> bossOutro() {
        return List.of(
            new DialogueLine(Speaker.ZOMBOSS, "Impossible! My machine... my beautiful machine!"),
            new DialogueLine(Speaker.ZOMBOSS, "You think this is victory? It's a delay. I'll be back."),
            new DialogueLine(Speaker.ZOMBOSS, "Enjoy your lawn while you can, plants!")
        );
    }
}
