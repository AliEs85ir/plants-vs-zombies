package game.entity.plant.ability;

import app.enums.plant.PlantName;
import app.model.App;
import app.view.game.PlayScreen;
import common.model.Position;
import game.entity.plant.Plant;
import game.entity.zombie.Zombie;
import game.service.plant.AttackService;
import game.service.plant.PlantService;
import game.service.quest.QuestSignals;
import game.world.GameWorld;
import graphics.GraphicsEngine;
import graphics.general.animation.condition.AnimationCondition;
import graphics.general.effects.EffectInstance;
import graphics.general.effects.EffectLayer;
import graphics.general.effects.EffectRequest;
import graphics.general.effects.anchers.EntityAnchor;
import graphics.general.effects.anchers.WorldAnchor;
import graphics.general.event.PlantVisualEventType;
import graphics.general.event.VisualEvent;
import graphics.plant.PlantRenderInstance;
import graphics.plant.PlantVisualState;

public final class PlantAbilitySupport {
    private PlantAbilitySupport() {
    }

    public static AttackService attacks(GameWorld world) {
        return new AttackService(world);
    }

    public static void forceMaxGrowth(Plant plant, int stage) {
        if (plant == null) {
            return;
        }
        int clamped = Math.max(1, stage);
        plant.setGrowthStage(clamped);
        if (plant.getAbilities() == null) {
            return;
        }
        for (PlantAbility ability : plant.getAbilities()) {
            if (ability instanceof SunProductionAbility sunAbility) {
                sunAbility.forceGrowthStage(clamped);
            }
            if (ability instanceof GrowthAbility growthAbility) {
                growthAbility.forceGrowthStage(clamped);
            }
        }
    }

    public static void play(Plant plant, PlantVisualEventType type) {
        if (plant == null || type == null || App.getRealTimeEngine() == null) {
            return;
        }
        GraphicsEngine graphicsEngine = App.getRealTimeEngine().getGraphicsEngine();
        if (graphicsEngine == null) {
            return;
        }
        PlantRenderInstance instance = graphicsEngine.getPlantInstance(plant);
        if (instance == null) {
            return;
        }
        instance.pushEvent(new VisualEvent<>(type));
    }

    public static EffectInstance<Object> spawnEffect(String effectId, Position position) {
        return spawnEffect(effectId, position, false, 0f);
    }

    public static EffectInstance<Object> spawnEffect(String effectId, Position position, boolean flipX, float holdDurationSeconds) {
        return spawnEffect(effectId, position, flipX, holdDurationSeconds, 1f);
    }

    public static EffectInstance<Object> spawnEffect(
        String effectId,
        Position position,
        boolean flipX,
        float holdDurationSeconds,
        float scale
    ) {
        return spawnEffect(effectId, position, flipX, holdDurationSeconds, scale, scale);
    }

    public static EffectInstance<Object> spawnEffect(
        String effectId,
        Position position,
        boolean flipX,
        float holdDurationSeconds,
        float scaleX,
        float scaleY
    ) {
        if (effectId == null || position == null || App.getRealTimeEngine() == null) {
            return null;
        }
        GraphicsEngine graphicsEngine = App.getRealTimeEngine().getGraphicsEngine();
        if (graphicsEngine == null || graphicsEngine.getEffectManager() == null) {
            return null;
        }
        EffectRequest<Object> request = new EffectRequest<>(
            effectId,
            null,
            new WorldAnchor(position),
            EffectLayer.FRONT
        );
        request.setFlipX(flipX);
        request.setHoldDurationSeconds(holdDurationSeconds);
        request.setScale(scaleX, scaleY);
        return graphicsEngine.getEffectManager().spawn(request);
    }

    public static void spawnEffectOn(String effectId, Zombie zombie) {
        if (effectId == null || effectId.isBlank() || zombie == null || zombie.getPosition() == null) {
            return;
        }
        if (App.getRealTimeEngine() == null) {
            return;
        }
        GraphicsEngine graphicsEngine = App.getRealTimeEngine().getGraphicsEngine();
        if (graphicsEngine == null || graphicsEngine.getEffectManager() == null) {
            return;
        }
        EffectRequest<Zombie> request = new EffectRequest<>(
            effectId,
            zombie,
            new EntityAnchor<>(zombie, z -> z.getPosition() == null ? null : z.getPosition().toVector()),
            EffectLayer.FRONT
        );
        graphicsEngine.getEffectManager().spawn(request);
    }

    public static EffectInstance<Plant> spawnAttachedEffect(String effectId, Plant plant) {
        return spawnAttachedEffect(effectId, plant, null);
    }

