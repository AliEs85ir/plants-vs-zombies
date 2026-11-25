package graphics.general.effects;

import app.model.App;
import com.badlogic.gdx.math.Vector2;
import common.model.Position;
import game.entity.plant.Plant;
import game.entity.zombie.Zombie;
import game.world.playground.Cell;
import graphics.general.animation.condition.AnimationCondition;
import graphics.general.effects.anchers.EffectAnchor;
import graphics.general.effects.anchers.EntityAnchor;
import graphics.general.effects.anchers.WorldAnchor;
import graphics.GraphicsEngine;

public final class GameplayEffects {
    private GameplayEffects() {
    }

    public static EffectInstance<?> spawn(String effectId, Position position) {
        return spawn(effectId, position, null);
    }

    public static EffectInstance<?> spawn(String effectId, Position position, EffectLayer layer) {
        if (position == null) {
            return null;
        }
        return spawn(effectId, null, new WorldAnchor(position), layer, null, 0f);
    }

    public static EffectInstance<?> spawn(String effectId, Vector2 position, EffectLayer layer) {
        if (position == null) {
            return null;
        }
        return spawn(effectId, null, new WorldAnchor(position), layer, null, 0f);
    }

    public static void spawnExplosionPair(Position position) {
        spawn("generic explosion back", position, EffectLayer.BEHIND);
        spawn("generic explosion front", position, EffectLayer.FRONT);
    }

    public static EffectInstance<?> spawnFollowingZombie(String effectId, Zombie zombie, EffectLayer layer) {
        if (zombie == null) {
            return null;
        }
        return spawn(
            effectId,
            zombie,
            new EntityAnchor<>(zombie, z -> z.getPosition() == null ? null : z.getPosition().toVector()),
            layer,
            null,
            0f
        );
    }

    public static EffectInstance<?> spawnFollowingPlant(
        String effectId,
        Plant plant,
        EffectLayer layer,
        AnimationCondition<? super Plant> condition,
        float holdDurationSeconds
    ) {
        return spawnFollowingPlant(effectId, plant, layer, condition, holdDurationSeconds, null);
    }

    public static EffectInstance<?> spawnFollowingPlant(
        String effectId,
        Plant plant,
        EffectLayer layer,
        AnimationCondition<? super Plant> condition,
        float holdDurationSeconds,
        Vector2 offset
    ) {
        if (plant == null) {
            return null;
        }
        Vector2 anchorOffset = offset == null ? new Vector2() : offset.cpy();
        return spawn(
            effectId,
            plant,
            new EntityAnchor<>(plant, GameplayEffects::plantCellAnchor, anchorOffset),
            layer,
            condition,
            holdDurationSeconds
        );
    }

    private static Vector2 plantCellAnchor(Plant plant) {
        if (plant == null) {
            return null;
        }
        Cell cell = plant.getCell();
        if (cell != null) {
            return cell.getWorldPosition().cpy();
        }
        return plant.getPosition() == null ? null : plant.getPosition().toVector();
    }

    public static <M> EffectInstance<M> spawn(
        String effectId,
        M context,
        EffectAnchor anchor,
        EffectLayer layer,
        AnimationCondition<? super M> condition,
        float holdDurationSeconds
    ) {
        if (effectId == null || anchor == null || App.getRealTimeEngine() == null) {
            return null;
        }
        GraphicsEngine graphicsEngine = App.getRealTimeEngine().getGraphicsEngine();
        if (graphicsEngine == null || graphicsEngine.getEffectManager() == null) {
            return null;
        }
        EffectRequest<M> request = new EffectRequest<>(effectId, context, anchor, layer);
        request.setActiveCondition(condition);
        request.setHoldDurationSeconds(holdDurationSeconds);
        return graphicsEngine.getEffectManager().spawn(request);
    }
}
