package app.view.game.states;

import app.enums.plant.PlantName;
import app.view.game.PlayScreen;
import app.view.game.pregame.PlantSelectionState;
import app.view.game.pregame.chapter_popup.ChapterIntroPopup;
import app.view.game.pregame.chapter_popup.PlantSelectionChapterPopup;
import app.view.hud.PlantWhatYouGotHud;

import java.util.List;

import game.audio.AudioCatalog;

public class ChapterPlantIntroState implements PlayScreenState {
    private ChapterIntroPopup activeIntroPopup;
    private PlantSelectionState selectionState;

    @Override
    public void enter(PlayScreen screen) {
        selectionState = new PlantSelectionState(screen.getChapter());
        screen.getHud().bindPlantSelection(selectionState);
        AudioCatalog.playChooseSeeds(screen.getChapter());


        activeIntroPopup = new PlantSelectionChapterPopup(
            selectionState,
            screen.getPregameLayout(),
            new PlantSelectionChapterPopup.Callback() {
                @Override
                public void onRandomChosen(List<PlantName> selected) {

                    if(screen.getHud() instanceof PlantWhatYouGotHud) {
                        commitAndStartPlantingState(screen, selected);
                    }
                    else{
                        commitAndStartPlants(screen, selected);
                    }
                }

                @Override
                public void onRecommendedChosen(List<PlantName> selected) {
                    if(screen.getHud() instanceof PlantWhatYouGotHud) {
                        commitAndStartPlantingState(screen, selected);
                    }
                    else{
                        commitAndStartPlants(screen, selected);
                    }
                }

                @Override
                public void onManualChosen() {
                    screen.getStateMachine().changeState(new PlantSelectionPhaseState(selectionState));
                }
            }
        );
        screen.getHud().getStage().addActor(activeIntroPopup);
    }

    @Override
    public void exit(PlayScreen screen) {
        if (activeIntroPopup != null) {
            activeIntroPopup.close();
            activeIntroPopup = null;
        }
    }
}
