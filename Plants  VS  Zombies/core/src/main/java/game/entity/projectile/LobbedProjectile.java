package game.entity.projectile;

import app.enums.projectile.ProjectileType;
import app.model.App;
import common.model.Position;
import game.component.physic.PhysicService;
import game.engine.time.GameClock;
import game.entity.Entity;
import game.entity.obstacle.Obstacle;
import game.entity.plant.Plant;
import game.entity.plant.ability.PlantTargeting;
import game.entity.zombie.Zombie;
import game.world.GameWorld;
import game.world.playground.Grid;

public class LobbedProjectile extends Projectile {
    private static final double DEFAULT_ARC_HEIGHT = 30;
    private static final double DEFAULT_ARC_DISTANCE_FACTOR = 0.25;
    private static final double DEFAULT_MIN_ARC_HEIGHT = 20;
    private static final double DEFAULT_MAX_ARC_HEIGHT = 100;

    private double arcHeight;
    private double arcDistanceFactor;
    private double minArcHeight;
    private double maxArcHeight;
    private double peakHeight;
    private double leadScale;

    private final Zombie targetZombie;
    private final Position startPosition;
    private final Position impactPosition;
    private int flightTick;
    private int durationTicks;
    private int lockedLaneMask;
    private int targetLane;
    private boolean hasImpacted;
    private boolean trajectoryLocked;

    public LobbedProjectile(Plant owner, int damage, double speed, Zombie targetZombie, Position impactPosition) {
        super(owner, damage, speed, ProjectileType.LOB, 0);
        this.launched = false;
        this.targetZombie = targetZombie;
        this.startPosition = new Position(0, 0);
        this.impactPosition = copyPosition(impactPosition);
        this.arcHeight = DEFAULT_ARC_HEIGHT;
        this.arcDistanceFactor = DEFAULT_ARC_DISTANCE_FACTOR;
        this.minArcHeight = DEFAULT_MIN_ARC_HEIGHT;
        this.maxArcHeight = DEFAULT_MAX_ARC_HEIGHT;
        this.peakHeight = DEFAULT_ARC_HEIGHT;
        this.leadScale = 1.0;
        this.flightTick = 0;
        this.durationTicks = 1;
        this.targetLane = resolveTargetLane(targetZombie, impactPosition);
        this.lockedLaneMask = targetLane >= 0 ? (1 << targetLane) : -1;
        this.hasImpacted = false;
        this.trajectoryLocked = false;
        enableObstacleBypass();
    }

    @Override
    public void updateLaneMask() {
        if (lockedLaneMask >= 0 && !hasImpacted) {
            laneMask = lockedLaneMask;
            return;
        }
        super.updateLaneMask();
    }

    @Override
    public void update(GameWorld gameWorld, long currentTick) {
        super.update(gameWorld, currentTick);
        if (!active || !launched || hasImpacted) {
            return;
        }
        advanceAlongArc(gameWorld, currentTick);
    }

    @Override
    protected void launch() {
        if (body == null) {
            return;
        }
        startPhysicsPosition = body.getPosition().cpy();
        Position current = getPosition();
        if (current != null) {
            startPosition.setX(current.getX());
            startPosition.setY(current.getY());
        }
        lockPredictedImpact();
        trajectoryLocked = true;
        targetLane = resolveTargetLane(targetZombie, impactPosition);
        lockedLaneMask = targetLane >= 0 ? (1 << targetLane) : lockedLaneMask;
        if (lockedLaneMask >= 0) {
            laneMask = lockedLaneMask;
        }
        double travel = distance(startPosition, impactPosition);
        double pixelSpeed = Math.max(1, speed * PhysicService.PIXEL_PER_METER);
        durationTicks = Math.max(1, (int) Math.round(travel / (pixelSpeed * GameClock.SECONDS_PER_TICK)));
        peakHeight = resolvePeakHeight(travel);
        flightTick = 0;
        applyArcPose(0f);
    }

    private void advanceAlongArc(GameWorld gameWorld, long currentTick) {
        if (body == null) {
            return;
        }
        flightTick++;
        if (flightTick >= durationTicks) {
            placeAt(impactPosition.getX(), impactPosition.getY());
            body.setLinearVelocity(0, 0);
            impact(gameWorld, currentTick, null);
            return;
        }
        applyArcPose(flightTick / (float) durationTicks);
        if (hasLeftTargetLane(gameWorld)) {
            body.setLinearVelocity(0, 0);
            impact(gameWorld, currentTick, null);
        }
    }

