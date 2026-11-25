package game.entity.obstacle.ability;

import app.view.game.PlayScreen;
import com.badlogic.gdx.math.Vector2;
import common.model.Position;
import game.component.physic.PhysicService;
import game.entity.obstacle.Obstacle;
import game.entity.plant.Plant;
import game.entity.zombie.Zombie;
import game.service.obstacle.ObstacleService;
import game.world.GameWorld;
import game.world.playground.Cell;
import graphics.general.effects.EffectInstance;
import graphics.general.event.ObstacleVisualEventType;

public class SquashAbility implements ObstacleAbility {
    private static final int JUMP_TICKS = 16;
    private static final float PEAK_HEIGHT = 90f;
    private static final float JUMP_RANGE_CELLS = 1.25f;

    private enum Phase { RIDING, JUMPING, DONE }

    private final Zombie myZombie;
    private final float followOffsetX;
    private final float followOffsetY;
    private final EffectInstance<?> idleEffect;

    private Phase phase = Phase.RIDING;
    private Plant targetPlant;
    private int lockedLaneMask;
    private float startX;
    private float startY;
    private float impactX;
    private float impactY;
    private int flightTick;
    private boolean falling;

    public SquashAbility(Zombie myZombie) {
        this(myZombie, new Vector2(-5f, 70f), null);
    }

    public SquashAbility(Zombie myZombie, Vector2 headOffset, EffectInstance<?> idleEffect) {
        this.myZombie = myZombie;
        this.followOffsetX = headOffset == null ? 5f : -headOffset.x;
        this.followOffsetY = headOffset == null ? 70f : headOffset.y;
        this.idleEffect = idleEffect;
    }

    @Override
    public void update(Obstacle obstacle, ObstacleService obstacleService, long currentTick) {
        if (obstacle.isDead() || phase == Phase.DONE) {
            return;
        }
        if (phase == Phase.RIDING) {
            updateRiding(obstacle, obstacleService);
            return;
        }
        updateJump(obstacle, obstacleService);
    }

    private void updateRiding(Obstacle obstacle, ObstacleService obstacleService) {
        if (myZombie == null || myZombie.isDead()) {
            obstacle.die();
            return;
        }
        obstacle.setLaneMask(myZombie.getLaneMask());
        followZombie(obstacle);

        Plant plant = resolveTarget(obstacle, obstacleService);
        if (plant == null) {
            return;
        }
        startJump(obstacle, plant);
    }

    private void startJump(Obstacle obstacle, Plant plant) {
        phase = Phase.JUMPING;
        targetPlant = plant;
        lockedLaneMask = myZombie != null ? myZombie.getLaneMask() : obstacle.getLaneMask();
        startX = (float) obstacle.getPosition().getX();
        startY = (float) obstacle.getPosition().getY();
        resolveImpact(plant);

        if (idleEffect != null) {
            idleEffect.forceFinish();
        }
        if (myZombie != null && !myZombie.isDead()) {
            myZombie.setTargetPlant(null);
            myZombie.setState(Zombie.ZombieState.USING_ABILITY);
            myZombie.stopMovement();
        }
        obstacle.stopMovement();
        obstacle.pushEvent(ObstacleVisualEventType.SQUASH_JUMP_UP);
        placeAt(obstacle, startX, startY);
    }

    private void updateJump(Obstacle obstacle, ObstacleService obstacleService) {
        if (lockedLaneMask != 0) {
            obstacle.setLaneMask(lockedLaneMask);
        }
        flightTick++;
        float t = flightTick / (float) JUMP_TICKS;
        if (t > 1f) {
            t = 1f;
        }
        if (!falling && t >= 0.5f) {
            falling = true;
            obstacle.pushEvent(ObstacleVisualEventType.SQUASH_JUMP_DOWN);
        }
        if (flightTick >= JUMP_TICKS) {
            placeAt(obstacle, impactX, impactY);
            smash(obstacle, obstacleService);
            return;
        }
        placeAt(obstacle, arcX(t), arcY(t));
    }

