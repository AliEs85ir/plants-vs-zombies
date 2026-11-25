package game.entity.plant.ability.plantfood;

import common.model.Position;
import game.entity.DamageContext;
import game.engine.time.GameClock;
import game.entity.plant.Plant;
import game.entity.plant.ability.PlantAbilitySupport;
import game.entity.plant.plantfood.PlantFoodDefinition;
import game.entity.plant.plantfood.PlantFoodFireLine;
import game.entity.zombie.Zombie;
import game.service.plant.AttackService;
import game.service.plant.PlantService;
import game.world.playground.Cell;
import graphics.general.effects.EffectInstance;
import graphics.plant.PlantRenderInstance;

import java.util.ArrayList;
import java.util.List;

public final class DurationPlantFoodAbility extends AbstractPlantFoodAbility {
    private final List<Cell> fireCells = new ArrayList<>();
    private final List<EffectInstance<?>> fireEffects = new ArrayList<>();
    private boolean fireDamageActive;
    private boolean fireEnded;

    public DurationPlantFoodAbility(PlantFoodDefinition definition) {
        super(definition);
    }

    @Override
    protected void onStart(Plant plant, PlantService plantService, long currentTick) {
        fireCells.clear();
        fireEffects.clear();
        fireDamageActive = false;
        fireEnded = false;
        if (!definition.isFireLine() || plant == null || plantService == null) {
            return;
        }
        fireCells.addAll(PlantFoodFireLine.cellsInFront(plantService.getGameWorld().getGrid(), plant));
        fireDamageActive = true;
        spawnFireEffects(plant);
    }

    @Override
    protected void onTick(Plant plant, PlantService plantService, long currentTick, long elapsedTicks) {
        int duration = definition.getDurationTicks();
        if (definition.isFireLine()) {
            if (plant == null || plant.isDead()) {
                cleanupFire(true);
                complete();
                return;
            }
            if (fireDamageActive && elapsedTicks < duration) {
                int interval = Math.max(1, definition.getDamageIntervalTicks());
                if (elapsedTicks % interval == 0) {
                    pulseFireDamage(plant, plantService, currentTick, elapsedTicks);
                }
            }
            if (duration > 0 && elapsedTicks >= duration) {
                endFireLine(plant);
                if (isFireAnimationFinished(plant, elapsedTicks)) {
                    complete();
                }
                return;
            }
            return;
        }
        if (duration > 0 && elapsedTicks >= duration) {
            complete();
        }
    }

    @Override
    public boolean handleDeath(Plant plant, PlantService plantService, long currentTick) {
        cleanupFire(true);
        if (plant != null && plant.getPlantFoodState() != null) {
            plant.getPlantFoodState().deactivate();
        }
        return false;
    }

    private void pulseFireDamage(Plant plant, PlantService plantService, long currentTick, long elapsedTicks) {
        if (!fireDamageActive || plantService == null || plantService.getGameWorld() == null) {
            return;
        }
        AttackService attackService = PlantAbilitySupport.attacks(plantService.getGameWorld());
        int damage = Math.max(1, plant.getAttackDamage());
        int fireTicks = AttackService.STANDARD_FIRE_DURATION_TICKS;
        for (Cell cell : fireCells) {
            List<Zombie> occupants = new ArrayList<>(attackService.zombiesInCell(cell));
            for (Zombie zombie : occupants) {
                attackService.heatZombie(zombie, fireTicks);
                zombie.takeDamage(new DamageContext(plant, damage, false), currentTick);
            }
        }
    }

    private void spawnFireEffects(Plant plant) {
        String effectId = definition.getParticleEffectId();
        if (effectId == null || effectId.isBlank()) {
            return;
        }
        float holdSeconds = definition.getDurationTicks() / (float) GameClock.TICKS_PER_SECOND;
        boolean flip = plant != null && plant.facesLeft();
        for (Cell cell : fireCells) {
            EffectInstance<?> effect = PlantAbilitySupport.spawnEffect(
                effectId,
                new Position(cell.getWorldX(), cell.getWorldY()),
                flip,
                holdSeconds,
                1f,
                1f
            );
            if (effect != null) {
                fireEffects.add(effect);
            }
        }
    }

    private void endFireLine(Plant plant) {
        if (fireEnded) {
            return;
        }
        fireEnded = true;
        fireDamageActive = false;
        for (EffectInstance<?> effect : fireEffects) {
            if (effect != null) {
                effect.setHoldDurationSeconds(0.001f);
            }
        }
        PlantRenderInstance instance = PlantAbilitySupport.instanceOf(plant);
        if (instance != null && instance.getSequenceLength() > 0) {
            requestAnimationStage(plant, instance.getSequenceLength() - 1);
        }
    }

    private boolean isFireAnimationFinished(Plant plant, long elapsedTicks) {
        PlantRenderInstance instance = PlantAbilitySupport.instanceOf(plant);
        if (instance == null) {
            return true;
        }
        return isAnimationLifecycleFinished(plant, elapsedTicks);
    }

    private void cleanupFire(boolean removeEffects) {
        fireDamageActive = false;
        fireEnded = true;
        fireCells.clear();
        if (removeEffects) {
            for (EffectInstance<?> effect : fireEffects) {
                if (effect != null && !effect.isFinished()) {
                    effect.forceFinish();
                }
            }
        }
        fireEffects.clear();
    }
}
