package game.entity.plant.ability.plantfood;

import common.model.Position;
import game.entity.plant.Plant;
import game.entity.plant.ability.PlantAbilitySupport;
import game.entity.plant.ability.PlantTargeting;
import game.entity.plant.plantfood.PlantFoodDefinition;
import game.entity.zombie.Zombie;
import game.entity.zombie.ZombieLaneSwitch;
import game.service.plant.PlantService;
import game.world.GameWorld;
import game.world.playground.Cell;

import java.util.ArrayList;
import java.util.List;

public final class LaneDivertPlantFoodAbility extends AbstractPlantFoodAbility {
    public LaneDivertPlantFoodAbility(PlantFoodDefinition definition) {
        super(definition);
    }

    @Override
    protected void onStart(Plant plant, PlantService plantService, long currentTick) {
        restoreHealth(plant);
    }

    @Override
    protected void onTick(Plant plant, PlantService plantService, long currentTick, long elapsedTicks) {
        if (containsTick(definition.getEffectTicks(), elapsedTicks)) {
            spawnConfiguredEffect(plant);
        }
        if (containsTick(definition.getPulseTicks(), elapsedTicks)) {
            divertForwardZombies(plant, plantService);
        }
        if (shouldComplete(plant, elapsedTicks)) {
            complete();
        }
    }

    private void restoreHealth(Plant plant) {
        if (plant == null || plant.getHealth() == null) {
            return;
        }
        if (definition.isRestoreFullHealth()) {
            plant.heal(plant.getHealth().getMaxHealth());
            return;
        }
        if (definition.getHealAmount() > 0) {
            plant.heal(definition.getHealAmount());
        }
    }

    private boolean shouldComplete(Plant plant, long elapsedTicks) {
        int lastScheduled = maxTick(0, definition.getEffectTicks());
        lastScheduled = maxTick(lastScheduled, definition.getPulseTicks());
        if (lastScheduled > 0 && elapsedTicks < lastScheduled) {
            return false;
        }
        if (isAnimationLifecycleFinished(plant, elapsedTicks)) {
            return true;
        }
        int duration = definition.getDurationTicks();
        return duration > 0 && elapsedTicks >= duration;
    }

    private void spawnConfiguredEffect(Plant plant) {
        if (plant == null || plant.getPosition() == null) {
            return;
        }
        String effectId = definition.getParticleEffectId();
        if (effectId == null || effectId.isBlank()) {
            return;
        }
        Position origin = plant.getPosition();
        PlantAbilitySupport.spawnEffect(
            effectId,
            new Position(
                origin.getX() + definition.getEffectOffsetX(),
                origin.getY() + definition.getEffectOffsetY()
            )
        );
    }

    private void divertForwardZombies(Plant plant, PlantService plantService) {
        if (plant == null || plantService == null || plantService.getGameWorld() == null) {
            return;
        }
        GameWorld world = plantService.getGameWorld();
        if (world.getGrid() == null) {
            return;
        }
        if (definition.isAttractToPlantLane()) {
            pullZombiesToPlantLane(plant, world, definition.isIncludeAdjacentLanes());
            return;
        }
        List<Zombie> targets = new ArrayList<>(PlantTargeting.zombiesForwardInLane(world, plant));
        for (Zombie zombie : targets) {
            if (PlantTargeting.isEligible(zombie)) {
                ZombieLaneSwitch.divertToAdjacentLane(zombie, world.getGrid());
            }
        }
    }

    private static void pullZombiesToPlantLane(Plant plant, GameWorld world, boolean includeAdjacentLanes) {
        if (world.getZombies() == null) {
            return;
        }
        Cell origin = PlantTargeting.cellOf(world, plant);
        int targetRow = origin != null ? origin.getRow() : world.getGrid().getEntityLane(plant);
        int laneRadius = includeAdjacentLanes ? 1 : 0;
        int minLane = Math.max(0, targetRow - laneRadius);
        int maxLane = Math.min(world.getGrid().getRowCount() - 1, targetRow + laneRadius);
        for (Zombie zombie : world.getZombies()) {
            if (!PlantTargeting.isEligible(zombie) || zombie.getPosition() == null) {
                continue;
            }
            int lane = world.getGrid().getEntityLane(zombie);
            if (lane < minLane || lane > maxLane) {
                continue;
            }
            if (lane == targetRow && !ZombieLaneSwitch.isDivertInProgress(zombie)) {
                continue;
            }
            ZombieLaneSwitch.beginLanePull(
                zombie,
                world.getGrid(),
                targetRow,
                ZombieLaneSwitch.LANE_PULL_SPEED_MULTIPLIER,
                true
            );
        }
    }

    private static boolean containsTick(List<Integer> ticks, long elapsedTicks) {
        if (ticks == null || ticks.isEmpty()) {
            return false;
        }
        for (Integer tick : ticks) {
            if (tick != null && tick == elapsedTicks) {
                return true;
            }
        }
        return false;
    }

    private static int maxTick(int current, List<Integer> ticks) {
        if (ticks == null) {
            return current;
        }
        int last = current;
        for (Integer tick : ticks) {
            if (tick != null && tick > last) {
                last = tick;
            }
        }
        return last;
    }
}
