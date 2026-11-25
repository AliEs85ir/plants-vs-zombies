package game.entity.zombie.ability;

import app.model.App;
import game.engine.time.GameClock;
import game.entity.zombie.Zombie;
import game.service.Zombie.ZombieService;
import graphics.general.effects.EffectInstance;
import graphics.general.effects.EffectLayer;
import graphics.general.effects.EffectRequest;
import graphics.general.effects.GameplayEffects;
import graphics.general.effects.anchers.EntityAnchor;
import graphics.general.effects.anchers.WorldAnchor;

public class SandStormAbility extends AbstractZombieAbility{

    private enum State {NOT_STARTED, STARTED, FINISHED, NOTHING}
    private State state;
    private float timer;
    private boolean rearLoopSpawned;
    private EffectInstance<?> rearLoopEffect;

    public SandStormAbility() {
        this.state = State.NOT_STARTED;
        this.timer = GameClock.TICKS_PER_SECOND * 5;
    }

    @Override
    public void update(Zombie zombie, ZombieService zombieService, long currentTick) {
        switch (state) {
            case NOT_STARTED -> {
                zombie.pushEffect("sand storm start",
                    new EntityAnchor<Zombie>(zombie, zombie1 -> {
                    return zombie1.getPosition().toVector();
                }));
                spawnRear("sand storm rear start", zombie, true);
                zombie.setMoveSpeedMultiplier(2.5);
                state = State.STARTED;
            }
            case STARTED -> {
                zombie.pushEffect("sand storm loop",
                    new EntityAnchor<Zombie>(zombie, zombie1 -> {
                        return zombie1.getPosition().toVector();
                    }));
                if (!rearLoopSpawned) {
                    rearLoopEffect = spawnRear("sand storm rear loop", zombie, true);
                    rearLoopSpawned = true;
                }
                timer -= 1;
                if(timer < 0) {
                    state = State.FINISHED;
                }
            }
            case FINISHED -> {
                zombie.pushEffect("sand storm finish",
                    new EntityAnchor<Zombie>(zombie, zombie1 -> zombie1.getPosition().toVector()));
                stopRearLoop();
                spawnRear("sand storm rear finish", zombie, false);
                zombie.setMoveSpeedMultiplier(1);
                state = State.NOTHING;
            }
            case NOTHING -> {
                return;
            }
        }
    }

    @Override
    public void onDeath(Zombie zombie) {
        if (state != State.STARTED) {
            return;
        }
        state = State.NOTHING;
        zombie.setMoveSpeedMultiplier(1);
        zombie.stopEffects();
        spawnFinishEffect(zombie);
        stopRearLoop();
        spawnRear("sand storm rear finish", zombie, false);
    }

    private EffectInstance<?> spawnRear(String effectId, Zombie zombie, boolean follow) {
        if (follow) {
            return GameplayEffects.spawnFollowingZombie(effectId, zombie, EffectLayer.BEHIND);
        }
        return GameplayEffects.spawn(effectId, zombie.getPosition(), EffectLayer.BEHIND);
    }

    private void stopRearLoop() {
        if (rearLoopEffect != null) {
            rearLoopEffect.forceFinish();
            rearLoopEffect = null;
        }
    }

    private void spawnFinishEffect(Zombie zombie) {
        if (App.getRealTimeEngine() == null || App.getRealTimeEngine().getGraphicsEngine() == null) {
            return;
        }
        App.getRealTimeEngine().getGraphicsEngine().getEffectManager().spawn(
            new EffectRequest<>(
                "sand storm finish",
                zombie,
                new WorldAnchor(zombie.getPosition()),
                EffectLayer.FRONT
            )
        );
    }
}
