package app.view.game.pregame.visual.providers;

import app.enums.plant.PlantName;
import app.view.hud.elements.plantcard.PlantCardConfigLoader;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;

public class StaticPlantVisualProvider implements PlantVisualProvider {

    @Override
    public Actor createVisual(PlantName plantName, float width, float height) {
        PlantCardConfigLoader.PlantCardConfig config =
            PlantCardConfigLoader.getConfig(plantName);

        TextureRegion region = (config != null) ? config.cachedPlantRegion : null;
        if (region == null) {
            return new Actor();
        }

        Image image = new Image(new TextureRegionDrawable(region));
        image.setScaling(Scaling.fit);
        image.setSize(width, height);
        return image;
    }
}
