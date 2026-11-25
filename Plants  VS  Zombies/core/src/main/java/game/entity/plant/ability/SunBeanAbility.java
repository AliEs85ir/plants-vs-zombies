package game.entity.plant.ability;

import app.model.App;
import game.engine.time.GameClock;
import common.model.Position;
import game.entity.plant.Plant;
import game.entity.plant.PlantDamage;
import game.entity.plant.upgrade.PlantLevelModifiers;
import game.entity.sun.Sun;
import game.entity.sun.SunToss;
import game.entity.zombie.Zombie;
import game.service.plant.PlantService;
import game.service.plant.PlantUpgradeService;
import game.service.sun.SunSpawner;
import game.world.GameWorld;
import game.world.playground.MapConfig;

import java.util.ArrayList;
import java.util.List;

public class SunBeanAbility extends AbstractPlantAbility {
    public static final int BASE_SUN_VALUE = 5;
    public static final int TOSS_DURATION_TICKS = Math.max(3, Math.round(0.40f * GameClock.TICKS_PER_SECOND));
    public static final double MIN_ANGLE_DEGREES = 35d;
    public static final double MAX_ANGLE_DEGREES = 145d;
    public static final double ANGLE_STEP_DEGREES = 23d;
    public static final double SPAWN_OFFSET_CELLS = 0.22d;
    public static final double MIN_LANDING_CELLS = 0.45d;
    public static final double MAX_LANDING_CELLS = 0.90d;
    public static final double MAX_SUN_DISTANCE_CELLS = 1.0d;
    public static final double ARC_HEIGHT_CELLS = 0.28d;
    public static final double MIN_SEPARATION_CELLS = 0.20d;
    public static final double DEFAULT_CELL_SIZE = 80d;

    private final List<Position> recentLandings = new ArrayList<>();
    private PlantService plantService;
    private int spawnIndex;
    private long lastProcessedDamageTick = Long.MIN_VALUE;

    public int getSpawnIndex() {
        return spawnIndex;
    }

    public int sunValue(Plant plant) {
        int extra = 0;
        if (plant != null && plant.getDefinition() != null && plant.getDefinition().getName() != null) {
            PlantLevelModifiers modifiers = PlantUpgradeService.getModifiers(plant.getDefinition().getName());
            extra = Math.max(0, modifiers.extraSunDrop());
        }
        return BASE_SUN_VALUE + extra;
    }

    @Override
    public void onDamaged(Plant plant, Zombie attacker, long currentTick) {
        if (!isValidDamage(plant, attacker, currentTick)) {
            return;
        }
        if (lastProcessedDamageTick == currentTick) {
            return;
        }
        lastProcessedDamageTick = currentTick;
        SunSpawner spawner = sunSpawner();
        if (spawner == null) {
            return;
        }
        spawnSun(plant, spawner, worldOf());
    }

    @Override
    protected void doUpdate(Plant plant, PlantService plantService, long currentTick) {
        this.plantService = plantService;
    }

    public Sun spawnSun(Plant plant, SunSpawner spawner, GameWorld world) {
        if (plant == null || plant.getPosition() == null || spawner == null) {
            return null;
        }
        SunToss toss = planToss(plant, world, spawnIndex++);
        rememberLanding(toss.landing());
        return spawner.spawnTossedPlantSun(toss, sunValue(plant), "plant " + plant.getName());
    }

    public SunToss planToss(Plant plant, GameWorld world, int index) {
        Position center = plant.getPosition();
        double cellW = cellWidth(plant);
        double cellH = cellHeight(plant);
        double angle = angleDegrees(index);
        Position spawn = offset(center, angle, SPAWN_OFFSET_CELLS * cellW, SPAWN_OFFSET_CELLS * cellH);
        double landingCells = landingDistanceCells(index);
        Position landing = offset(center, angle, landingCells * cellW, landingCells * cellH);
        landing = separateFromRecent(center, landing, cellW, cellH, index);
        landing = clampLanding(center, landing, world, cellW, cellH);
        double arcHeight = ARC_HEIGHT_CELLS * cellH;
        return new SunToss(spawn.getX(), spawn.getY(), landing.getX(), landing.getY(), arcHeight, TOSS_DURATION_TICKS);
    }

