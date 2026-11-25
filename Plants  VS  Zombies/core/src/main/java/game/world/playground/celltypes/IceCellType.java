package game.world.playground.celltypes;

import game.world.seasons.Color;
import game.world.seasons.Season;

import java.util.Random;

public class IceCellType extends CellType{

    private final Direction direction;

    public IceCellType() {
        Random random = new Random();
        direction = random.nextBoolean() ? Direction.UP : Direction.DOWN;
    }

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
        return Color.CYAN + "▒" + Color.RESET;
    }

    public Direction getDirection() {
        return direction;
    }
}
