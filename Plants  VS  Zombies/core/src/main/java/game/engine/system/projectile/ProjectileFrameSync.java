package game.engine.system.projectile;

import game.entity.plant.Plant;
import game.entity.projectile.Projectile;
import game.world.GameWorld;

import java.util.List;

public class ProjectileFrameSync {
    private final GameWorld gameWorld;

    public ProjectileFrameSync(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }

    public void update(float interpolationAlpha) {
        if (gameWorld == null) {
            return;
        }
        List<Projectile> projectiles = gameWorld.getProjectiles();
        if (projectiles != null) {
            for (Projectile projectile : projectiles) {
                if (projectile != null && projectile.isActive()) {
                    projectile.syncFrame(interpolationAlpha);
                }
            }
        }
        List<Plant> plants = gameWorld.getPlants();
        if (plants != null) {
            for (Plant plant : plants) {
                if (plant != null && !plant.isDead()) {
                    plant.syncFrame(interpolationAlpha);
                }
            }
        }
    }
}