    private void smash(Obstacle obstacle, ObstacleService obstacleService) {
        phase = Phase.DONE;
        PlayScreen.shake(0.4f, 12f);
        GameWorld world = obstacleService.getWorld();
        if (targetPlant != null && !targetPlant.isDead()) {
            targetPlant.markDead(world);
        }
        if (myZombie != null && !myZombie.isDead()) {
            myZombie.die();
        }
        obstacle.die();
    }

    private Plant resolveTarget(Obstacle obstacle, ObstacleService obstacleService) {
        Plant contacted = livingPlant(obstacle.getTargetPlant());
        if (contacted != null) {
            return damageTarget(contacted);
        }
        if (myZombie != null) {
            Plant eating = livingPlant(myZombie.getTargetPlant());
            if (eating != null) {
                return damageTarget(eating);
            }
        }
        return nearestPlantInRange(obstacleService.getWorld());
    }

    private Plant nearestPlantInRange(GameWorld world) {
        if (world == null || myZombie == null || myZombie.getPosition() == null) {
            return null;
        }
        Cell zombieCell = world.getGrid().getCellAt(myZombie.getPosition());
        float jumpRange = zombieCell != null
            ? zombieCell.getWidth() * JUMP_RANGE_CELLS
            : 120f;
        int lane = myZombie.getLaneMask();
        Plant best = null;
        double bestDistance = Double.MAX_VALUE;
        for (Plant plant : world.getPlants()) {
            Plant candidate = livingPlant(plant);
            if (candidate == null || (candidate.getLaneMask() & lane) == 0) {
                continue;
            }
            candidate = damageTarget(candidate);
            if (candidate.getPosition() == null) {
                continue;
            }
            double dx = Math.abs(myZombie.getPosition().getX() - candidate.getPosition().getX());
            if (dx > jumpRange || dx >= bestDistance) {
                continue;
            }
            best = candidate;
            bestDistance = dx;
        }
        return best;
    }

    private void resolveImpact(Plant plant) {
        if (plant != null && plant.getCell() != null) {
            impactX = plant.getCell().getWorldX();
            impactY = plant.getCell().getWorldY();
            return;
        }
        if (plant != null && plant.getPosition() != null) {
            impactX = (float) plant.getPosition().getX();
            impactY = (float) plant.getPosition().getY();
            return;
        }
        impactX = startX;
        impactY = startY;
    }

    private void followZombie(Obstacle obstacle) {
        float x = (float) myZombie.getPosition().getX() + followOffsetX;
        float y = (float) myZombie.getPosition().getY() + followOffsetY;
        obstacle.stopMovement();
        placeAt(obstacle, x, y);
    }

    /**
     * Tent / inverted-V: {@code peak * (1 - |2t - 1|)}, which is {@code -|2x|}
     * shifted so t=0 and t=1 sit on the ground line and t=0.5 is the peak.
     */
    private float tent(float t) {
        return PEAK_HEIGHT * (1f - Math.abs(2f * t - 1f));
    }

    private float arcX(float t) {
        return startX + (impactX - startX) * t;
    }

    private float arcY(float t) {
        return startY + (impactY - startY) * t + tent(t);
    }

    private static void placeAt(Obstacle obstacle, float x, float y) {
        obstacle.setPosition(new Position(x, y));
        if (obstacle.getBody() == null) {
            return;
        }
        float ppm = PhysicService.PIXEL_PER_METER;
        obstacle.getBody().setTransform(x / ppm, y / ppm, 0f);
        obstacle.getBody().setLinearVelocity(0f, 0f);
    }

    private static Plant livingPlant(Plant plant) {
        if (plant == null || plant.isDead()) {
            return null;
        }
        return plant;
    }

    private static Plant damageTarget(Plant plant) {
        if (plant.getCell() != null && plant.getCell().getDamageTarget() != null) {
            Plant top = plant.getCell().getDamageTarget();
            if (top != null && !top.isDead()) {
                return top;
            }
        }
        return plant;
    }

    @Override
    public void onDeath(Obstacle obstacle) {
    }
}
