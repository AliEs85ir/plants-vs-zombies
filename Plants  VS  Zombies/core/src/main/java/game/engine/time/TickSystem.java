package game.engine.time;

// >>> Represents a system that reacts to game ticks.
public interface TickSystem
{
    // Updates system for current tick.
    void update(long currentTick);
}
