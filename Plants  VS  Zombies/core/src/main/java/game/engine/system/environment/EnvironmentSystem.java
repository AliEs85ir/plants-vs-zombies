package game.engine.system.environment;

import game.engine.time.TickSystem;
import game.entity.obstacle.Obstacle;
import game.service.obstacle.ObstacleService;
import game.service.season.SeasonService;
import game.world.GameWorld;
import game.world.playground.Cell;
import game.world.playground.Grid;

import java.util.ArrayList;
import java.util.List;

public class EnvironmentSystem implements TickSystem {
    private final GameWorld world;
    private final ObstacleService obstacleService;
    private final SeasonService seasonService;
    private final List<Obstacle> obstacles;

    public EnvironmentSystem(GameWorld world) {
        this.world = world;
        this.obstacleService = new ObstacleService(world);
        this.seasonService = new SeasonService(world);
        this.obstacles = world.getObstacles();
    }

    @Override
    public void update(long currentTick) {
        world.evaluateOutcome();
        for(Obstacle obstacle : new ArrayList<>(obstacles)) {
            obstacle.update(currentTick, obstacleService);
        }
        world.getObstacles().removeIf(Obstacle::isDead);

        world.getSeason().updateEnvironment(currentTick, seasonService);
    }

}
