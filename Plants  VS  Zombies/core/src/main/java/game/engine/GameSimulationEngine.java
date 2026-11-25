package game.engine;

import app.enums.GameState;
import com.badlogic.gdx.math.Vector3;
import game.engine.system.environment.EnvironmentSystem;
import game.engine.system.lawnmower.LawnMowerSimulationSystem;
import game.engine.system.plant.PlantSimulationSystem;
import game.engine.system.projectile.ProjectileSimulationSystem;
import game.engine.system.sun.SunSimulationSystem;
import game.engine.system.wave.WaveSimulationSystem;
import game.engine.system.zombie.ZombieSimulationSystem;
import game.engine.time.GameClock;
import game.engine.time.ScheduledTaskManager;
import game.engine.time.TickProcessor;
import game.engine.time.TickSystem;
import game.entity.Entity;
import game.entity.registry.ZombieRegistry;
import game.service.Zombie.ZombieService;
import game.world.GameWorld;
import game.world.chapter.rule.ChapterRule;
import game.world.minigame.MinigameWorld;

// >>> Coordinates all time-based game simulations.
public class GameSimulationEngine
{
    // -> Properties
    private final GameWorld gameWorld; // Current game world
    private final GameClock gameClock; // Current game clock
    private final TickProcessor tickProcessor; // Tick execution manager
    private final ScheduledTaskManager taskManager;

    public GameSimulationEngine(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
        this.gameClock = new GameClock();
        this.gameClock.reset();
        this.tickProcessor = new TickProcessor(this.gameWorld);
        this.taskManager = new ScheduledTaskManager();
        loadSystems();

        if (gameWorld.getCurrentChapter() != null) {
            for (ChapterRule rule : gameWorld.getCurrentChapter().getRules()) {
                rule.onEngineStart(this);
            }
        }

        if (gameWorld instanceof MinigameWorld) {
            ((MinigameWorld) gameWorld).getLogic().onEngineStart(this);
        }
    }

    private void loadSystems()
    {
        ZombieService zombieService = new ZombieService(gameWorld);
        tickProcessor.addSystem(new SunSimulationSystem(gameWorld));
        tickProcessor.addSystem(new ZombieSimulationSystem(gameWorld));
        tickProcessor.addSystem(new WaveSimulationSystem(gameWorld, zombieService));
        tickProcessor.addSystem(new PlantSimulationSystem(gameWorld));
        tickProcessor.addSystem(new ProjectileSimulationSystem(gameWorld));
        tickProcessor.addSystem(new LawnMowerSimulationSystem(gameWorld));
        tickProcessor.addSystem(new EnvironmentSystem(gameWorld));
    }

    public void removeSystem(Class<? extends TickSystem> systemClass) {
        tickProcessor.removeSystem(systemClass);
    }

    public void addSystem(TickSystem system) {
        tickProcessor.addSystem(system);
    }

    // -> Methods
    // Advances game time.
    public void advanceTime(long ticks)
    {
        for (long i = 0; i < ticks; i++) {
            if (gameWorld.getGameState() == GameState.WON ||gameWorld.getGameState() == GameState.LOST) return;
            long currentTick = gameClock.advance();
            tickProcessor.update(currentTick);

            taskManager.executeTasks(currentTick);

            for(String event : gameWorld.getEvents()) {
                System.out.println("|EVENT LOG: tick #"+currentTick+"|  " + event);
                //System.out.println(event);
            }
            gameWorld.clearEventLog();
        }
    }

    // Returns current game world.
    public GameWorld getGameWorld()
    {
        return gameWorld;
    }

    // Returns current game clock.
    public GameClock getGameClock()
    {
        return gameClock;
    }

    public ScheduledTaskManager getTaskManager() {
        return taskManager;
    }

    public <T extends TickSystem> T findSystem(Class<T> systemClass) {
        return tickProcessor.findSystem(systemClass);
    }
}
