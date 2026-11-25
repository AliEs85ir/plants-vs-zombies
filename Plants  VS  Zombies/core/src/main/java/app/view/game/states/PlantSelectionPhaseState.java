package app.view.game.states;

import app.view.game.PlayScreen;
import app.view.game.pregame.PlantSelectionPopup;
import app.view.game.pregame.PlantSelectionState;
import app.view.hud.PlantWhatYouGotHud;
import game.audio.AudioCatalog;
import game.engine.pause.PauseReason;

public class PlantSelectionPhaseState implements PlayScreenState {
    private PlantSelectionPopup activeSelectionPopup;
    private final PlantSelectionState selectionState;

    public PlantSelectionPhaseState(PlantSelectionState selectionState) {
        this.selectionState = selectionState;
    }

    @Override
    public void enter(PlayScreen screen) {
        screen.getEngine().pause(PauseReason.SELECTION);
        AudioCatalog.playChooseSeeds(screen.getChapter());

        activeSelectionPopup = new PlantSelectionPopup(
            selectionState,
            screen.getPregameLayout(),
            finalPlants -> {
                if(screen.getHud() instanceof PlantWhatYouGotHud) {
                    commitAndStartPlantingState(screen, finalPlants);
                }
                else{
                    commitAndStartPlants(screen, finalPlants);
                }
            }
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
        screen.getHud().bindPlantSelection(null);
    }
}