    public static double angleDegrees(int index) {
        double span = MAX_ANGLE_DEGREES - MIN_ANGLE_DEGREES;
        double stepped = MIN_ANGLE_DEGREES + Math.floorMod(index, 16) * ANGLE_STEP_DEGREES;
        while (stepped > MAX_ANGLE_DEGREES) {
            stepped -= span;
        }
        while (stepped < MIN_ANGLE_DEGREES) {
            stepped += span;
        }
        return stepped;
    }

    public static double landingDistanceCells(int index) {
        double span = MAX_LANDING_CELLS - MIN_LANDING_CELLS;
        return MIN_LANDING_CELLS + (Math.floorMod(index, 6) / 5.0d) * span;
    }

    private Position separateFromRecent(
        Position center,
        Position landing,
        double cellW,
        double cellH,
        int index
    ) {
        Position chosen = landing;
        double minSep = MIN_SEPARATION_CELLS * cellW;
        for (int attempt = 0; attempt < 6; attempt++) {
            boolean overlaps = false;
            for (Position previous : recentLandings) {
                if (distance(chosen, previous) < minSep) {
                    overlaps = true;
                    break;
                }
            }
            if (!overlaps) {
                return chosen;
            }
            double angle = angleDegrees(index + attempt + 1);
            double landingCells = landingDistanceCells(index + attempt + 1);
            chosen = offset(center, angle, landingCells * cellW, landingCells * cellH);
        }
        return chosen;
    }

    private Position clampLanding(
        Position center,
        Position landing,
        GameWorld world,
        double cellW,
        double cellH
    ) {
        Position clamped = landing;
        if (world != null && world.getGrid() != null && world.getGrid().getConfig() != null) {
            clamped = clampToPlayground(landing, world.getGrid().getConfig());
        }
        double maxDistance = MAX_SUN_DISTANCE_CELLS * cellW;
        double dx = clamped.getX() - center.getX();
        double dy = clamped.getY() - center.getY();
        double dist = Math.hypot(dx, dy);
        if (dist > maxDistance && dist > 0d) {
            double scale = maxDistance / dist;
            clamped = new Position(center.getX() + dx * scale, center.getY() + dy * scale);
        }
        return clamped;
    }

    private static Position clampToPlayground(Position position, MapConfig config) {
        double minX = config.getPlaygroundX();
        double maxX = config.getPlaygroundX() + config.getPlaygroundWidth();
        double minY = config.getPlaygroundY();
        double maxY = config.getPlaygroundY() + config.getPlaygroundHeight();
        double x = Math.max(minX, Math.min(maxX, position.getX()));
        double y = Math.max(minY, Math.min(maxY, position.getY()));
        return new Position(x, y);
    }

    private static Position offset(Position center, double angleDegrees, double distanceX, double distanceY) {
        double radians = Math.toRadians(angleDegrees);
        return new Position(
            center.getX() + Math.cos(radians) * distanceX,
            center.getY() + Math.sin(radians) * distanceY
        );
    }

    private static double distance(Position left, Position right) {
        return Math.hypot(left.getX() - right.getX(), left.getY() - right.getY());
    }

    private void rememberLanding(Position landing) {
        recentLandings.add(landing);
        if (recentLandings.size() > 8) {
            recentLandings.remove(0);
        }
    }

    private static boolean isValidDamage(Plant plant, Zombie attacker, long currentTick) {
        if (plant == null || plant.getHealth() == null) {
            return false;
        }
        PlantDamage damageState = plant.getPlantDamage();
        return damageState != null && damageState.getLastDamageTick() == currentTick
            && (attacker == null || damageState.getZombie() == attacker);
    }

    private static double cellWidth(Plant plant) {
        if (plant.getCell() != null && plant.getCell().getWidth() > 1f) {
            return plant.getCell().getWidth();
        }
        return DEFAULT_CELL_SIZE;
    }

    private static double cellHeight(Plant plant) {
        if (plant.getCell() != null && plant.getCell().getHeight() > 1f) {
            return plant.getCell().getHeight();
        }
        return DEFAULT_CELL_SIZE;
    }

    private SunSpawner sunSpawner() {
        if (plantService != null && plantService.getSunSpawner() != null) {
            return plantService.getSunSpawner();
        }
        GameWorld world = worldOf();
        return world == null ? null : new SunSpawner(world);
    }

    private GameWorld worldOf() {
        if (plantService != null && plantService.getGameWorld() != null) {
            return plantService.getGameWorld();
        }
        if (App.getCurrentGameEngine() != null) {
            return App.getCurrentGameEngine().getGameWorld();
        }
        return null;
    }
}
