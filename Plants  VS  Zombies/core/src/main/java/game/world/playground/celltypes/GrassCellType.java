package game.world.playground.celltypes;

import game.world.seasons.Season;

public class GrassCellType extends CellType {
    @Override
    public boolean canPlant() {
        return true;
    }

    @Override
    public boolean canZombieWalk() {
        return true;
    }

    @Override
    public String getDisplay(Season season) {
        return season.getGrassDisplay();
    }
}
