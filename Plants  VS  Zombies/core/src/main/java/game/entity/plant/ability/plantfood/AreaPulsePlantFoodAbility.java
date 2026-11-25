package game.entity.plant.ability.plantfood;

import app.enums.plant.PlantTag;
import game.entity.DeathStyle;
import game.entity.plant.Plant;
import game.entity.plant.ability.PlantAbilitySupport;
import game.entity.plant.ability.PlantTargeting;
import game.entity.plant.plantfood.PlantFoodDefinition;
import game.entity.zombie.Zombie;
import game.service.plant.AttackService;
import game.service.plant.PlantService;
import game.world.GameWorld;
import game.world.playground.Cell;
import game.world.playground.Grid;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class AreaPulsePlantFoodAbility extends AbstractPlantFoodAbility {
    private boolean pulsed;

    public AreaPulsePlantFoodAbility(PlantFoodDefinition definition) {
        super(definition);
    }

    @Override
    protected void onStart(Plant plant, PlantService plantService, long currentTick) {
        pulsed = false;
        if (definition.isForceMaxGrowth()) {
            PlantAbilitySupport.forceMaxGrowth(plant, 3);
        }
        if (!definition.hasEffectSchedule()) {
            spawnConfiguredEffects(plant);
        }
    }

    @Override
    protected void onTick(Plant plant, PlantService plantService, long currentTick, long elapsedTicks) {
        if (shouldSpawnEffects(elapsedTicks)) {
            spawnConfiguredEffects(plant);
        }
        if (shouldPulse(elapsedTicks)) {
            pulse(plant, plantService, currentTick);
            pulsed = true;
        }
        if (shouldComplete(plant, elapsedTicks)) {
            complete();
        }
    }

    private boolean shouldSpawnEffects(long elapsedTicks) {
        return containsTick(definition.getEffectTicks(), elapsedTicks);
    }

    private boolean shouldPulse(long elapsedTicks) {
        if (definition.hasPulseSchedule()) {
            return containsTick(definition.getPulseTicks(), elapsedTicks);
        }
        int delay = Math.max(0, definition.getFireStartDelayTicks());
        if (elapsedTicks < delay) {
            return false;
        }
        int duration = definition.getDurationTicks();
        if (duration > 0 && elapsedTicks >= duration) {
            return false;
        }
        int interval = Math.max(1, definition.getFireIntervalTicks());
        return (elapsedTicks - delay) % interval == 0;
    }

    private boolean shouldComplete(Plant plant, long elapsedTicks) {
        if (isGameplayComplete(elapsedTicks)) {
            return true;
        }
        int duration = definition.getDurationTicks();
        int interval = Math.max(1, definition.getFireIntervalTicks());
        boolean singlePulse = duration > 0 && interval >= duration;
        return singlePulse && pulsed && isAnimationLifecycleFinished(plant, elapsedTicks);
    }

    private boolean isGameplayComplete(long elapsedTicks) {
        int duration = definition.getDurationTicks();
        return duration > 0 && elapsedTicks >= duration;
    }

    private void pulse(Plant plant, PlantService plantService, long currentTick) {
        if (plant == null || plantService == null || plantService.getGameWorld() == null) {
            return;
        }
        GameWorld world = plantService.getGameWorld();
        AttackService attackService = PlantAbilitySupport.attacks(world);
        int damage = plant.getAttackDamage();
        int radius = definition.getAreaRadius();
        Cell origin = PlantTargeting.cellOf(world, plant);
        boolean heat = isFirePlant(plant);
        int fireTicks = AttackService.STANDARD_FIRE_DURATION_TICKS;
        for (Zombie zombie : collectTargets(world, origin, radius, definition.getAreaPercent())) {
            if (heat) {
                attackService.heatZombie(zombie, fireTicks);
            }
            attackService.damageZombie(zombie, currentTick, damage, DeathStyle.NORMAL, plant);
        }
        if (heat) {
            heatCells(world, attackService, origin, radius, fireTicks);
        }
    }

    private static boolean isFirePlant(Plant plant) {
        return plant != null && plant.getDefinition() != null && plant.getDefinition().hasTag(PlantTag.FIRE);
    }

    private static void heatCells(
        GameWorld world,
        AttackService attackService,
        Cell origin,
        int radius,
        int fireTicks
    ) {
        if (world == null || attackService == null || origin == null || world.getGrid() == null || radius < 0) {
            return;
        }
        Grid grid = world.getGrid();
        for (int row = origin.getRow() - radius; row <= origin.getRow() + radius; row++) {
            for (int col = origin.getCol() - radius; col <= origin.getCol() + radius; col++) {
                attackService.heatCell(grid.getCell(row, col), fireTicks);
            }
        }
    }

    private static List<Zombie> collectTargets(GameWorld world, Cell origin, int radius, float areaPercent) {
        if (radius < 0) {
            return PlantTargeting.zombiesInCellRadius(world, origin, PlantTargeting.ALL_LAWN_RADIUS);
        }
        Set<Zombie> targets = new LinkedHashSet<>();
        if (origin == null) {
            return new ArrayList<>(targets);
        }
        targets.addAll(PlantTargeting.zombiesInChebyshevArea(
            world,
            origin.getRow(),
            origin.getCol(),
            radius,
            areaPercent
        ));
        Grid grid = world.getGrid();
        if (grid == null) {
            return new ArrayList<>(targets);
        }
        for (int row = origin.getRow() - radius; row <= origin.getRow() + radius; row++) {
            for (int col = origin.getCol() - radius; col <= origin.getCol() + radius; col++) {
                targets.addAll(PlantTargeting.zombiesInCell(world, grid.getCell(row, col)));
            }
        }
        return new ArrayList<>(targets);
    }

    private void spawnConfiguredEffects(Plant plant) {
        spawnIfPresent(definition.getParticleEffectId(), plant);
        spawnIfPresent(definition.getSecondaryParticleEffectId(), plant);
    }

    private static void spawnIfPresent(String effectId, Plant plant) {
        if (effectId == null || effectId.isBlank()) {
            return;
        }
        PlantAbilitySupport.spawnAttachedEffect(effectId, plant);
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
}
