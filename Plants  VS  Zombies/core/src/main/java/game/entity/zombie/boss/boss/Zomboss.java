package game.entity.zombie.boss.boss;

import common.model.Position;
import game.entity.DamageContext;
import game.entity.plant.Plant;
import game.entity.zombie.Zombie;
import game.entity.zombie.ability.ZombieAbility;
import game.entity.zombie.data.properties.BossProperty;
import game.entity.zombie.data.properties.Property;
import game.entity.zombie.strategy.attack.AttackStrategy;
import game.entity.zombie.strategy.movement.MovementStrategy;
import game.entity.zombie.strategy.takedamage.TakingDamageStrategy;
import game.service.Zombie.ZombieService;
import game.world.GameWorld;
import graphics.general.effects.EffectLayer;
import graphics.general.effects.GameplayEffects;

import java.util.List;

public abstract class Zomboss extends Zombie {
    protected boolean introduced;
    protected BossProperty bossProperty;
    protected BossPhase currentPhase;
    private int stunPhasesCompleted;
    private boolean stunned;
    private boolean readyForOutroDialogue;
    private boolean outroDialogueFinished;
    private boolean deathSequenceComplete;

    public Zomboss(double currentHealth, MovementStrategy movementStrategy,
                   AttackStrategy attackStrategy, TakingDamageStrategy takingDamageStrategy,
                   Position position, List<ZombieAbility> abilities, GameWorld world,
                   Plant targetPlant, Property propertyData) {
        super(currentHealth, movementStrategy,
            attackStrategy, takingDamageStrategy,
            position, abilities, world, targetPlant,
            propertyData);
        this.bossProperty = (BossProperty) propertyData;
        this.currentPhase = BossPhase.PHASE_1;
        this.introduced = false;
    }

    public double getBaseHealth() {
        return bossProperty.getInitialHealth();
    }

    @Override
    public void update(ZombieService zombieService, long currentTick) {
        super.update(zombieService, currentTick);
        updatePhase();
    }

    private void updatePhase() {
        double healthPercentage = getCurrentHealth() / getPropertyData().getInitialHealth();

        if (healthPercentage <= 0.33) {
            currentPhase = BossPhase.PHASE_3;
        } else if (healthPercentage <= 0.66) {
            currentPhase = BossPhase.PHASE_2;
        } else {
            currentPhase = BossPhase.PHASE_1;
        }
    }

    public float getAttackChance() {
        return switch (currentPhase) {
            case PHASE_1 -> bossProperty.getPhase1AttackChance();
            case PHASE_2 -> bossProperty.getPhase2AttackChance();
            case PHASE_3 -> bossProperty.getPhase3AttackChance();
        };
    }

    public boolean isIntroduced() {
        return introduced;
    }

    public void setIntroduced(boolean introduced) {
        this.introduced = introduced;
    }

    public boolean isStunned() {
        return stunned;
    }

    @Override
    public boolean persistAfterDeath() {
        return true;
    }

    public void beginOutroDialogue() {
        readyForOutroDialogue = true;
    }

    public boolean isReadyForOutroDialogue() {
        return readyForOutroDialogue && !outroDialogueFinished;
    }

    public void finishOutroDialogue() {
        outroDialogueFinished = true;
    }

    public boolean isOutroDialogueFinished() {
        return outroDialogueFinished;
    }

    public void completeDeathSequence() {
        deathSequenceComplete = true;
    }

    public boolean isDeathSequenceComplete() {
        return deathSequenceComplete;
    }

    public boolean shouldEnterStun() {
        if (stunned || isDead()) {
            return false;
        }
        return stunPhasesCompleted < requiredStunPhases();
    }

    public void beginStun() {
        stunned = true;
        stunPhasesCompleted++;
        stopMovement();
        game.audio.Sfx.play(game.audio.AudioId.SFX_HYDRAULIC);
    }

    public void endStun() {
        stunned = false;
    }

    private int requiredStunPhases() {
        double healthPercentage = getCurrentHealth() / getBaseHealth();
        if (healthPercentage <= 1.0 / 3.0) {
            return 2;
        }
        if (healthPercentage <= 2.0 / 3.0) {
            return 1;
        }
        return 0;
    }

    @Override
    public void takeDamage(DamageContext context, long currentTick) {
        boolean wasDead = isDead();
        super.takeDamage(context, currentTick);
        updatePhase();
        if (!wasDead) {
            spawnPhaseDamageChunks();
        }
    }

    private void spawnPhaseDamageChunks() {
        String effectId = switch (currentPhase) {
            case PHASE_2 -> "zomboss damage chunks 2";
            case PHASE_3 -> "zomboss damage chunks 3";
            default -> null;
        };
        if (effectId == null) {
            return;
        }
        GameplayEffects.spawn(effectId, getPosition(), EffectLayer.FRONT);
    }

    public BossPhase getCurrentPhase() {
        return currentPhase;
    }

    public enum BossPhase {
        PHASE_1,
        PHASE_2,
        PHASE_3
    }
}
