package app.view.game.pregame.zombie.visual.providers;

import app.view.hud.elements.zombiecard.ZombieCardConfigLoader;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;

public class StaticZombieVisualProvider implements ZombieVisualProvider{

    @Override
    public Actor createVisual(String alias, float width, float height) {
        ZombieCardConfigLoader.ZombieCardConfig config =
            ZombieCardConfigLoader.getConfig(alias);

        TextureRegion region = config.cachedZombieRegion;
        if (region == null) {
            return new Actor();
        }

        Image image = new Image(new TextureRegionDrawable(region));
        image.setScaling(Scaling.fit);
        image.setSize(width, height);
        return image;
    }
}
