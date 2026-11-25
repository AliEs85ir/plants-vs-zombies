package app.view.game.pregame.visual.providers;

import app.enums.plant.PlantName;
import com.badlogic.gdx.scenes.scene2d.Actor;

public interface PlantVisualProvider {

    Actor createVisual(PlantName plantName, float width, float height);

    static PlantVisualProvider staticImage() {
        return new StaticPlantVisualProvider();
    }

    static PlantVisualProvider idleAnimation() {
        return new IdlePlantVisualProvider();
    }
}