    @Override
    public void syncFrame(float interpolationAlpha) {
        if (!active || !launched || hasImpacted || body == null || durationTicks <= 0) {
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
        applyArcPose(t);
    }

    private void applyArcPose(float t) {
        placeAt(arcX(t), arcY(t));
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

    private void placeAt(double x, double y) {
        float ppm = PhysicService.PIXEL_PER_METER;
        body.setTransform((float) (x / ppm), (float) (y / ppm), 0);
        body.setLinearVelocity(0, 0);
    }

    private void lockPredictedImpact() {
        if (trajectoryLocked || !isTargetValid()) {
            return;
        }
        double projectileSpeedPx = Math.max(1, speed * PhysicService.PIXEL_PER_METER);
        Position predicted = PlantTargeting.predictLanding(
            startPosition,
            targetZombie,
            projectileSpeedPx,
            leadScale
        );
        impactPosition.setX(predicted.getX());
        impactPosition.setY(predicted.getY());
    }

    private boolean isTargetValid() {
        if (targetZombie == null || targetZombie.isDead()) {
            return false;
        }
        return targetZombie.getPosition() != null;
    }

    private boolean hasLeftTargetLane(GameWorld gameWorld) {
        if (targetLane < 0 || gameWorld == null || gameWorld.getGrid() == null) {
            return false;
        }
        int currentLane = gameWorld.getGrid().getEntityLane(this);
        return currentLane < targetLane;
    }

    private void impact(GameWorld gameWorld, long currentTick, Zombie directHit) {
        if (hasImpacted) {
            return;
        }
        hasImpacted = true;
        if (directHit != null) {
            applyDamage(directHit, resolveDamage(), currentTick);
        }
        if (isSplash) {
            Position center = getPosition();
            if (center == null) {
                center = impactPosition;
            }
            GameWorld world = gameWorld;
            if (world == null && directHit != null) {
                world = directHit.getWorld();
            }
            applySplash(world, center, directHit, currentTick);
        }
        destroy();
    }

    private int resolveTargetLane(Zombie zombie, Position fallback) {
        Grid grid = currentGrid();
        if (grid == null) {
            return -1;
        }
        if (zombie != null && zombie.getPosition() != null) {
            return grid.getEntityLane(zombie);
        }
        if (fallback != null) {
            return grid.getEntityLane(fallback);
        }
        return -1;
    }

    private static Grid currentGrid() {
        if (App.getCurrentGameEngine() == null || App.getCurrentGameEngine().getGameWorld() == null) {
            return null;
        }
        return App.getCurrentGameEngine().getGameWorld().getGrid();
    }

    private double resolvePeakHeight(double pathLength) {
        double peak = arcHeight + pathLength * arcDistanceFactor;
        if (peak < minArcHeight) {
            return minArcHeight;
        }
        if (peak > maxArcHeight) {
            return maxArcHeight;
        }
        return peak;
    }

    private Position copyPosition(Position source) {
        if (source == null) {
            return new Position(0, 0);
        }
        return new Position(source.getX(), source.getY());
    }

    private double distance(Position first, Position second) {
        if (first == null || second == null) {
            return 0.0;
        }
        return Math.hypot(first.getX() - second.getX(), first.getY() - second.getY());
    }

    @Override
    public void handleHit(Zombie zombie, long currentTick) {
        if (!active || hasImpacted || zombie == null || zombie.isDead()) {
            return;
        }
        GameWorld world = zombie.getWorld();
        impact(world, currentTick, zombie);
    }

    @Override
    public void handleHit(Obstacle obstacle, long currentTick) {
    }


    public double getArcHeight() {
        return arcHeight;
    }

    public void setArcHeight(double arcHeight) {
        this.arcHeight = Math.max(0, arcHeight);
    }

    public double getArcDistanceFactor() {
        return arcDistanceFactor;
    }

    public void setArcDistanceFactor(double arcDistanceFactor) {
        this.arcDistanceFactor = Math.max(0, arcDistanceFactor);
    }

    public double getMinArcHeight() {
        return minArcHeight;
    }

    public void setMinArcHeight(double minArcHeight) {
        this.minArcHeight = Math.max(0, minArcHeight);
    }

    public double getMaxArcHeight() {
        return maxArcHeight;
    }

    public void setMaxArcHeight(double maxArcHeight) {
        this.maxArcHeight = Math.max(this.minArcHeight, maxArcHeight);
    }

    public double getLeadScale() {
        return leadScale;
    }

    public void setLeadScale(double leadScale) {
        this.leadScale = Math.max(0, leadScale);
    }

    public Zombie getTargetZombie() {
        return targetZombie;
    }

    public Position getImpactPosition() {
        return impactPosition;
    }
}
