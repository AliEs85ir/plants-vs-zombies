package game.engine;

import app.enums.GameState;
import app.model.App;
import com.badlogic.gdx.physics.box2d.World;
import game.component.physic.BodyFactory;
import game.component.physic.PhysicService;
import game.engine.pause.PauseManager;
import game.engine.pause.PauseReason;
import game.engine.system.projectile.ProjectileFrameSync;
import game.engine.time.GameClock;
import game.entity.obstacle.ObstacleFactory;
import game.settings.GameSettings;
import game.world.GameWorld;
import graphics.GraphicsEngine;

public class RealTimeGameEngine {

    private final GameWorld world;
    private final GameClock clock;
    private final GameSimulationEngine simulationEngine;
    private final PhysicService physicService;
    private final GraphicsEngine graphicsEngine;
    private final ProjectileFrameSync projectileFrameSync;
    private final PauseManager pauseManager = new PauseManager();

    private boolean isRunning = false;

    public RealTimeGameEngine(GraphicsEngine graphicsEngine) {
        this.simulationEngine = App.getCurrentGameEngine();
        this.world = simulationEngine.getGameWorld();
        this.clock = simulationEngine.getGameClock();
        this.physicService = new PhysicService();
        this.projectileFrameSync = new ProjectileFrameSync(world);
        BodyFactory.initialize(getB2dWorld(), world);
        ObstacleFactory.initialize(world, graphicsEngine);
        this.graphicsEngine = graphicsEngine;

        RuntimeEntityInitializer.initializeExistingEntities(world, physicService, graphicsEngine);
    }

    public void start() {
        this.isRunning = true;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void update(float deltaSeconds) {
        if (!canAdvanceSimulation()) {
            return;
        }
        float scaledDelta = deltaSeconds * GameSettings.gameplay().gameSpeedMultiplier();
        graphicsEngine.advanceGroundTrackedZombiesBeforePhysics();
        physicService.updateWorld(scaledDelta);
        int ticks = clock.update(scaledDelta);
        if (ticks > 0) {
            simulationEngine.advanceTime(ticks);
        }
        projectileFrameSync.update(clock.getInterpolationAlpha());
    }

    public boolean canAdvanceSimulation() {
        return isRunning
            && !pauseManager.isPaused()
            && world.getGameState() != GameState.WON
            && world.getGameState() != GameState.LOST;
    }

    public PauseManager getPauseManager() {
        return pauseManager;
    }

    public void pause(PauseReason reason) {
        pauseManager.pause(reason);
    }

    public void resume(PauseReason reason) {
        pauseManager.resume(reason);
    }

    public void pause() {
        pauseManager.pause(PauseReason.PAUSE_MENU);
    }

    public void resume() {
        pauseManager.resume(PauseReason.PAUSE_MENU);
    }

    public boolean isPaused() {
        return pauseManager.isPaused();
    }

    public boolean isPausedBy(PauseReason reason) {
        return pauseManager.isPausedBy(reason);
    }

    public GameSimulationEngine getSimulationEngine() {
        return simulationEngine;
    }

    public GameWorld getWorld() {
        return world;
    }

    public GameClock getClock() {
        return clock;
    }

    public World getB2dWorld() {
        return physicService.getB2dWorld();
    }

    public GraphicsEngine getGraphicsEngine() {
        return graphicsEngine;
    }

    public PhysicService getPhysicService() {
        return physicService;
    }

}
