package game.entity.plant.ability;

import app.enums.plant.PlantGameplayPhase;
import game.engine.time.GameClock;
import game.entity.plant.Plant;
import game.service.plant.PlantService;
import graphics.general.event.PlantVisualEventType;
import graphics.plant.PlantVisualState;

public class GrowthAbility extends AbstractPlantAbility {
    public static final int DEFAULT_MAX_STAGE = 3;
    public static final int STAGE_DURATION_SECONDS = 12;

    private static final long MIN_TRANSFORM_TICKS = 4;
    private static final long MAX_TRANSFORM_TICKS = 40;

    private final int maxStage;
    private final int stageDurationTicks;
    private int stage;
    private long lastStageTick;
    private boolean transforming;
    private long transformStartTick;

    public GrowthAbility(int maxStage, int stageDurationTicks) {
        this.maxStage = Math.max(1, maxStage);
        this.stageDurationTicks = Math.max(1, stageDurationTicks);
        this.stage = 1;
        this.lastStageTick = -1;
    }

    public static GrowthAbility ofDurationSeconds(int maxStage, int stageDurationSeconds) {
        return new GrowthAbility(maxStage, stageDurationSeconds * GameClock.TICKS_PER_SECOND);
    }

    public int getStage() {
        return stage;
    }

    public int getMaxStage() {
        return maxStage;
    }

    public int getStageDurationTicks() {
        return stageDurationTicks;
    }

    public boolean isTransforming() {
        return transforming;
    }

    public void forceGrowthStage(int newStage) {
        this.stage = Math.max(1, Math.min(maxStage, newStage));
        this.transforming = false;
    }

    @Override
    public void resetCycle(long currentTick) {
        super.resetCycle(currentTick);
        transforming = false;
        lastStageTick = currentTick;
    }

    @Override
    protected void doUpdate(Plant plant, PlantService plantService, long currentTick) {
        if (plant == null || !plant.isAlive()) {
            return;
        }
        syncFromPlant(plant);
        if (lastStageTick < 0) {
            lastStageTick = plant.getPlantedTick();
            plant.setGrowthStage(stage);
        }
        if (transforming) {
            long elapsed = currentTick - transformStartTick;
            boolean finished = elapsed >= MIN_TRANSFORM_TICKS
                && PlantAbilitySupport.isClipFinished(plant, PlantVisualState.ATTACK_2);
            if (finished || elapsed >= MAX_TRANSFORM_TICKS) {
                transforming = false;
                stage = Math.min(maxStage, stage + 1);
                plant.setGrowthStage(stage);
                plant.setGameplayPhase(PlantGameplayPhase.DEFAULT);
                lastStageTick = currentTick;
            }
            return;
        }
        if (stage < maxStage && currentTick - lastStageTick >= stageDurationTicks) {
            transforming = true;
            transformStartTick = currentTick;
            plant.setGameplayPhase(PlantGameplayPhase.TRANSFORMING);
            PlantAbilitySupport.play(plant, PlantVisualEventType.ATTACK_2_TRIGGERED);
        }
    }

    private void syncFromPlant(Plant plant) {
        int plantedStage = plant.getStage();
        if (plantedStage > stage) {
            stage = Math.min(maxStage, plantedStage);
            transforming = false;
        }
    }
}
