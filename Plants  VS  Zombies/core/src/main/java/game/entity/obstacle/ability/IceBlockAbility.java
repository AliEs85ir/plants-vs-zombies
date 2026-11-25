package game.entity.obstacle.ability;

import app.enums.effect.EffectType;
import app.model.App;
import game.entity.effect.FreezeEffect;
import game.entity.obstacle.Obstacle;
import game.entity.zombie.Zombie;
import game.service.obstacle.ObstacleService;
import graphics.general.effects.EffectLayer;
import graphics.general.effects.EffectRequest;
import graphics.general.effects.anchers.WorldAnchor;

public class IceBlockAbility implements ObstacleAbility{
    private Zombie myZombie;
    private double currentHealth;
    private boolean dead;
    private boolean addedEffect;

    public IceBlockAbility(double currentHealth, Zombie myZombie) {
        this.currentHealth = currentHealth;
        this.myZombie = myZombie;
        this.dead = false;
        this.addedEffect = false;
    }

    @Override
    public void update(Obstacle obstacle, ObstacleService obstacleService, long currentTick) {
        if(dead) {
            EffectRequest<Object> request = new EffectRequest<>(
                "ice block death",
                null,
                new WorldAnchor(obstacle.getPosition()),
                EffectLayer.FRONT
            );
            App.getRealTimeEngine().getGraphicsEngine()
                .getEffectManager().spawn(request);
            game.audio.Sfx.play(game.audio.AudioId.SFX_FROZEN);

            obstacle.die();
            myZombie.removeEffect(EffectType.FREEZE);
        }

        if(!addedEffect) {
            myZombie.addEffect(new FreezeEffect(Integer.MAX_VALUE));
            game.audio.Sfx.play(game.audio.AudioId.SFX_FROZEN);
            addedEffect = true;
        }
    }

    @Override
    public void onDeath(Obstacle obstacle) {

    }

    public double getCurrentHealth() {
        return currentHealth;
    }

    public void takeDamage(int amount, Obstacle obstacle) {
        currentHealth -= amount;
        EffectRequest<Object> request = new EffectRequest<>(
            "ice block damage",
            null,
            new WorldAnchor(obstacle.getPosition()),
            EffectLayer.FRONT
        );
        App.getRealTimeEngine().getGraphicsEngine()
            .getEffectManager().spawn(request);
        if(currentHealth <= 0) {
            dead = true;
        }
    }
}
