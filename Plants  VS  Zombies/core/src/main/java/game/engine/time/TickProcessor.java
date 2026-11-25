package game.engine.time;

import game.world.GameWorld;
import game.world.chapter.rule.ChapterRule;
import game.world.minigame.MinigameWorld;

import java.util.ArrayList;
import java.util.List;

// >>> Executes all registered tick systems.
public class TickProcessor
{
    // -> Properties
    private final List<TickSystem> systems = new ArrayList<>();
    private final GameWorld gameWorld;

    // -> Constructor
    public TickProcessor(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }

    // -> Methods
    // Processes one game tick.
    public void update(long currentTick)
    {
        for (TickSystem system : systems) {
            system.update(currentTick);
        }

        if (gameWorld.getCurrentChapter() != null) {
            for (ChapterRule rule : gameWorld.getCurrentChapter().getRules()) {
                rule.onTick(gameWorld, currentTick);
            }
        }

        if (gameWorld instanceof MinigameWorld) {
            ((MinigameWorld) gameWorld).getLogic().onTick(gameWorld, currentTick);
        }
    }

    // Registers a new system.
    public void addSystem(TickSystem system)
    {
        if(system != null && !systems.contains(system)) {
            systems.add(system);
        }
    }

    // Removes a registered system.
    public void removeSystem(Class<? extends TickSystem> systemClass) {
        systems.removeIf(systemClass::isInstance);
    }

    public <T extends TickSystem> T findSystem(Class<T> systemClass) {
        if (systemClass == null) {
            return null;
        }
        for (TickSystem system : systems) {
            if (systemClass.isInstance(system)) {
                return systemClass.cast(system);
            }
        }
        return null;
    }
}
