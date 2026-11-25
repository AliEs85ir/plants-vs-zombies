package app.view.game.states;

import app.view.game.PlayScreen;
import app.view.game.pregame.chapter_popup.ChapterIntroPopup;
import app.view.game.pregame.chapter_popup.ZombieSelectionChapterPopup;
import app.view.game.pregame.zombie.ZombieSelectionState;

import java.util.List;

import game.audio.AudioCatalog;

public class ChapterZombieIntroState implements PlayScreenState{

    private ChapterIntroPopup activeIntroPopup;
    private ZombieSelectionState selectionState;

    @Override
    public void enter(PlayScreen screen) {
        selectionState = new ZombieSelectionState(screen.getChapter());
        screen.getHud().bindZombieSelection(selectionState);
        AudioCatalog.playChooseSeeds(screen.getChapter());


        activeIntroPopup = new ZombieSelectionChapterPopup(
            selectionState,
            screen.getPregameLayout(),
            new ZombieSelectionChapterPopup.Callback() {
                @Override
                public void onRandomChosen(List<String> selected) {
                    commitAndStartZombies(screen, selected);
                }

                @Override
                public void onManualChosen() {
                    screen.getStateMachine().changeState(new ZombieSelectionPhaseState(selectionState));
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
