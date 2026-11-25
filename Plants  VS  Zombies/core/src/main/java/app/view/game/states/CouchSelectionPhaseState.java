package app.view.game.states;

import app.enums.plant.PlantName;
import app.model.App;
import app.view.game.PlayScreen;
import app.view.game.pregame.PlantSelectionState;
import app.view.game.pregame.PregameLayout;
import app.view.game.pregame.couch.CouchDualSelectionOverlay;
import app.view.game.pregame.zombie.ZombieSelectionState;
import game.audio.AudioCatalog;
import game.engine.pause.PauseReason;
import game.world.minigame.izombie.IZombieWorld;

import java.util.List;

public class CouchSelectionPhaseState implements PlayScreenState {
    private CouchDualSelectionOverlay overlay;
    private PlantSelectionState plantState;
    private ZombieSelectionState zombieState;

    @Override
    public void enter(PlayScreen screen) {
        screen.getEngine().pause(PauseReason.SELECTION);
        AudioCatalog.playChooseSeeds(screen.getChapter());

        plantState = new PlantSelectionState(screen.getChapter());
        zombieState = new ZombieSelectionState(screen.getChapter());
        screen.getHud().bindPlantSelection(plantState);
        screen.getHud().bindZombieSelection(zombieState);

        overlay = new CouchDualSelectionOverlay(
            plantState,
            zombieState,
            PregameLayout.couchPanel(),
            PregameLayout.couchPanel(),
            (plants, zombies) -> commitAndStart(screen, plants, zombies)
        );
        screen.getHud().getStage().addActor(overlay);
        screen.getHud().getStage().setKeyboardFocus(overlay);
    }

    private void commitAndStart(PlayScreen screen, List<PlantName> plants, List<String> zombies) {
        List<PlantName> sessionList = App.getPendingGameSession().getSelectedPlants();
        sessionList.clear();
        sessionList.addAll(plants);

        if (App.getCurrentGameEngine() != null && App.getCurrentGameEngine().getGameWorld() != null) {
            App.getCurrentGameEngine().getGameWorld().loadSelectedGamePlants(plants);
            if (App.getCurrentGameEngine().getGameWorld() instanceof IZombieWorld izombieWorld) {
                izombieWorld.loadSelectedGameZombies(zombies);
            }
        }

        screen.getHud().finalizePlantCardBar(plants);
        screen.getHud().finalizeZombieCardBar(zombies);
        startGame(screen);
    }

    @Override
    public void exit(PlayScreen screen) {
        screen.getEngine().resume(PauseReason.SELECTION);
        if (overlay != null) {
            overlay.close();
            overlay = null;
        }
        if (screen.getHud() != null && screen.getHud().getStage() != null) {
            screen.getHud().getStage().unfocusAll();
        }
        screen.getHud().bindPlantSelection(null);
        screen.getHud().bindZombieSelection(null);
    }
}
