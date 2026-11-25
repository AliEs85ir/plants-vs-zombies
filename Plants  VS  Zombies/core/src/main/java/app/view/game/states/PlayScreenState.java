package app.view.game.states;

import app.enums.plant.PlantName;
import app.model.App;
import app.view.game.PlayScreen;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import game.engine.pause.PauseReason;
import game.world.minigame.izombie.IZombieWorld;

import java.util.List;

public interface PlayScreenState extends State<PlayScreen> {
    @Override default void enter(PlayScreen screen) {}
    @Override default void update(PlayScreen screen) {}
    @Override default void exit(PlayScreen screen) {}
    @Override default boolean onMessage(PlayScreen screen, Telegram telegram) { return false; }

    default void commitAndStartPlants(PlayScreen screen, List<PlantName> finalPlants) {
        List<PlantName> sessionList = App.getPendingGameSession().getSelectedPlants();
        sessionList.clear();
        sessionList.addAll(finalPlants);

        if (App.getCurrentGameEngine() != null && App.getCurrentGameEngine().getGameWorld() != null) {
            App.getCurrentGameEngine().getGameWorld().loadSelectedGamePlants(finalPlants);
        }

        screen.getHud().finalizePlantCardBar(finalPlants);
        startGame(screen);
    }
    default void commitAndStartZombies(PlayScreen screen, List<String> finalZombies) {

        if (App.getCurrentGameEngine() != null && App.getCurrentGameEngine().getGameWorld() != null) {
            IZombieWorld world = (IZombieWorld) App.getCurrentGameEngine().getGameWorld();
            world.loadSelectedGameZombies(finalZombies);
        }

        screen.getHud().finalizeZombieCardBar(finalZombies);
        startGame(screen);
    }


    default void commitAndStartPlantingState(PlayScreen screen, List<PlantName> finalPlants) {
        List<PlantName> sessionList = App.getPendingGameSession().getSelectedPlants();
        sessionList.clear();
        sessionList.addAll(finalPlants);

        if (App.getCurrentGameEngine() != null && App.getCurrentGameEngine().getGameWorld() != null) {
            App.getCurrentGameEngine().getGameWorld().loadSelectedGamePlants(finalPlants);
        }

        screen.getHud().finalizePlantCardBar(finalPlants);
        screen.getStateMachine().changeState(new PlantingState());

    }

    default void startGame(PlayScreen screen) {
        screen.getStateMachine().changeState(new ReadyState());
        if (!screen.getEngine().isRunning()) {
            screen.getEngine().start();
        }
        screen.getEngine().resume(PauseReason.PRE_GAME);
    }


}
