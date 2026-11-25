package game.world.minigame;

import game.world.GameWorld;
import game.world.seasons.Season;

public class MinigameWorld extends GameWorld {
    private final Minigame logic;

    public MinigameWorld(Minigame logic, Season season) {
        super();
        this.logic = logic;
        this.setCurrentSeason(season);
    }



    public Minigame getLogic() {
        return logic;
    }
}