    public static EffectInstance<Plant> spawnAttachedEffect(
        String effectId,
        Plant plant,
        AnimationCondition<? super Plant> activeCondition
    ) {
        if (effectId == null || effectId.isBlank() || plant == null || plant.getPosition() == null) {
            return null;
        }
        if (App.getRealTimeEngine() == null) {
            return null;
        }
        GraphicsEngine graphicsEngine = App.getRealTimeEngine().getGraphicsEngine();
        if (graphicsEngine == null || graphicsEngine.getEffectManager() == null) {
            return null;
        }
        EffectRequest<Plant> request = new EffectRequest<>(
            effectId,
            plant,
            new EntityAnchor<>(plant, p -> p.getPosition() == null ? null : p.getPosition().toVector()),
            EffectLayer.FRONT,
            activeCondition
        );
        return graphicsEngine.getEffectManager().spawn(request);
    }

    public static void notifyExploded() {
        notifyExploded(null);
    }

    public static void notifyExploded(Plant plant) {
        QuestSignals.plantExploded(plant);
        shakeForExplosion(plant);
        game.audio.Sfx.plantExploded(plant);
    }

    private static void shakeForExplosion(Plant plant) {
        if (plant == null || plant.getDefinition() == null) {
            return;
        }
        PlantName name = plant.getDefinition().getName();
        if (name == null) {
            return;
        }
        switch (name) {
            case DOOM_SHROOM -> PlayScreen.shake(0.9f, 26f);
            case CHERRY_BOMB, GRAPESHOT -> PlayScreen.shake(0.55f, 18f);
            case JALAPENO -> PlayScreen.shake(0.5f, 16f);
            case POTATO_MINE, PRIMAL_POTATO_MINE -> PlayScreen.shake(0.4f, 14f);
            case EXPLODE_O_NUT -> PlayScreen.shake(0.5f, 16f);
            case SQUASH -> PlayScreen.shake(0.4f, 14f);
            case ICE_SHROOM -> PlayScreen.shake(0.35f, 10f);
            default -> {
            }
        }
    }

    public static void consumeLater(Plant plant, PlantService plantService, long currentTick, int delayTicks) {
        if (plant == null || App.getCurrentGameEngine() == null) {
            return;
        }
        int delay = Math.max(0, delayTicks);
        App.getCurrentGameEngine().getTaskManager().scheduleRelative(currentTick, delay, () -> {
            if (plant.isDead()) {
                return;
            }
            GameWorld world = plantService != null
                ? plantService.getGameWorld()
                : App.getCurrentGameEngine().getGameWorld();
            plant.markDead(world);
        });
    }

    public static void schedule(long currentTick, int delayTicks, Runnable action) {
        if (action == null || App.getCurrentGameEngine() == null) {
            return;
        }
        App.getCurrentGameEngine().getTaskManager().scheduleRelative(
            currentTick,
            Math.max(0, delayTicks),
            action
        );
    }

    public static void keepAlive(Plant plant) {
        if (plant == null || plant.getHealth() == null) {
            return;
        }
        int restore = Math.max(1, plant.getHealth().getMaxHealth());
        plant.getHealth().heal(restore);
    }

    public static PlantRenderInstance instanceOf(Plant plant) {
        if (plant == null || App.getRealTimeEngine() == null || App.getRealTimeEngine().getGraphicsEngine() == null) {
            return null;
        }
        return App.getRealTimeEngine().getGraphicsEngine().getPlantInstance(plant);
    }

    public static boolean isClipFinished(Plant plant, PlantVisualState state) {
        return isActionComplete(plant, state, 0);
    }

    public static boolean isSequenceFinished(Plant plant, PlantVisualState state, int lastStageIndex) {
        return isActionComplete(plant, state, lastStageIndex);
    }

    public static boolean isActionComplete(Plant plant, PlantVisualState state, int lastStageIndex) {
        PlantRenderInstance instance = instanceOf(plant);
        if (instance == null || state == null) {
            return false;
        }
        PlantVisualState current = instance.getCurrentVisualState();
        if (current == state) {
            if (lastStageIndex > 0 && instance.getAnimationStage() < lastStageIndex) {
                return false;
            }
            if (!instance.getAnimationController().isFinished()) {
                return false;
            }
            return lastStageIndex <= 0 || instance.getAnimationStage() >= lastStageIndex;
        }
        if (plant.getPlantFoodState() != null && plant.getPlantFoodState().isActive()) {
            return false;
        }
        return current == PlantVisualState.IDLE;
    }
}
