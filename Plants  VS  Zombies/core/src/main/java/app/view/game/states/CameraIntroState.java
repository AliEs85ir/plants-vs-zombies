package app.view.game.states;

import app.view.game.PlayScreen;
import game.world.minigame.beghouled.BeghouledMode;
import game.world.minigame.beghouled.BeghouledUpgradeCatalog;
import game.world.minigame.izombie.IZombieRules;
import game.world.minigame.izombie_couchplay.IZombieCouchPlayRules;

public class CameraIntroState implements PlayScreenState {
    @Override
    public void update(PlayScreen screen) {
        if (screen.getCameraController().isIntroFinished()) {
            if (IZombieCouchPlayRules.matches(screen.getChapter())) {
                screen.getStateMachine().changeState(new CouchSelectionPhaseState());
            } else if (IZombieRules.matches(screen.getChapter())) {
                if (IZombieRules.localSide().isPlants()) {
                    screen.getStateMachine().changeState(new ChapterPlantIntroState());
                } else {
                    screen.getStateMachine().changeState(new ChapterZombieIntroState());
                }
            } else if (screen.getChapter().getMinigameLogic() instanceof BeghouledMode mode) {
                commitAndStartPlants(screen, BeghouledUpgradeCatalog.cardPlantsForLevel(mode.getLevel()));
            } else {
                screen.getStateMachine().changeState(new ChapterPlantIntroState());
            }
        }
    }
}
