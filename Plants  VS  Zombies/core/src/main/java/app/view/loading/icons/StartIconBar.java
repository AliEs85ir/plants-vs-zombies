package app.view.loading.icons;

import app.view.cursor.GameCursor;
import app.view.loading.InternalFiles;
import app.view.loading.icons.config.StartIconsConfig;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import java.util.List;
import java.util.function.Consumer;

public final class StartIconBar extends WidgetGroup implements Disposable {
    private final StartIconsConfig config;
    private final Array<StartIconActor> actors = new Array<>();
    private final Array<Texture> textures = new Array<>();

    public StartIconBar(StartIconsConfig config, GameCursor gameCursor, Consumer<StartIconsConfig.IconConfig> onActivate) {
        this.config = config;
        setFillParent(true);
        setTransform(false);
        setTouchable(Touchable.childrenOnly);
        List<StartIconsConfig.IconConfig> icons = config.visibleIcons();
        for (StartIconsConfig.IconConfig icon : icons) {
            Texture texture = loadTexture(icon.assetPath);
            if (texture == null) {
                Gdx.app.error("StartIconBar", "Missing icon: " + icon.assetPath);
                continue;
            }
            textures.add(texture);
            StartIconActor actor = new StartIconActor(texture, icon, config.hover, gameCursor);
            actor.setAction(() -> {
                if (onActivate != null) {
                    onActivate.accept(icon);
                }
            });
            actors.add(actor);
            addActor(actor);
        }
    }

    @Override
    public void layout() {
        StartIconLayout.Placement[] placements = StartIconLayout.place(
            actors.size,
            getWidth(),
            getHeight(),
            config.layout
        );
        for (int i = 0; i < actors.size && i < placements.length; i++) {
            StartIconLayout.Placement placement = placements[i];
            StartIconActor actor = actors.get(i);
            StartIconsConfig.IconConfig definition = actor.definition();
            float width = definition.width > 0f ? definition.width : placement.width;
            float height = definition.height > 0f ? definition.height : placement.height;
            float pad = Math.max(0f, definition.padding);
            float x = definition.hasPosition ? definition.x : placement.x + definition.offsetX;
            float y = definition.hasPosition ? definition.y : placement.y + definition.offsetY;
            y += actor.hoverLift();
            actor.setBounds(
                x + pad,
                y + pad,
                Math.max(1f, width - pad * 2f),
                Math.max(1f, height - pad * 2f)
            );
        }
    }

    @Override
    public void dispose() {
        for (Texture texture : textures) {
            texture.dispose();
        }
        textures.clear();
        actors.clear();
    }

    private static Texture loadTexture(String path) {
        var file = InternalFiles.resolve(path);
        if (file == null || !file.exists()) {
            return null;
        }
        Texture texture = new Texture(file);
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        return texture;
    }
}
