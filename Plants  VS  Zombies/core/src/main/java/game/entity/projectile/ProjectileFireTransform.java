package game.entity.projectile;

import app.enums.ProjectilleEffect.ProjectileEffect;
import app.enums.plant.PlantName;
import app.model.App;
import game.entity.plant.Plant;
import game.entity.plant.ability.ModifierProjectileAbility;
import game.entity.plant.ability.PlantAbility;
import game.world.GameWorld;
import game.world.playground.Cell;
import game.world.playground.Grid;
import graphics.GraphicsEngine;

import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Set;

public final class ProjectileFireTransform {
    public static final int NORMAL_MULTIPLIER = 2;
    public static final int EMPOWERED_MULTIPLIER = 3;
    public static final float EPSILON_CELL_FRACTION = 0.08f;
    public static final float VERTICAL_CELL_FRACTION = 0.45f;
    public static final float DEFAULT_CELL_SIZE = 80f;

    private static final Set<PlantName> EXCLUDED_SOURCES = Collections.unmodifiableSet(EnumSet.of(
        PlantName.FIRE_PEASHOOTER,
        PlantName.CABBAGE_PULT,
        PlantName.KERNEL_PULT,
        PlantName.MELON_PULT,
        PlantName.WINTER_MELON,
        PlantName.PEPPER_PULT,
        PlantName.ELECTRIC_BLUEBERRY,
        PlantName.BOWLING_BULB,
        PlantName.CITRON,
        PlantName.GOO_PEASHOOTER
    ));

    private ProjectileFireTransform() {
    }

    public static Set<PlantName> excludedSources() {
        return EXCLUDED_SOURCES;
    }

    public static boolean isExcluded(PlantName source) {
        return source != null && EXCLUDED_SOURCES.contains(source);
    }

    public static boolean isEligible(Projectile projectile) {
        if (projectile == null || !projectile.isActive()) {
            return false;
        }
        if (projectile.isFireTransformed()) {
            return false;
        }
        return !isExcluded(projectile.getPlantName());
    }

    public static boolean crossesCenter(
        double prevX,
        double prevY,
        double currX,
        double currY,
        double torchX,
        double torchY,
        double epsilon,
        double verticalTolerance
    ) {
        double dx = currX - prevX;
        if (Math.abs(dx) < 0.0001d) {
            return Math.hypot(currX - torchX, currY - torchY) <= Math.max(epsilon, 0.0001d)
                && Math.abs(currY - torchY) <= verticalTolerance;
        }
        double lineX = torchX + (dx > 0d ? epsilon : -epsilon);
        boolean crossed = dx > 0d
            ? prevX < lineX && currX >= lineX
            : prevX > lineX && currX <= lineX;
        if (!crossed) {
            return false;
        }
        double t = (lineX - prevX) / dx;
        if (t < -0.05d || t > 1.05d) {
            return false;
        }
        double yAt = prevY + t * (currY - prevY);
        return Math.abs(yAt - torchY) <= verticalTolerance;
    }

    public static void applyAlongSegment(
        GameWorld world,
        Projectile projectile,
        double prevX,
        double prevY,
        double currX,
        double currY
    ) {
        if (world == null || !isEligible(projectile)) {
            return;
        }
        for (Plant plant : plantsAlongSegment(world, prevX, prevY, currX, currY)) {
            if (!isModifierPlant(plant) || plant.getPosition() == null) {
                continue;
            }
            if (crossesCenter(
                prevX,
                prevY,
                currX,
                currY,
                plant.getPosition().getX(),
                plant.getPosition().getY(),
                epsilon(plant),
                verticalTolerance(plant)
            )) {
                transform(projectile, plant);
                return;
            }
        }
    }

    public static boolean isModifierPlant(Plant plant) {
        if (plant == null || plant.isDead() || plant.getAbilities() == null) {
            return false;
        }
        for (PlantAbility ability : plant.getAbilities()) {
            if (ability instanceof ModifierProjectileAbility) {
                return true;
            }
        }
        return false;
    }

    public static void transform(Projectile projectile, Plant torchwood) {
        if (!isEligible(projectile) || torchwood == null) {
            return;
        }
        boolean empowered = torchwood.getPlantFoodState() != null && torchwood.getPlantFoodState().isActive();
        int multiplier = empowered ? EMPOWERED_MULTIPLIER : NORMAL_MULTIPLIER;
        projectile.setFireDamageMultiplier(multiplier);
        projectile.markFireTransformed();
        projectile.addEffect(ProjectileEffect.FIRE);
        projectile.removeEffect(ProjectileEffect.ICE);
        projectile.setAnimationPlantName(empowered ? PlantName.TORCHWOOD : PlantName.FIRE_PEASHOOTER);
        reloadVisual(projectile);
    }

