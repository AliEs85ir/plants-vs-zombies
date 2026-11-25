package game.entity.obstacle.ability;

import app.enums.obstacle.ObstacleType;
import game.entity.obstacle.Obstacle;
import game.service.obstacle.ObstacleService;
import graphics.general.effects.EffectLayer;
import graphics.general.effects.GameplayEffects;
import graphics.general.event.ObstacleVisualEventType;

public class TakeDamageAbility implements ObstacleAbility{
    private double baseHealth;
    private double currentHealth;
    private boolean dead;
    private enum State {UnDamaged ,Damage1, Damage2, Damage3, Damage4}
    private State state;

    public TakeDamageAbility(double currentHealth) {
        this.currentHealth = currentHealth;
        this.baseHealth = currentHealth;
        this.state = State.UnDamaged;
        this.dead = false;
    }

    @Override
    public void update(Obstacle obstacle, ObstacleService obstacleService, long currentTick) {
        if(dead) {
            obstacle.die();
            return;
        }
        switch (state) {
            case UnDamaged -> {
                obstacle.pushEvent(ObstacleVisualEventType.UNDAMAGED);
                if(getPercentage() <= 0.8) {
                    state = State.Damage1;
                }
            }
            case Damage1 -> {
                obstacle.pushEvent(ObstacleVisualEventType.DAMAGE1);
                if(getPercentage() <= 0.6) {
                    state = State.Damage2;
                }
            }
            case Damage2 -> {
                obstacle.pushEvent(ObstacleVisualEventType.DAMAGE2);
                if(getPercentage() <= 0.4) {
                    state = State.Damage3;
                }
            }
            case Damage3 -> {
                obstacle.pushEvent(ObstacleVisualEventType.DAMAGE3);
                if(getPercentage() <= 0.2) {
                    state = State.Damage4;
                }
            }
            case Damage4 -> {
                obstacle.pushEvent(ObstacleVisualEventType.DAMAGE4);
                if(getPercentage() <= 0.2) {
                    state = State.Damage4;
                }
            }
        }

    }

    @Override
    public void onDeath(Obstacle obstacle) {

    }

    public double getCurrentHealth() {
        return currentHealth;
    }

    public void setCurrentHealth(double currentHealth) {
        this.currentHealth = currentHealth;
    }

    public void takeDamage(double amount, Obstacle obstacle) {
        if (amount <= 0 || dead) {
            return;
        }
        currentHealth -= amount;
        spawnTombDamage(obstacle);
        if(currentHealth <= 0) {
            dead = true;
        }
    }

    private void spawnTombDamage(Obstacle obstacle) {
        if (obstacle == null || obstacle.getType() == null) {
            return;
        }
        String effectId = tombDamageEffectId(obstacle.getType());
        if (effectId == null) {
            return;
        }
        GameplayEffects.spawn(effectId, obstacle.getPosition(), EffectLayer.FRONT);
        game.audio.Sfx.play(game.audio.AudioId.SFX_GRAVESTONE_RUMBLE);
    }

    private static String tombDamageEffectId(ObstacleType type) {
        return switch (type) {
            case EGYPT_GRAVE -> "egypt tomb damage";
            case TUTORIAL_GRAVE, SUN_GRAVE, PLANTFOOD_GRAVE -> "dark tomb damage";
            default -> null;
        };
    }

    private double getPercentage() {
        return currentHealth / baseHealth;
    }
}
