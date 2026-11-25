package game.engine.pause;

public enum PauseReason {

    PRE_GAME(false),
    SELECTION(false),
    PAUSE_MENU(true),
    DIALOG(true),
    MINIGAME(false),
    TUTORIAL(true),
    CUTSCENE(false),
    SYSTEM(true);

    private final boolean dismissibleByPlayer;

    PauseReason(boolean dismissibleByPlayer) {
        this.dismissibleByPlayer = dismissibleByPlayer;
    }

    public boolean isDismissibleByPlayer() {
        return dismissibleByPlayer;
    }
}
