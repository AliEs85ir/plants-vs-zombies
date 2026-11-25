package game.entity.zombie.boss.boss;

import app.model.App;
import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import common.model.Position;
import graphics.general.effects.EffectConditions;
import graphics.general.effects.EffectLayer;
import graphics.general.effects.EffectRequest;
import graphics.general.effects.anchers.WorldAnchor;
import game.component.physic.PhysicService;
import game.engine.time.GameClock;
import game.entity.plant.Plant;
import game.entity.zombie.ZombieFactory;
import game.entity.zombie.ability.ZombieAbility;
import game.entity.zombie.boss.ai.DarkDragonAI;
import game.entity.zombie.data.properties.Property;
import game.entity.zombie.strategy.attack.AttackStrategy;
import game.entity.zombie.strategy.movement.MovementStrategy;
import game.entity.zombie.strategy.takedamage.TakingDamageStrategy;
import game.service.Zombie.ZombieService;
import game.world.GameWorld;
import game.world.playground.Cell;
import game.world.playground.Grid;
import game.world.playground.MapConfig;

import java.util.ArrayList;
import java.util.List;

public class DarkDragon extends Zomboss {
    private static final String FIREBALL_TARGET_EFFECT_PHASE_1 = "dark dragon fireball target phase 1";
    private static final String FIREBALL_TARGET_EFFECT_PHASE_2 = "dark dragon fireball target phase 2";

    private BehaviorTree<DarkDragon> behaviorTree;
    private final Vector2 vertexScratch = new Vector2();
    private final List<Cell> pendingFireballTargets = new ArrayList<>();

    public DarkDragon(double currentHealth,
                      MovementStrategy movementStrategy,
                      AttackStrategy attackStrategy,
                      TakingDamageStrategy takingDamageStrategy,
                      Position position, List<ZombieAbility> abilities,
                      GameWorld world, Plant targetPlant,
                      Property propertyData) {

        super(currentHealth,
            movementStrategy,
            attackStrategy,
            takingDamageStrategy,
            position, abilities,
            world, targetPlant, propertyData);

        behaviorTree = DarkDragonAI.buildTree(this);
    }

    @Override
    public void update(ZombieService zombieService, long currentTick) {
        updateLaneMask();
        super.update(zombieService, currentTick);
        if (shouldEnterStun()) {
            behaviorTree.resetTask();
        }
        behaviorTree.step();
        clampBodyToPlayground();
    }

    @Override
    public void updateLaneMask() {
        super.updateLaneMask();
        Grid grid = getWorld().getGrid();
        Position positionDown = new Position(getPosition().getX(),
            getPosition().getY() - grid.getConfig().getCellHeight());
        int lane2 = getWorld().getGrid().getEntityLane(positionDown);
        laneMask |= (1 << lane2);
    }

    public double getBaseHealth() {
        return bossProperty.getInitialHealth();
    }

    public float bodyHalfWidthPixels() {
        return bodyHalfExtentPixels(true);
    }

    public float bodyHalfHeightPixels() {
        return bodyHalfExtentPixels(false);
    }

    public boolean canMoveInsideMap(int direction, float duration) {
        if (direction == 0) {
            return true;
        }
        MapConfig config = getWorld().getGrid().getConfig();
        float pixelSpeed = (float) bossProperty.getSpeed() * PhysicService.PIXEL_PER_METER;
        float futureY = (float) getPosition().getY() + pixelSpeed * duration * direction;
        float halfH = bodyHalfHeightPixels();
        float bottom = config.getPlaygroundY();
        float top = bottom + config.getPlaygroundHeight();
        return futureY - halfH >= bottom && futureY + halfH <= top;
    }

    public boolean canKeepMovingHorizontal(int xDirection, float speedMeters) {
        MapConfig config = getWorld().getGrid().getConfig();
        float x = (float) getPosition().getX();
        float halfW = bodyHalfWidthPixels();
        float step = Math.abs(speedMeters) * PhysicService.PIXEL_PER_METER * GameClock.SECONDS_PER_TICK;
        float nextX = x + xDirection * step;
        float left = config.getPlaygroundX();
        float right = left + config.getPlaygroundWidth();
        return nextX - halfW >= left && nextX + halfW <= right;
    }

    public void clampBodyToPlayground() {
        if (getBody() == null || getWorld() == null || getWorld().getGrid() == null) {
            return;
        }
        MapConfig config = getWorld().getGrid().getConfig();
        float halfW = bodyHalfWidthPixels();
        float halfH = bodyHalfHeightPixels();
        float x = (float) getPosition().getX();
        float y = (float) getPosition().getY();
        float minX = config.getPlaygroundX() + halfW;
        float maxX = config.getPlaygroundX() + config.getPlaygroundWidth() - halfW;
        float minY = config.getPlaygroundY() + halfH;
        float maxY = config.getPlaygroundY() + config.getPlaygroundHeight() - halfH;
        if (minX > maxX) {
            minX = maxX = config.getPlaygroundX() + config.getPlaygroundWidth() / 2f;
        }
        if (minY > maxY) {
            minY = maxY = config.getPlaygroundY() + config.getPlaygroundHeight() / 2f;
        }
        float clampedX = Math.max(minX, Math.min(maxX, x));
        float clampedY = Math.max(minY, Math.min(maxY, y));
        if (clampedX == x && clampedY == y) {
            return;
        }
        float ppm = PhysicService.PIXEL_PER_METER;
        getBody().setTransform(clampedX / ppm, clampedY / ppm, getBody().getAngle());
        getBody().setLinearVelocity(0f, 0f);
        setPosition(new Position(clampedX, clampedY));
    }

    private float bodyHalfExtentPixels(boolean horizontal) {
        if (getBody() != null && getBody().getFixtureList().size > 0) {
            Shape shape = getBody().getFixtureList().first().getShape();
            if (shape instanceof PolygonShape polygon && polygon.getVertexCount() > 0) {
                float min = Float.POSITIVE_INFINITY;
                float max = Float.NEGATIVE_INFINITY;
                for (int i = 0; i < polygon.getVertexCount(); i++) {
                    polygon.getVertex(i, vertexScratch);
                    float value = horizontal ? vertexScratch.x : vertexScratch.y;
                    min = Math.min(min, value);
                    max = Math.max(max, value);
                }
                return (max - min) * 0.5f * PhysicService.PIXEL_PER_METER;
            }
        }
        return horizontal ? 40f : ZombieFactory.DRAGON_HEIGHT;
    }

    public List<Cell> getPendingFireballTargets() {
        return pendingFireballTargets;
    }

    public void setPendingFireballTargets(List<Cell> targets) {
        pendingFireballTargets.clear();
        if (targets != null) {
            pendingFireballTargets.addAll(targets);
        }
    }

    public void clearPendingFireballTargets() {
        pendingFireballTargets.clear();
    }

    public void spawnFireballTargetEffect(Cell cell) {
        if (cell == null || App.getRealTimeEngine() == null || App.getRealTimeEngine().getGraphicsEngine() == null) {
            return;
        }
        String effectId = getCurrentPhase() == BossPhase.PHASE_1
            ? FIREBALL_TARGET_EFFECT_PHASE_1
            : FIREBALL_TARGET_EFFECT_PHASE_2;
        EffectRequest<Object> request = new EffectRequest<>(
            effectId,
            null,
            new WorldAnchor(cell.getWorldPosition()),
            EffectLayer.FRONT,
            EffectConditions.withinSeconds(3)
        );
        App.getRealTimeEngine().getGraphicsEngine().getEffectManager().spawn(request);
    }
}
