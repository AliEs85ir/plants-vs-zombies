package game.world.minigame.wallnut_bowling;

import game.entity.zombie.Zombie;
import game.world.minigame.Minigame;
import game.world.minigame.MinigameWorld;
import game.world.seasons.Season;


public class WallNutBowingWorld extends MinigameWorld {
    private final int redLineColumn = 2;

    public WallNutBowingWorld(Minigame logic, Season season) {
        super(logic, season);
    }

    public int getRedLineColumn() {
        return redLineColumn;
    }

}
