package game.entity.plant.combat;

import common.model.Position;
import game.entity.plant.Plant;
import game.entity.plant.plantfood.PlantFoodShot;
import game.entity.projectile.ProjectileCapabilities;
import game.entity.projectile.ShooterProjectile;
import game.entity.registry.PlantFoodDefinitionRegistry;
import game.entity.sun.SunSpawnPoint;
import game.service.plant.PlantService;
import game.service.projectile.ProjectileFactory;
import game.service.sun.SunSourceLocator;

/**
 * Shared PAM-pixel spawn conversion and tick-8 launch gating for plant-food shots.
 */
public final class PlantFoodProjectileLaunch {
    private PlantFoodProjectileLaunch() {
    }

    public static Position spawnPosition(Plant plant, PlantFoodShot shot) {
        if (plant == null || plant.getPosition() == null || shot == null) {
            return null;
        }
        double offsetX = shot.getOffsetX();
        double offsetY = shot.getOffsetY();
        if (shot.isApplyFacing() && plant.facesLeft()) {
            offsetX = -offsetX;
        }
        Position center = plant.getPosition();
        if (shot.isFromPamVisualCenter()) {
            return SunSourceLocator.worldFromPamCenter(center, new SunSpawnPoint(offsetX, offsetY));
        }
        return new Position(center.getX() + offsetX, center.getY() + offsetY);
    }

    public static double angleDegrees(Plant plant, PlantFoodShot shot) {
        double angle = shot == null ? 0d : shot.getAngleDegrees();
        if (shot != null && shot.isApplyFacing() && plant != null && plant.facesLeft()) {
            return 180d - angle;
        }
        return angle;
    }

    /**
     * Exactly one launch at {@code delayTicks} (Citron: 8). Guards duplicate updates
     * on the same elapsed tick via {@code alreadyLaunched}.
     */

    public static ShooterProjectile spawnEmpoweredShot(Plant plant, PlantService plantService, PlantFoodShot shot, int damage, double speed) {
        if (plant == null || plant.isDead() || shot == null || plantService == null) {
            return null;
        }
        Position position = spawnPosition(plant, shot);
        if (position == null) {
            return null;
        }
        double angle = angleDegrees(plant, shot);
        ShooterProjectile projectile = ProjectileFactory.createShooterProjectile(plant, damage, position, speed, angle);
        ProjectileCapabilities capabilities = shot.getCapabilities();
        if (capabilities != null) {
            projectile.applyCapabilities(capabilities);
        }
        plantService.addProjectile(projectile);
        return projectile;
    }

    public static PlantFoodShot empoweredAttackShot(Plant plant) {
        if (plant == null || plant.getPlantFoodState() == null || !plant.getPlantFoodState().isActive()) {
            return null;
        }
        if (plant.getDefinition() == null) {
            return null;
        }
        var definition = PlantFoodDefinitionRegistry.get(plant.getDefinition().getName());
        if (definition == null) {
            return null;
        }
        return definition.getEmpoweredAttackShot();
    }

    public static boolean shouldLaunchOnce(long elapsedTicks, int delayTicks, boolean alreadyLaunched) {
        if (alreadyLaunched) {
            return false;
        }
        return elapsedTicks == delayTicks;
    }
}