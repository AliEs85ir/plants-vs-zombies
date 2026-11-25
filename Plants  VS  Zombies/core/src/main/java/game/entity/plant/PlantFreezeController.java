package game.entity.plant;

import app.model.App;
import game.entity.effect.FreezeEffect;
import graphics.general.effects.EffectConditions;
import graphics.general.effects.EffectLayer;
import graphics.general.effects.EffectRequest;
import graphics.general.effects.anchers.WorldAnchor;
import graphics.general.modifier.visualModifiers.ColdModifier;
import graphics.plant.PlantRenderInstance;

public class PlantFreezeController {
    private int freezeLevel;

    public PlantFreezeController() {
        this.freezeLevel = 0;
    }
    public void addFreeze(Plant plant) {
        freezeLevel++;
        if (freezeLevel == 1) {
            spawnChill(plant, "plant chill1", EffectConditions.plantFreeze1(this));
        }
        if (freezeLevel == 2) {
            spawnChill(plant, "plant chill2", EffectConditions.plantFreeze2(this));
        }

        if (freezeLevel >= 3) {
            plant.addEffect(new FreezeEffect(10));
            freezeLevel = 0;
            return;
        }
        if (App.getRealTimeEngine() == null || App.getRealTimeEngine().getGraphicsEngine() == null) {
            return;
        }
        PlantRenderInstance instance = App.getRealTimeEngine().getGraphicsEngine().getPlantInstance(plant);
        if (instance != null) {
            instance.getModifierController().add(new ColdModifier(1f));
        }
    }

    private static void spawnChill(Plant plant, String effectId, graphics.general.animation.condition.AnimationCondition<Object> condition) {
        if (App.getRealTimeEngine() == null || App.getRealTimeEngine().getGraphicsEngine() == null) {
            return;
        }
        App.getRealTimeEngine().getGraphicsEngine().getEffectManager().spawn(
            new EffectRequest<>(
                effectId,
                plant,
                new WorldAnchor(plant.getPosition()),
                EffectLayer.FRONT,
                condition
            )
        );
    }

    public void reduceFreeze(int levels) {
        if (levels <= 0 || freezeLevel <= 0) {
            return;
        }
        freezeLevel = Math.max(0, freezeLevel - levels);
    }

    public int getFreezeLevel() {
        return freezeLevel;
    }

    public void reset() {
        this.freezeLevel = 0;
    }
}
