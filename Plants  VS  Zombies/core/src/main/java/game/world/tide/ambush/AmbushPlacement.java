package game.world.tide.ambush;

import game.world.playground.Cell;

import java.util.List;

public interface AmbushPlacement {
    List<Cell> choose(List<Cell> newlyExposedLand, int count);
}
