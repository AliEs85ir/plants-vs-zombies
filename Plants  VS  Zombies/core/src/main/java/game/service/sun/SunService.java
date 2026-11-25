package game.service.sun;

import game.world.GameWorld;

public class SunService {
    private final GameWorld gameWorld;
    private final SunSpawner sunSpawner;
    private final SunDropManager sunDropManager;
    public SunService(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
        this.sunSpawner = new SunSpawner(gameWorld);
        this.sunDropManager = new SunDropManager(gameWorld);
    }

    public SunSpawner getSunSpawner() {
        return sunSpawner;
    }
    public SunDropManager getSunDropManager() {
        return sunDropManager;
    }
}
