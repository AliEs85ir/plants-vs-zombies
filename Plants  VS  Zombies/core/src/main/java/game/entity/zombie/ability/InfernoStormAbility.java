package game.entity.zombie.ability;

import app.enums.plant.PlantTag;
import app.model.App;
import game.engine.time.GameClock;
import game.entity.plant.Plant;
import game.entity.zombie.Zombie;
import game.service.Zombie.ZombieService;
import game.world.GameWorld;
import graphics.general.effects.EffectInstance;
import graphics.general.effects.EffectLayer;
import graphics.general.effects.EffectRequest;
import graphics.general.effects.GameplayEffects;
import graphics.general.effects.anchers.EntityAnchor;
import graphics.general.effects.anchers.WorldAnchor;

public class InfernoStormAbility extends AbstractZombieAbility {
    public static final double SPEED_MULTIPLIER = 3;

    private enum State {NOT_STARTED, STARTED, FINISHED, NOTHING}

    private State state;
    private float timer;
    private boolean rearLoopSpawned;
    private EffectInstance<?> rearLoopEffect;
    private Plant ignitedPlant;

    public InfernoStormAbility() {
        this.state = State.NOT_STARTED;
        this.timer = GameClock.TICKS_PER_SECOND * 5;
    }

    @Override
    public void update(Zombie zombie, ZombieService zombieService, long currentTick) {
        switch (state) {
            case NOT_STARTED -> {
                zombie.pushEffect("inferno storm start",
                    new EntityAnchor<>(zombie, zombie1 -> zombie1.getPosition().toVector()));
                spawnRear("inferno storm back start", zombie, true);
                zombie.setMoveSpeedMultiplier(SPEED_MULTIPLIER);
                state = State.STARTED;
            }
            case STARTED -> {
                zombie.pushEffect("inferno storm loop",
                    new EntityAnchor<>(zombie, zombie1 -> zombie1.getPosition().toVector()));
                if (!rearLoopSpawned) {
                    rearLoopEffect = spawnRear("inferno storm back loop", zombie, true);
                    rearLoopSpawned = true;
                }
                applyReachedPlant(zombie);
                timer -= 1;
                if (timer < 0) {
                    state = State.FINISHED;
                }
            }
            case FINISHED -> {
                zombie.pushEffect("inferno storm finish",
                    new EntityAnchor<>(zombie, zombie1 -> zombie1.getPosition().toVector()));
                stopRearLoop();
                spawnRear("inferno storm back finish", zombie, false);
                zombie.setMoveSpeedMultiplier(1);
                state = State.NOTHING;
            }
            case NOTHING -> {
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
        spawnRear("inferno storm back finish", zombie, false);
    }

    void applyReachedPlant(Zombie zombie) {
        if (zombie == null) {
            return;
        }
        Plant plant = zombie.getTargetPlant();
        if (!shouldDestroyPlant(plant) || plant == ignitedPlant) {
            return;
        }
        ignitedPlant = plant;
        GameWorld world = zombie.getWorld();
        if (world != null) {
            plant.markDead(world);
        }
    }

    static boolean shouldDestroyPlant(Plant plant) {
        if (plant == null || plant.isDead()) {
            return false;
        }
        return plant.getDefinition() == null || !plant.getDefinition().hasTag(PlantTag.FIRE);
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
                "inferno storm finish",
                zombie,
                new WorldAnchor(zombie.getPosition()),
                EffectLayer.FRONT
            )
        );
    }
}
