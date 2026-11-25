package game.entity.plant.ability;

import app.enums.plant.PlantTag;
import game.engine.time.GameClock;
import game.entity.DeathStyle;
import game.entity.plant.Plant;
import game.entity.plant.upgrade.PlantUpgradeKeys;
import game.entity.zombie.Zombie;
import game.service.plant.AttackService;
import game.service.plant.PlantService;
import game.service.plant.PlantUpgradeService;
import game.world.GameWorld;
import game.world.playground.Cell;
import game.world.playground.Grid;
import graphics.general.event.PlantVisualEventType;

import java.util.ArrayList;
import java.util.List;

public class AdjacentMeleeAbility extends AbstractPlantAbility {
    public static final int BASE_CELL_RANGE = 1;
    public static final int WINDUP_DAMAGE_DELAY_TICKS = 3;

    private final long attackCooldown;
    private final int damageDelayTicks;
    private final String hitEffectId;
    private final boolean simultaneousSides;
    private long lastAttackTick;
    private boolean lastPunchWasFront;

    public AdjacentMeleeAbility(long intervalTicks, int damageDelayTicks, String hitEffectId) {
        this(intervalTicks, damageDelayTicks, hitEffectId, false);
    }

    public AdjacentMeleeAbility(
        long intervalTicks,
        int damageDelayTicks,
        String hitEffectId,
        boolean simultaneousSides
    ) {
        this.attackCooldown = intervalTicks;
        this.damageDelayTicks = Math.max(1, damageDelayTicks);
        this.hitEffectId = hitEffectId;
        this.simultaneousSides = simultaneousSides;
        this.lastAttackTick = 0;
        this.lastPunchWasFront = false;
    }

    public int getDamageDelayTicks() {
        return damageDelayTicks;
    }

    public boolean punchesSimultaneousSides() {
        return simultaneousSides;
    }

    @Override
    public void resetCycle(long currentTick) {
        super.resetCycle(currentTick);
        lastAttackTick = currentTick;
        lastPunchWasFront = false;
    }

    @Override
    protected void doUpdate(Plant plant, PlantService plantService, long currentTick) {
        if (plant == null || !plant.isAlive() || plantService == null) {
            return;
        }
        if (lastAttackTick != 0 && currentTick - lastAttackTick < attackCooldown) {
            return;
        }
        GameWorld world = plantService.getGameWorld();
        if (world == null) {
            return;
        }
        boolean front = hasZombies(world, plant, true);
        boolean back = hasZombies(world, plant, false);
        if (!front && !back) {
            return;
        }
        if (simultaneousSides) {
            if (front) {
                punch(plant, plantService, world, currentTick, true, true);
            }
            if (back) {
                punch(plant, plantService, world, currentTick, false, !front);
            }
        } else {
            boolean punchFront = resolvePunchFront(front, back);
            punch(plant, plantService, world, currentTick, punchFront, true);
            lastPunchWasFront = punchFront;
        }
        lastAttackTick = currentTick;
    }

    private boolean resolvePunchFront(boolean front, boolean back) {
        if (front && back) {
            return !lastPunchWasFront;
        }
        return front;
    }

    private void punch(
        Plant plant,
        PlantService plantService,
        GameWorld world,
        long currentTick,
        boolean front,
        boolean playAnimation
    ) {
        if (playAnimation) {
            PlantAbilitySupport.play(
                plant,
                front ? PlantVisualEventType.ATTACK_TRIGGERED : PlantVisualEventType.ATTACK_2_TRIGGERED
            );
        }
        PlantAbilitySupport.schedule(currentTick, damageDelayTicks, () ->
            applyPunchDamage(plant, plantService, world, front)
        );
    }

    private void applyPunchDamage(Plant plant, PlantService plantService, GameWorld world, boolean front) {
        if (plant == null || !plant.isAlive() || plantService == null) {
            return;
        }
        GameWorld liveWorld = plantService.getGameWorld();
        if (liveWorld == null) {
            liveWorld = world;
        }
        if (liveWorld == null) {
            return;
        }
        AttackService attackService = PlantAbilitySupport.attacks(liveWorld);
        int damage = plant.getAttackDamage();
        long tick = GameClock.currentTick;
        boolean heat = isFirePlant(plant);
        int fireTicks = AttackService.STANDARD_FIRE_DURATION_TICKS;
        int sign = columnSign(plant, front);
        int range = cellRange(plant);
        for (int distance = 1; distance <= range; distance++) {
            Cell target = offsetCell(liveWorld, plant, sign * distance);
            if (target == null) {
                continue;
            }
            List<Zombie> zombies = new ArrayList<>(PlantTargeting.zombiesInCell(liveWorld, target));
            for (Zombie zombie : zombies) {
                if (!PlantTargeting.isEligible(zombie)) {
                    continue;
                }
                if (heat) {
                    attackService.heatZombie(zombie, fireTicks);
                }
                attackService.damageZombie(zombie, tick, damage, DeathStyle.NORMAL, plant);
                PlantAbilitySupport.spawnEffectOn(hitEffectId, zombie);
            }
            if (heat) {
                attackService.heatCell(target, fireTicks);
            }
        }
    }

    private static boolean hasZombies(GameWorld world, Plant plant, boolean front) {
        int sign = columnSign(plant, front);
        int range = cellRange(plant);
        for (int distance = 1; distance <= range; distance++) {
            if (!PlantTargeting.zombiesInCell(world, offsetCell(world, plant, sign * distance)).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private static Cell offsetCell(GameWorld world, Plant plant, int colOffset) {
        Cell origin = PlantTargeting.cellOf(world, plant);
        if (origin == null || world == null) {
            return null;
        }
        Grid grid = world.getGrid();
        if (grid == null) {
            return null;
        }
        return grid.getCell(origin.getRow(), origin.getCol() + colOffset);
    }

    private static int columnSign(Plant plant, boolean front) {
        int facing = plant != null && plant.facesLeft() ? -1 : 1;
        return front ? facing : -facing;
    }

    private static int cellRange(Plant plant) {
        int extra = 0;
        if (plant != null && plant.getDefinition() != null && plant.getDefinition().getName() != null) {
            extra = Math.max(0, PlantUpgradeService.getModifiers(plant.getDefinition().getName()).extraRange());
        }
        return BASE_CELL_RANGE + extra;
    }

    private static boolean isFirePlant(Plant plant) {
        return plant != null && plant.getDefinition() != null && plant.getDefinition().hasTag(PlantTag.FIRE);
    }
}
