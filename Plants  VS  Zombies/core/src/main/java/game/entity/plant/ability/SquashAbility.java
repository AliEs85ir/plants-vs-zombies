package game.entity.plant.ability;

import app.enums.plant.PlantGameplayPhase;
import com.badlogic.gdx.physics.box2d.Fixture;
import common.model.Position;
import game.component.physic.PhysicService;
import game.engine.time.GameClock;
import game.entity.plant.Plant;
import game.entity.zombie.Zombie;
import game.service.plant.AttackService;
import game.service.plant.PlantService;
import game.world.GameWorld;
import game.world.playground.Cell;
import graphics.general.event.PlantVisualEventType;

public class SquashAbility extends AbstractPlantAbility {
    private static final int JUMP_TICKS = 12;
    private static final int TURN_TICKS = 6;
    private static final double DEFAULT_ARC_HEIGHT = 30;
    private static final double DEFAULT_ARC_DISTANCE_FACTOR = 0.25;
    private static final double DEFAULT_MIN_ARC_HEIGHT = 20;
    private static final double DEFAULT_MAX_ARC_HEIGHT = 100;

    private Zombie target;
    private Cell landingCell;
    private boolean turning;
    private boolean jumping;
    private boolean smashed;
    private long actionStartTick;
    private final Position startPosition = new Position(0, 0);
    private final Position impactPosition = new Position(0, 0);
    private int flightTick;
    private int durationTicks;
    private double peakHeight;
    private int lockedLaneMask = -1;

    @Override
    protected void doUpdate(Plant plant, PlantService plantService, long currentTick) {
        if (plant == null || smashed) {
            return;
        }
        GameWorld world = plantService.getGameWorld();
        if (jumping) {
            updateJump(plant, plantService, currentTick);
            return;
        }
        if (turning) {
            if (currentTick - actionStartTick >= TURN_TICKS) {
                turning = false;
                startJump(plant, currentTick);
            }
            return;
        }
        Zombie trigger = PlantTargeting.firstZombieInHorizontalRange(world, plant);
        if (trigger == null) {
            return;
        }
        target = trigger;
        landingCell = PlantTargeting.cellOf(world, trigger);
        Cell origin = PlantTargeting.cellOf(world, plant);
        boolean fromBehind = origin != null && landingCell != null && landingCell.getCol() < origin.getCol();
        plant.setFacing(fromBehind ? -1 : 1);
        plant.setGameplayPhase(PlantGameplayPhase.ATTACKING);
        if (fromBehind) {
            turning = true;
            actionStartTick = currentTick;
            plant.setGameplayPhase(PlantGameplayPhase.TURNING);
            PlantAbilitySupport.play(plant, PlantVisualEventType.TURN_TRIGGERED);
            return;
        }
        startJump(plant, currentTick);
    }

    @Override
    public void syncFrame(Plant plant, float interpolationAlpha) {
        if (!jumping || smashed || plant == null || durationTicks <= 0) {
            return;
        }
        float alpha = interpolationAlpha;
        if (alpha < 0f) {
            alpha = 0f;
        }
        if (alpha > 1f) {
            alpha = 1f;
        }
        float t = (flightTick + alpha) / durationTicks;
        if (t > 1f) {
            t = 1f;
        }
        applyArcPose(plant, t);
    }

    private void startJump(Plant plant, long currentTick) {
        jumping = true;
        actionStartTick = currentTick;
        plant.setGameplayPhase(PlantGameplayPhase.JUMPING);
        lockedLaneMask = plant.getLaneMask();
        Position current = plant.getPosition();
        if (current != null) {
            startPosition.setX(current.getX());
            startPosition.setY(current.getY());
        }
        if (landingCell != null) {
            impactPosition.setX(landingCell.getWorldX());
            impactPosition.setY(landingCell.getWorldY());
        } else if (target != null && target.getPosition() != null) {
            impactPosition.setX(target.getPosition().getX());
            impactPosition.setY(target.getPosition().getY());
        } else {
            impactPosition.setX(startPosition.getX());
            impactPosition.setY(startPosition.getY());
        }
        double travel = Math.hypot(
            impactPosition.getX() - startPosition.getX(),
            impactPosition.getY() - startPosition.getY()
        );
        durationTicks = JUMP_TICKS;
        peakHeight = resolvePeakHeight(travel);
        flightTick = 0;
        disableCollision(plant);
        applyArcPose(plant, 0f);
        PlantAbilitySupport.play(plant, PlantVisualEventType.ATTACK_TRIGGERED);
        game.audio.Sfx.squashJump();
    }

    private void updateJump(Plant plant, PlantService plantService, long currentTick) {
        if (lockedLaneMask >= 0) {
            plant.setLaneMask(lockedLaneMask);
        }
        flightTick++;
        if (flightTick >= durationTicks) {
            placeAt(plant, impactPosition.getX(), impactPosition.getY());
            smash(plant, plantService, currentTick);
            return;
        }
        applyArcPose(plant, flightTick / (float) durationTicks);
    }

    private void applyArcPose(Plant plant, float t) {
        placeAt(plant, arcX(t), arcY(t));
    }

    private double arcX(float t) {
        return startPosition.getX() + (impactPosition.getX() - startPosition.getX()) * t;
    }

    private double groundY(float t) {
        return startPosition.getY() + (impactPosition.getY() - startPosition.getY()) * t;
    }

    private double arcY(float t) {
        return groundY(t) + 4.0 * peakHeight * t * (1.0 - t);
    }

    private void placeAt(Plant plant, double x, double y) {
        plant.setPosition(new Position(x, y));
        if (plant.getBody() == null) {
            return;
        }
        float ppm = PhysicService.PIXEL_PER_METER;
        plant.getBody().setTransform((float) (x / ppm), (float) (y / ppm), 0);
        plant.getBody().setLinearVelocity(0, 0);
    }

    private double resolvePeakHeight(double pathLength) {
        double peak = DEFAULT_ARC_HEIGHT + pathLength * DEFAULT_ARC_DISTANCE_FACTOR;
        if (peak < DEFAULT_MIN_ARC_HEIGHT) {
            return DEFAULT_MIN_ARC_HEIGHT;
        }
        if (peak > DEFAULT_MAX_ARC_HEIGHT) {
            return DEFAULT_MAX_ARC_HEIGHT;
        }
        return peak;
    }

    private void disableCollision(Plant plant) {
        if (plant == null || plant.getBody() == null) {
            return;
        }
        var fixtures = plant.getBody().getFixtureList();
        for (int i = 0; i < fixtures.size; i++) {
            Fixture fixture = fixtures.get(i);
            if (fixture != null) {
                fixture.setSensor(true);
            }
        }
    }

    private void smash(Plant plant, PlantService plantService, long currentTick) {
        smashed = true;
        jumping = false;
        GameWorld world = plantService.getGameWorld();
        AttackService attackService = PlantAbilitySupport.attacks(world);
        PlantAbilitySupport.notifyExploded(plant);
        if (landingCell != null) {
            attackService.damageZombiesInCell(
                landingCell.getRow(),
                landingCell.getCol(),
                plant.getAttackDamage(),
                currentTick
            );
        } else if (target != null && !target.isDead()) {
            attackService.damageZombie(target, currentTick, plant.getAttackDamage());
        }
        PlantAbilitySupport.consumeLater(plant, plantService, currentTick, Math.max(1, GameClock.TICKS_PER_SECOND / 2));
    }
}
