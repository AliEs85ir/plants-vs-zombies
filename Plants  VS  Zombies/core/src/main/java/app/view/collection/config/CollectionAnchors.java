package app.view.collection.config;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import game.component.AssetManager;

public final class CollectionAnchors {
    private CollectionAnchors() {
    }

    public static void apply(Actor actor, CollectionConfig.AnchoredConfig config, float parentWidth, float parentHeight) {
        if (actor == null || config == null) {
            return;
        }
        float width = Math.max(1f, config.width * config.scale);
        float height = Math.max(1f, config.height * config.scale);
        apply(actor, config, parentWidth, parentHeight, width, height);
    }

    public static void apply(
        Actor actor,
        CollectionConfig.AnchoredConfig config,
        float parentWidth,
        float parentHeight,
        float width,
        float height
    ) {
        if (actor == null || config == null) {
            return;
        }
        actor.setSize(Math.max(1f, width), Math.max(1f, height));
        float originX = config.originX * actor.getWidth();
        float originY = config.originY * actor.getHeight();
        actor.setOrigin(originX, originY);
        actor.setRotation(config.rotation);
        float x = parentWidth * config.anchorX + config.offsetX - originX;
        float y = parentHeight * config.anchorY + config.offsetY - originY;
        actor.setPosition(x, y);
        Color color = actor.getColor();
        actor.setColor(color.r, color.g, color.b, config.alpha);
    }

    public static void applyImage(
        Image image,
        CollectionConfig.AnchoredConfig config,
        Texture texture,
        float parentWidth,
        float parentHeight
    ) {
        if (image == null || config == null) {
            return;
        }
        float width = Math.max(1f, config.width * config.scale);
        float height = Math.max(1f, config.height * config.scale);
        if (config.keepAspect && texture != null) {
            height = aspectHeight(texture, width, height);
        }
        apply(image, config, parentWidth, parentHeight, width, height);
    }

    public static void applyPanel(Actor actor, CollectionConfig.PanelConfig panel, float parentWidth, float parentHeight) {
        if (actor == null || panel == null) {
            return;
        }
        float width = Math.max(1f, parentWidth * panel.widthRatio);
        float height = Math.max(1f, parentHeight * panel.heightRatio);
        float x = (parentWidth - width) * panel.anchorX + panel.offsetX;
        float y = (parentHeight - height) * panel.anchorY + panel.offsetY;
        if (panel.marginTop > 0f) {
            y = panel.offsetY;
            height = Math.max(1f, parentHeight - panel.marginTop - y);
            x = (parentWidth - width) * panel.anchorX + panel.offsetX;
        }
        actor.setBounds(x, y, width, height);
    }

    public static float aspectHeight(Texture texture, float width, float fallbackHeight) {
        if (texture == null || texture.getWidth() <= 0) {
            return fallbackHeight;
        }
        return width * (texture.getHeight() / (float) texture.getWidth());
    }

    public static boolean hasRegion(String id) {
        return id != null && !id.isBlank()
            && AssetManager.textureBank != null
            && AssetManager.textureBank.region(id) != null;
    }
}
