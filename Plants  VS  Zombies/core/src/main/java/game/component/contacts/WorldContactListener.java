package game.component.contacts;

import app.model.App;
import com.badlogic.gdx.physics.box2d.*;
import game.entity.Entity;
import game.component.contacts.LawnMowerZombieContact;
import game.entity.lawnmower.LawnMower;
import game.entity.obstacle.Obstacle;
import game.entity.plant.Plant;
import game.entity.projectile.Projectile;
import game.entity.zombie.Zombie;
import game.world.playground.Grid;

public class WorldContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Object userA = contact.getFixtureA().getBody().getUserData();
        Object userB = contact.getFixtureB().getBody().getUserData();

        Entity A = userA instanceof Entity ? (Entity) userA : null;
        Entity B = userB instanceof Entity ? (Entity) userB : null;
        if (A == null || B == null) {
            return;
        }

        if (!sameLane(A, B)) {
            return;
        }

        if (userA instanceof Zombie && userB instanceof Plant) {
            handleBeginContact((Zombie) userA, (Plant) userB);
        } else if (userA instanceof Plant && userB instanceof Zombie) {
            handleBeginContact((Zombie) userB, (Plant) userA);
        }

        if (userA instanceof Projectile && userB instanceof Zombie) {
            handleProjectileHit((Projectile) userA, (Zombie) userB);
        } else if (userA instanceof Zombie && userB instanceof Projectile) {
            handleProjectileHit((Projectile) userB, (Zombie) userA);
        }
        if (userA instanceof Projectile && userB instanceof Obstacle) {
            handleProjectileHit((Projectile) userA, (Obstacle) userB);
        } else if (userA instanceof Obstacle && userB instanceof Projectile) {
            handleProjectileHit((Projectile) userB, (Obstacle) userA);
        }

        if (userA instanceof LawnMower && userB instanceof Zombie) {
            handleBeginContact((LawnMower) userA, (Zombie) userB);
        } else if (userA instanceof Zombie && userB instanceof LawnMower) {
            handleBeginContact((LawnMower) userB, (Zombie) userA);
        }

        if (userA instanceof Plant && userB instanceof Obstacle) {
            handleBeginContact((Plant) userA, (Obstacle) userB);
        } else if (userA instanceof Obstacle && userB instanceof Plant) {
            handleBeginContact((Plant) userB, (Obstacle) userA);
        }

        if (userA instanceof Zombie && userB instanceof Obstacle) {
            handleBeginContact((Obstacle) userB, (Zombie) userA);
        } else if (userA instanceof Obstacle && userB instanceof Zombie) {
            handleBeginContact((Obstacle) userA, (Zombie) userB);
        }
    }

    private void handleBeginContact(Zombie zombie, Plant plant) {
        if (zombie.isDead() || plant.isDead()) {
            return;
        }
        Plant target = ZombiePlantContact.resolveTarget(plant);
        if (target != null && !target.isDead()) {
            zombie.setTargetPlant(target);
        }
    }

    private void handleProjectileHit(Projectile projectile, Zombie zombie) {
        if (projectile.isActive() && !zombie.isDead()) {
            long currentTick = App.getCurrentGameEngine() != null
                ? App.getCurrentGameEngine().getGameClock().getCurrentTick()
                : 0;
            projectile.handleHit(zombie, currentTick);
        }
    }

    private void handleProjectileHit(Projectile projectile, Obstacle obstacle) {
        if (projectile.isActive() && !obstacle.isDead() && obstacle.isDamageable()) {
            long currentTick = App.getCurrentGameEngine() != null
                ? App.getCurrentGameEngine().getGameClock().getCurrentTick()
                : 0;
            projectile.handleHit(obstacle, currentTick);
        }
    }

    private void handleBeginContact(LawnMower lawnMower, Zombie zombie) {
        LawnMowerZombieContact.registerHit(lawnMower, zombie);
    }

    private void handleBeginContact(Plant plant, Obstacle obstacle) {
        if (plant != null && !plant.isDead()) {
            obstacle.setTargetPlant(plant);
        }
    }

    private void handleBeginContact(Obstacle obstacle, Zombie zombie) {
        ObstacleZombieContact.registerHit(obstacle, zombie);
    }

    @Override
    public void endContact(Contact contact) {
        Object userA = contact.getFixtureA().getBody().getUserData();
        Object userB = contact.getFixtureB().getBody().getUserData();

        if (userA instanceof Zombie && userB instanceof Plant) {
            handleEndContact((Zombie) userA, (Plant) userB);
        } else if (userA instanceof Plant && userB instanceof Zombie) {
            handleEndContact((Zombie) userB, (Plant) userA);
        }
    }

    private void handleEndContact(Zombie zombie, Plant plant) {
        if (zombie.getTargetPlant() == plant && ZombiePlantContact.blockingPlant(zombie) == null) {
            zombie.setTargetPlant(null);
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold manifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {
    }

    private static boolean sameLane(Entity left, Entity right) {
        if (left.lineMatches(right)) {
            return true;
        }
        if (App.getCurrentGameEngine() == null || App.getCurrentGameEngine().getGameWorld() == null) {
            return false;
        }
        Grid grid = App.getCurrentGameEngine().getGameWorld().getGrid();
        if (grid == null) {
            return false;
        }
        int laneA = grid.getEntityLane(left);
        int laneB = grid.getEntityLane(right);
        return laneA >= 0 && laneA == laneB;
    }
}