    public static void thawPlantsAlongSegment(
        GameWorld world,
        Projectile projectile,
        double prevX,
        double prevY,
        double currX,
        double currY
    ) {
        if (world == null || projectile == null) {
            return;
        }
        if (projectile.getEffects() == null || !projectile.getEffects().contains(ProjectileEffect.FIRE)) {
            return;
        }
        for (Plant plant : plantsAlongSegment(world, prevX, prevY, currX, currY)) {
            if (plant == null || plant.isDead() || plant.getPosition() == null) {
                continue;
            }
            if (projectile.getHitPlants() != null && projectile.getHitPlants().contains(plant)) {
                continue;
            }
            if (plant.getFreezeLevel() <= 0) {
                continue;
            }
            if (crossesCenter(
                prevX,
                prevY,
                currX,
                currY,
                plant.getPosition().getX(),
                plant.getPosition().getY(),
                epsilon(plant),
                verticalTolerance(plant)
            )) {
                plant.reduceFreezeLevel();
                if (projectile.getHitPlants() != null) {
                    projectile.getHitPlants().add(plant);
                }
            }
        }
    }

    public static double epsilon(Plant plant) {
        return cellWidth(plant) * EPSILON_CELL_FRACTION;
    }

    public static double verticalTolerance(Plant plant) {
        return cellHeight(plant) * VERTICAL_CELL_FRACTION;
    }

    public static double cellWidth(Plant plant) {
        if (plant != null && plant.getCell() != null && plant.getCell().getWidth() > 1f) {
            return plant.getCell().getWidth();
        }
        GameWorld world = worldOf();
        if (world != null && world.getGrid() != null && world.getGrid().getConfig() != null) {
            return world.getGrid().getConfig().getCellWidth();
        }
        return DEFAULT_CELL_SIZE;
    }

    public static double cellHeight(Plant plant) {
        if (plant != null && plant.getCell() != null && plant.getCell().getHeight() > 1f) {
            return plant.getCell().getHeight();
        }
        GameWorld world = worldOf();
        if (world != null && world.getGrid() != null && world.getGrid().getConfig() != null) {
            return world.getGrid().getConfig().getCellHeight();
        }
        return DEFAULT_CELL_SIZE;
    }

    private static Set<Plant> plantsAlongSegment(
        GameWorld world,
        double prevX,
        double prevY,
        double currX,
        double currY
    ) {
        Set<Plant> plants = new LinkedHashSet<>();
        Grid grid = world.getGrid();
        if (grid == null || grid.getConfig() == null) {
            if (world.getPlants() != null) {
                plants.addAll(world.getPlants());
            }
            return plants;
        }
        float cellW = Math.max(1f, grid.getConfig().getCellWidth());
        float cellH = Math.max(1f, grid.getConfig().getCellHeight());
        double distance = Math.hypot(currX - prevX, currY - prevY);
        int steps = Math.max(1, (int) Math.ceil(distance / Math.min(cellW, cellH)));
        for (int i = 0; i <= steps; i++) {
            double t = i / (double) steps;
            float x = (float) (prevX + (currX - prevX) * t);
            float y = (float) (prevY + (currY - prevY) * t);
            addCellPlants(plants, grid.getCellAt(x, y));
            addCellPlants(plants, grid.getCellAt(x, y - cellH * 0.5f));
            addCellPlants(plants, grid.getCellAt(x, y + cellH * 0.5f));
        }
        return plants;
    }

    private static void addCellPlants(Set<Plant> plants, Cell cell) {
        if (cell == null || cell.getPlants() == null) {
            return;
        }
        plants.addAll(cell.getPlants());
    }

    private static void reloadVisual(Projectile projectile) {
        if (App.getRealTimeEngine() == null || App.getRealTimeEngine().getGraphicsEngine() == null) {
            return;
        }
        GraphicsEngine graphicsEngine = App.getRealTimeEngine().getGraphicsEngine();
        graphicsEngine.refreshProjectileVisual(projectile);
    }

    private static GameWorld worldOf() {
        if (App.getCurrentGameEngine() != null) {
            return App.getCurrentGameEngine().getGameWorld();
        }
        return null;
    }
}
