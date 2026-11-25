package game.entity.plant.ability;

import app.enums.plant.PlantGameplayPhase;
import game.engine.time.GameClock;
import game.entity.DeathStyle;
import game.entity.plant.Plant;
import game.entity.zombie.Zombie;
import game.service.plant.AttackService;
import game.service.plant.PlantService;
import game.world.GameWorld;
import game.world.playground.Cell;
import graphics.general.effects.EffectInstance;
import graphics.general.event.PlantVisualEventType;
import graphics.plant.PlantRenderInstance;
import graphics.plant.PlantVisualState;

import java.util.ArrayList;
import java.util.List;

public class AreaMeleeAbility extends AbstractPlantAbility {
    public static final int CELL_RADIUS_3X3 = 1;
    public static final int KIWIBEAST_PULSE_DELAY_TICKS = 8;
    public static final int KIWIBEAST_TILE_HIT_DELAY_TICKS = 9;
    public static final int KIWIBEAST_DAMAGE_DELAY_TICKS = 11;

    private final long attackCooldown;
    private final int damageDelayTicks;
    private final int areaRadius;
    private final String idleEffectId;
    private final List<Cue> attackCues;
    private long lastAttackTick;
    private EffectInstance<Plant> idlePulse;

    public AreaMeleeAbility(
        long intervalTicks,
        int damageDelayTicks,
        int areaRadius,
        String attackEffectId,
        String idleEffectId
    ) {
        this(intervalTicks, damageDelayTicks, areaRadius, idleEffectId, immediateCue(attackEffectId));
    }

    public AreaMeleeAbility(
        long intervalTicks,
        int damageDelayTicks,
        int areaRadius,
        String idleEffectId,
        Cue... attackCues
    ) {
        this.attackCooldown = intervalTicks;
        this.damageDelayTicks = Math.max(0, damageDelayTicks);
        this.areaRadius = Math.max(0, areaRadius);
        this.idleEffectId = idleEffectId;
        this.attackCues = List.of(attackCues == null ? new Cue[0] : attackCues);
        this.lastAttackTick = 0;
    }

    public int getDamageDelayTicks() {
        return damageDelayTicks;
    }

    public int getAreaRadius() {
        return areaRadius;
    }

    public List<Cue> getAttackCues() {
        return attackCues;
    }

    @Override
    public void resetCycle(long currentTick) {
        super.resetCycle(currentTick);
        lastAttackTick = currentTick;
    }

    @Override
    public boolean handleDeath(Plant plant, PlantService plantService, long currentTick) {
        stopIdlePulse();
        return false;
    }

    @Override
    protected void doUpdate(Plant plant, PlantService plantService, long currentTick) {
        if (plant == null || !plant.isAlive() || plantService == null) {
            stopIdlePulse();
            return;
        }
        if (plant.getGameplayPhase() == PlantGameplayPhase.TRANSFORMING) {
            stopIdlePulse();
            return;
        }
        if (lastAttackTick != 0 && currentTick - lastAttackTick < attackCooldown) {
            refreshIdlePulse(plant);
            return;
        }
        GameWorld world = plantService.getGameWorld();
        if (world == null) {
            refreshIdlePulse(plant);
            return;
        }
        if (!hasTargets(world, plant)) {
            refreshIdlePulse(plant);
            return;
        }
        stopIdlePulse();
        slam(plant, plantService, world, currentTick);
        lastAttackTick = currentTick;
    }

    private void slam(Plant plant, PlantService plantService, GameWorld world, long currentTick) {
        PlantAbilitySupport.play(plant, PlantVisualEventType.ATTACK_TRIGGERED);
        for (Cue cue : attackCues) {
            if (cue == null || cue.effectId == null || cue.effectId.isBlank()) {
                continue;
            }
            PlantAbilitySupport.schedule(currentTick, cue.delayTicks, () ->
                PlantAbilitySupport.spawnAttachedEffect(cue.effectId, plant)
            );
        }
        PlantAbilitySupport.schedule(currentTick, damageDelayTicks, () ->
            applyAreaDamage(plant, plantService, world)
        );
    }

    private void applyAreaDamage(Plant plant, PlantService plantService, GameWorld world) {
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
        Cell origin = PlantTargeting.cellOf(liveWorld, plant);
        AttackService attackService = PlantAbilitySupport.attacks(liveWorld);
        int damage = plant.getAttackDamage();
        long tick = GameClock.currentTick;
        List<Zombie> zombies = new ArrayList<>(PlantTargeting.zombiesInCellRadius(liveWorld, origin, areaRadius));
        for (Zombie zombie : zombies) {
            if (!PlantTargeting.isEligible(zombie)) {
                continue;
            }
            attackService.damageZombie(zombie, tick, damage, DeathStyle.NORMAL, plant);
        }
    }

    private boolean hasTargets(GameWorld world, Plant plant) {
        return !PlantTargeting.zombiesInCellRadius(world, PlantTargeting.cellOf(world, plant), areaRadius).isEmpty();
    }

    private void refreshIdlePulse(Plant plant) {
        if (idleEffectId == null || idleEffectId.isBlank() || !shouldShowIdlePulse(plant)) {
            stopIdlePulse();
            return;
        }
        if (idlePulse != null && !idlePulse.isFinished()) {
            return;
        }
        idlePulse = PlantAbilitySupport.spawnAttachedEffect(
            idleEffectId,
            plant,
            this::isIdlePulseActive
        );
    }

    private boolean isIdlePulseActive(Plant plant) {
        return shouldShowIdlePulse(plant);
    }

    private boolean shouldShowIdlePulse(Plant plant) {
        if (plant == null || !plant.isAlive()) {
            return false;
        }
        if (plant.getPlantFoodState() != null && plant.getPlantFoodState().isActive()) {
            return false;
        }
        if (plant.getGameplayPhase() == PlantGameplayPhase.TRANSFORMING) {
            return false;
        }
        PlantRenderInstance instance = PlantAbilitySupport.instanceOf(plant);
        return instance == null || instance.getCurrentVisualState() == PlantVisualState.IDLE;
    }

    private void stopIdlePulse() {
        if (idlePulse != null && !idlePulse.isFinished()) {
            idlePulse.forceFinish();
        }
        idlePulse = null;
    }

    private static Cue[] immediateCue(String attackEffectId) {
        if (attackEffectId == null || attackEffectId.isBlank()) {
            return new Cue[0];
        }
        return new Cue[]{new Cue(0, attackEffectId)};
    }

    public static final class Cue {
        public final int delayTicks;
        public final String effectId;

        public Cue(int delayTicks, String effectId) {
            this.delayTicks = Math.max(0, delayTicks);
            this.effectId = effectId;
        }
    }
}
