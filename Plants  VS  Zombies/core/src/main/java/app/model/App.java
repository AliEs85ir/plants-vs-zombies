package app.model;

import app.enums.Menu;
import app.enums.SeasonType;
import app.model.game.PendingGameSession;
import app.model.user.GameProgress;
import app.model.user.User;
import app.view.game.GameCameraController;
import game.engine.GameSimulationEngine;
import game.engine.RealTimeGameEngine;
import game.entity.plant.Plant;
import game.entity.zombie.Zombie;
import game.world.GameWorld;

import java.util.List;

public class App {
    private static Menu currentMenu;
    private static User currentUser;
    private static GameProgress currentProgress;
    private static final PendingGameSession pendingGameSession = new PendingGameSession();
    private static GameSimulationEngine currentGameEngine;
    private static RealTimeGameEngine realTimeEngine;
    private static GameCameraController cameraController;

    public static PendingGameSession getPendingGameSession() {
        return pendingGameSession;
    }

    public static GameSimulationEngine getCurrentGameEngine() {
        return currentGameEngine;
    }

    public static void setCurrentGameEngine(GameSimulationEngine engine) {
        currentGameEngine = engine;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static GameProgress getCurrentProgress() {
        return currentProgress;
    }

    public static Menu getCurrentMenu() {
        return currentMenu;
    }

    public static void setCurrentMenu(Menu menu) {
        currentMenu = menu;
    }

    public static void setCurrentProgress(GameProgress progress) {
        currentProgress = progress;
    }


    public static void setRealTimeEngine(RealTimeGameEngine engine) {
        realTimeEngine = engine;
        if (engine != null) {
            currentGameEngine = engine.getSimulationEngine();
        }
    }

    public static GameCameraController getCameraController() {
        return cameraController;
    }

    public static void setCameraController(GameCameraController cameraController) {
        App.cameraController = cameraController;
    }

    public static RealTimeGameEngine getRealTimeEngine() {
        return realTimeEngine;
    }

    public static GameWorld getCurrentWorld() {
        return realTimeEngine != null ? realTimeEngine.getWorld() : null;
    }
}
