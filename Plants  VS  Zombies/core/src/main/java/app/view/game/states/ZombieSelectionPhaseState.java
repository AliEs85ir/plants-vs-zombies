package app.view.game.states;

import app.view.game.PlayScreen;
import app.view.game.pregame.zombie.ZombieSelectionPopup;
import app.view.game.pregame.zombie.ZombieSelectionState;
import game.audio.AudioCatalog;
import game.engine.pause.PauseReason;

public class ZombieSelectionPhaseState implements PlayScreenState{
    private ZombieSelectionPopup activeSelectionPopup;
    private final ZombieSelectionState selectionState;

    public ZombieSelectionPhaseState(ZombieSelectionState selectionState) {
        this.selectionState = selectionState;
    }

    @Override
    public void enter(PlayScreen screen) {
        screen.getEngine().pause(PauseReason.SELECTION);
        AudioCatalog.playChooseSeeds(screen.getChapter());

        activeSelectionPopup = new ZombieSelectionPopup(
            selectionState,
            screen.getPregameLayout(),
            finalZombies -> commitAndStartZombies(screen, finalZombies)
        );
        screen.getHud().getStage().addActor(activeSelectionPopup);
    }

    @Override
    public void exit(PlayScreen screen) {
        screen.getEngine().resume(PauseReason.SELECTION);
        if (activeSelectionPopup != null) {
            activeSelectionPopup.close();
            activeSelectionPopup = null;
        }
        screen.getHud().bindZombieSelection(null);
    }
}
