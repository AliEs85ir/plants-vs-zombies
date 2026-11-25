package game.world.playground.celltypes;

import game.world.seasons.Season;

public abstract class CellType {

    public abstract boolean canPlant();

    public abstract boolean canZombieWalk();

    public abstract String getDisplay(Season season);
}
