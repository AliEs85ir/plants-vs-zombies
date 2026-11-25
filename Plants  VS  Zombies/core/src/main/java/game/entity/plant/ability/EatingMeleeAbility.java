package game.entity.plant.ability;

import app.enums.plant.PlantGameplayPhase;
import game.engine.time.GameClock;
import game.entity.DeathStyle;
import game.entity.plant.Plant;
import game.entity.zombie.Zombie;
import game.service.plant.AttackService;
import game.service.plant.PlantService;
import game.world.GameWorld;
import graphics.general.event.PlantVisualEventType;
import graphics.plant.PlantVisualState;

import java.util.ArrayList;
import java.util.List;

public class EatingMeleeAbility extends AbstractPlantAbility {
    public static final int ATTACK_INTERVAL_TICKS = 3;
    public static final int DAMAGE_DELAY_TICKS = 1;

    private final long attackCooldown;
    private final int damageDelayTicks;
    private long lastAttackTick;
    private boolean attacking;
    private boolean recovering;
    private long recoverStartTick;
    private boolean recoverClipSeen;

    public EatingMeleeAbility(long intervalTicks, int damageDelayTicks) {
        this.attackCooldown = Math.max(1, intervalTicks);
        this.damageDelayTicks = Math.max(0, damageDelayTicks);
        this.lastAttackTick = 0;
    }

    public int getAttackIntervalTicks() {
        return (int) attackCooldown;
    }

    public int getDamageDelayTicks() {
        return damageDelayTicks;
    }

    @Override
    public void resetCycle(long currentTick) {
        super.resetCycle(currentTick);
        lastAttackTick = currentTick;
        attacking = false;
        recovering = false;
        recoverClipSeen = false;
    }

    @Override
    public boolean handleDeath(Plant plant, PlantService plantService, long currentTick) {
        attacking = false;
        recovering = false;
        recoverClipSeen = false;
        return false;
    }

    @Override
    protected void doUpdate(Plant plant, PlantService plantService, long currentTick) {
        if (plant == null || !plant.isAlive() || plant.getHealth() == null || plant.getHealth().isDead()) {
            attacking = false;
            recovering = false;
            return;
        }
        if (plantService == null) {
            return;
        }
        GameWorld world = plantService.getGameWorld();
        if (world == null) {
            return;
        }
        boolean eating = !PlantTargeting.zombiesEating(world, plant).isEmpty();
        if (recovering) {
            if (eating) {
                beginAttack(plant, plantService, world, currentTick);
            } else {
                updateRecovering(plant, currentTick);
            }
            return;
        }
        if (eating) {
            if (!attacking) {
                beginAttack(plant, plantService, world, currentTick);
            } else {
                tryStrike(plant, plantService, world, currentTick);
            }
            return;
        }
        if (attacking) {
            beginRecover(plant, currentTick);
        }
    }

    private void beginAttack(Plant plant, PlantService plantService, GameWorld world, long currentTick) {
        attacking = true;
        recovering = false;
        recoverClipSeen = false;
        lastAttackTick = 0;
        plant.setGameplayPhase(PlantGameplayPhase.ATTACKING);
        PlantAbilitySupport.play(plant, PlantVisualEventType.ATTACK_TRIGGERED);
        tryStrike(plant, plantService, world, currentTick);
    }

    private void tryStrike(Plant plant, PlantService plantService, GameWorld world, long currentTick) {
        if (lastAttackTick != 0 && currentTick - lastAttackTick < attackCooldown) {
            return;
        }
        lastAttackTick = currentTick;
        PlantAbilitySupport.schedule(currentTick, damageDelayTicks, () ->
            applyEatingDamage(plant, plantService, world)
        );
    }

    private void applyEatingDamage(Plant plant, PlantService plantService, GameWorld world) {
        if (plant == null || !plant.isAlive() || plant.getHealth() == null || plant.getHealth().isDead()) {
            return;
        }
        if (plantService == null) {
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
        List<Zombie> eaters = new ArrayList<>(PlantTargeting.zombiesEating(liveWorld, plant));
        for (Zombie zombie : eaters) {
            if (!PlantTargeting.isEligible(zombie)) {
                continue;
            }
            attackService.damageZombie(zombie, tick, damage, DeathStyle.NORMAL, plant);
        }
    }

    private void beginRecover(Plant plant, long currentTick) {
        attacking = false;
        recovering = true;
        recoverStartTick = currentTick;
        recoverClipSeen = false;
        plant.setGameplayPhase(PlantGameplayPhase.RECOVERING);
        PlantAbilitySupport.play(plant, PlantVisualEventType.RECOVER_TRIGGERED);
    }

    private void updateRecovering(Plant plant, long currentTick) {
        var instance = PlantAbilitySupport.instanceOf(plant);
        if (instance == null) {
            finishRecover(plant);
            return;
        }
        PlantVisualState visual = instance.getCurrentVisualState();
        if (visual == PlantVisualState.RECOVER) {
            recoverClipSeen = true;
            if (instance.getAnimationController().isFinished()) {
                finishRecover(plant);
            }
            return;
        }
        if (recoverClipSeen && visual == PlantVisualState.IDLE) {
            finishRecover(plant);
            return;
        }
        if (!recoverClipSeen && currentTick - recoverStartTick >= recoverFallbackTicks()) {
            finishRecover(plant);
        }
    }

    private void finishRecover(Plant plant) {
        recovering = false;
        recoverClipSeen = false;
        plant.setGameplayPhase(PlantGameplayPhase.DEFAULT);
    }

    private static int recoverFallbackTicks() {
        return Math.max(1, 4 * GameClock.TICKS_PER_SECOND);
    }
}
