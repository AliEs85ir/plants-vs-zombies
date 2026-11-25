package game.service.sun;

import app.enums.sun.SunType;
import app.model.App;
import common.model.Position;
import game.engine.time.GameClock;
import game.entity.obstacle.Obstacle;
import game.entity.plant.Plant;
import game.entity.sun.PlantSun;
import game.entity.sun.Sun;
import game.entity.sun.SunLaunchSpec;
import game.entity.sun.SunToss;
import game.entity.zombie.Zombie;
import game.world.GameWorld;

import java.util.concurrent.ThreadLocalRandom;

public class SunSpawner {
    private final GameWorld gameWorld;
    private long lastSpawnTick;

    private static final float EXTRA_TOP_OFFSET = 180f;

    private static final float SPAWN_VERTICAL_BIAS = 0.7f;

    public SunSpawner(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }

    public Sun spawnSkySun(long tick) {
        if (tick - lastSpawnTick < 12 * GameClock.TICKS_PER_SECOND) return null;

        SunType type = randomType();
        Position position = randomPosition();
        int value = type == SunType.SPECIAL ? 100 : 25;

        Sun sun = SunFactory.createSkySun(type, position, value, tick);
        gameWorld.addSun(sun);

        gameWorld.addEvent(
            "New " + type.name().toLowerCase()
                + " sun is dropping at position (<"
                + (int) position.getX() + ">, <" + (int) position.getY() + ">)"
        );

        lastSpawnTick = tick;

        App.getRealTimeEngine().getGraphicsEngine().registerSun(sun);
        return sun;
    }

    public Sun spawnPlantSun(Plant plant, int amount) {
        return spawnPlantSun(plant, amount, 0);
    }

    public Sun spawnPlantSun(Plant plant, int amount, int sourceIndex) {
        if (plant == null || plant.getPosition() == null) {
            return null;
        }
        Position landing = randomLandingNear(plant.getPosition());
        SunLaunchSpec spec = SunLaunchCatalog.get(plant);
        boolean plantFood = SunLaunchCatalog.isPlantFoodActive(plant);
        SunSpawnRequest.Builder builder = SunSpawnRequest.builder(amount, landing)
            .producerLabel("plant " + plant.getName());
        if (spec != null && spec.hasLaunchSources(plantFood)) {
            SunToss planned = SunLaunchPlanner.plan(
                plant.getPosition(),
                spec,
                landing,
                sourceIndex,
                plantFood
            );
            if (planned != null) {
                builder.sourceWorld(planned.spawn())
                    .durationTicks(planned.getDurationTicks())
                    .arcHeight(planned.getArcHeight());
            }
        }
        return spawn(builder.build());
    }

    public Sun spawn(SunSpawnRequest request) {
        if (request == null || request.getLanding() == null) {
            return null;
        }
        if (request.isLaunch()) {
            int duration = request.getDurationTicks() != null
                ? request.getDurationTicks()
                : SunLaunchCatalog.DEFAULT_DURATION_TICKS;
            double arcHeight = request.getArcHeight() != null
                ? request.getArcHeight()
                : SunLaunchCatalog.DEFAULT_ARC_HEIGHT;
            Position source = request.getSourceWorld();
            Position landing = request.getLanding();
            SunToss toss = new SunToss(
                source.getX(),
                source.getY(),
                landing.getX(),
                landing.getY(),
                arcHeight,
                duration
            );
            return spawnTossedPlantSun(toss, request.getAmount(), request.getProducerLabel());
        }
        return spawnAt(request.getLanding(), request.getAmount(), request.getProducerLabel());
    }

    public Sun spawnTossedPlantSun(SunToss toss, int amount, String producerLabel) {
        if (toss == null) {
            return null;
        }
        Position spawn = toss.spawn();
        Sun sun = SunFactory.createPlantSun(spawn, amount);
        if (sun instanceof PlantSun plantSun) {
            plantSun.startToss(toss);
        }
        gameWorld.addSun(sun);
        gameWorld.addEvent(
            producerLabel
                + " produced a sun at (<"
                + (int) spawn.getX() + ">, <"
                + (int) spawn.getY() + ">)"
        );
        if (App.getRealTimeEngine() != null && App.getRealTimeEngine().getGraphicsEngine() != null) {
            App.getRealTimeEngine().getGraphicsEngine().registerSun(sun);
        }
        return sun;
    }

    public Sun spawnZombieSun(Zombie zombie, int amount) {
        return spawnNear(zombie.getPosition(), amount, "zombie " + zombie.getName());
    }

    public Sun spawnObstacleSun(Obstacle obstacle, int amount) {
        return spawnNear(obstacle.getPosition(), amount, "obstacle " + obstacle.getType());
    }

    private Sun spawnNear(Position origin, int amount, String producerLabel) {
        return spawnAt(randomLandingNear(origin), amount, producerLabel);
    }

    /**
     * Previous plant-sun landing: random offset in [-50, 50) around the producer.
     * Launch mode still uses this as the exact landing.
     */
    public Position randomLandingNear(Position origin) {
        int min = -50, max = 50;
        int randomInt1 = ThreadLocalRandom.current().nextInt(min, max);
        int randomInt2 = ThreadLocalRandom.current().nextInt(min, max);
        return new Position(origin.getX() + randomInt1, origin.getY() + randomInt2);
    }

    private Sun spawnAt(Position position, int amount, String producerLabel) {
        Sun sun = SunFactory.createPlantSun(position, amount);
        gameWorld.addSun(sun);

        gameWorld.addEvent(
            producerLabel
                + " produced a sun at (<"
                + (int) position.getX() + ">, <" + (int) position.getY() + ">)"
        );

        if (App.getRealTimeEngine() != null && App.getRealTimeEngine().getGraphicsEngine() != null) {
            App.getRealTimeEngine().getGraphicsEngine().registerSun(sun);
        }
        return sun;
    }

    private Position randomPosition() {
        var config = gameWorld.getGrid().getConfig();

        float x = config.getPlaygroundX()
            + ThreadLocalRandom.current().nextFloat() * config.getPlaygroundWidth();

        float minY = config.getPlaygroundY();
        float maxY = config.getPlaygroundY() + config.getPlaygroundHeight() + EXTRA_TOP_OFFSET;
        float range = maxY - minY;

        float t = ThreadLocalRandom.current().nextFloat();
        float y;

        if (SPAWN_VERTICAL_BIAS == 0) {
            y = minY + t * range;
        } else if (SPAWN_VERTICAL_BIAS > 0) {
            y = minY + range * (1f - (float) Math.pow(1f - t, SPAWN_VERTICAL_BIAS + 1f));
        } else {
            float exponent = -SPAWN_VERTICAL_BIAS + 1f;
            y = minY + range * (float) Math.pow(t, exponent);
        }

        return new Position(x, y);
    }

    private SunType randomType() {
        int r = ThreadLocalRandom.current().nextInt(100);
        if (r < 55) return SunType.NORMAL;
        if (r < 85) return SunType.SPECIAL;
        return SunType.RADIOACTIVE;
    }
}
