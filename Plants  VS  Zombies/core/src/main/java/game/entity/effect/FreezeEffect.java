package game.entity.effect;

import app.enums.effect.EffectType;
import app.model.App;
import game.entity.plant.Plant;
import game.entity.zombie.Zombie;
import graphics.general.effects.EffectConditions;
import graphics.general.effects.EffectLayer;
import graphics.general.effects.EffectRequest;
import graphics.general.effects.anchers.WorldAnchor;
import graphics.general.modifier.visualModifiers.FreezeModifier;
import graphics.plant.PlantRenderInstance;
import graphics.zombie.ZombieRenderInstance;

public class FreezeEffect
        extends TimedEffect
{
    public FreezeEffect(float durationInSecond) {
        super(durationInSecond, EffectType.FREEZE);
    }

    @Override
    public void onApply(Zombie zombie) {
        setAnimationFrozen(zombie, true);
    }

    @Override
    public void onApply(Plant plant) {
        if (App.getRealTimeEngine() != null && App.getRealTimeEngine().getGraphicsEngine() != null) {
            EffectRequest<Plant> freezeEffect = new EffectRequest<>(
                "plant ice block",
                plant,
                new WorldAnchor(plant.getPosition()),
                EffectLayer.BEHIND,
                EffectConditions.and(EffectConditions.plantAlive(plant), EffectConditions.withinSeconds(durationSeconds))
            );
            App.getRealTimeEngine().getGraphicsEngine()
                .getEffectManager().spawn(freezeEffect);
        }
        plant.setCanAct(false);
        setAnimationFrozen(plant, true);
    }

    @Override
    public void onRemove(Plant plant) {
        plant.setCanAct(true);
        setAnimationFrozen(plant, false);
    }

    @Override
    public void onRemove(Zombie zombie) {
        setAnimationFrozen(zombie, false);
    }

    private void setAnimationFrozen(Plant plant, boolean frozen) {
        if (App.getRealTimeEngine() == null || App.getRealTimeEngine().getGraphicsEngine() == null) {
            return;
        }
        PlantRenderInstance instance = App.getRealTimeEngine()
            .getGraphicsEngine()
            .getPlantInstance(plant);
        if (instance != null) {
            instance.getAnimationController().setTimeScale(frozen ? 0f : 1f);
            if (frozen) {
                instance.getModifierController().add(new FreezeModifier(durationSeconds));
            }
        }
    }

    private void setAnimationFrozen(Zombie zombie, boolean frozen) {
        if (App.getRealTimeEngine() == null || App.getRealTimeEngine().getGraphicsEngine() == null) {
            return;
        }
        ZombieRenderInstance instance = App.getRealTimeEngine()
            .getGraphicsEngine()
            .getZombieInstance(zombie);
        if (instance != null) {
            instance.getAnimationController().setTimeScale(frozen ? 0f : 1f);
            if (frozen) {
                instance.getModifierController().add(new FreezeModifier(durationSeconds));
            }
        }
    }
}
