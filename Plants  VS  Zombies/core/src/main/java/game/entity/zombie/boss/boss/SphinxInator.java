package game.entity.zombie.boss.boss;

import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import common.model.Position;
import game.component.physic.PhysicService;
import game.engine.time.GameClock;
import game.entity.plant.Plant;
import game.entity.zombie.ZombieFactory;
import game.entity.zombie.ability.ZombieAbility;
import game.entity.zombie.boss.ai.SphinxInatorAI;
import game.entity.zombie.data.properties.Property;
import game.entity.zombie.strategy.attack.AttackStrategy;
import game.entity.zombie.strategy.movement.MovementStrategy;
import game.entity.zombie.strategy.takedamage.TakingDamageStrategy;
import game.service.Zombie.ZombieService;
import game.world.GameWorld;
import game.world.playground.Cell;
import game.world.playground.Grid;
import game.world.playground.MapConfig;

import java.util.List;

public class SphinxInator extends Zomboss {
    private BehaviorTree<SphinxInator> behaviorTree;
    private final Vector2 vertexScratch = new Vector2();
    private Cell pendingMissileTarget;


    public SphinxInator(double currentHealth,
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
        behaviorTree = SphinxInatorAI.buildTree(this);
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
            getPosition().getY() + grid.getConfig().getCellHeight());
        int lane2 = getWorld().getGrid().getEntityLane(positionDown);
        laneMask |= (1 << lane2);
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
        return horizontal ? ZombieFactory.EGYPT_BOSS_WIDTH : ZombieFactory.EGYPT_BOSS_HEIGHT;
    }

    public Cell getPendingMissileTarget() {
        return pendingMissileTarget;
    }

    public void setPendingMissileTarget(Cell pendingMissileTarget) {
        this.pendingMissileTarget = pendingMissileTarget;
    }

}
