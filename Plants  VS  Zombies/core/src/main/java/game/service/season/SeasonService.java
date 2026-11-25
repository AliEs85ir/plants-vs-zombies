package game.service.season;

import game.world.GameWorld;

public class SeasonService {
    private GameWorld world;

    public SeasonService(GameWorld world) {
        this.world = world;
    }

    public GameWorld getWorld() {
        return world;
    }
}
