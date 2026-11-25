package game.entity.projectile;

import app.enums.projectile.ProjectileType;
import common.model.Position;
import game.entity.obstacle.Obstacle;
import game.entity.plant.Plant;
import game.entity.zombie.Zombie;
import game.world.GameWorld;

public class BowlingProjectile extends Projectile {
    public BowlingProjectile(Plant ownerPlant, int damage, double speed, ProjectileType type, int delayOffset) {
        super(ownerPlant, damage, speed, type, delayOffset);
    }

    @Override
    public void handleHit(Obstacle obstacle, long currentTick) {

    }

    @Override
    protected void launch() {

    }

    @Override
    public void handleHit(Zombie zombie, long currentTick) {

    }

}
