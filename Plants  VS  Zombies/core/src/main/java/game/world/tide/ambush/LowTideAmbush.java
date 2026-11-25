package game.world.tide.ambush;

import game.world.GameWorld;
import game.world.playground.Cell;
import game.world.tide.TideState;

import java.util.List;

/**
 * Triggered after a low-tide retreat exposes land. Implementations decide
 * which zombies appear; nothing here is tied to Mermaid Imp.
 */
public interface LowTideAmbush {
    void trigger(GameWorld world, TideState tideState, List<Cell> newlyExposedLand);
}
