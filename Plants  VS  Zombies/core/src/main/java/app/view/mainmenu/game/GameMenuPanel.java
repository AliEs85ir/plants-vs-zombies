package app.view.mainmenu.game;

import app.view.cursor.GameCursor;
import app.view.mainmenu.MenuTextures;
import app.view.mainmenu.config.MainMenuConfig;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Scaling;

import java.util.List;
import java.util.function.Consumer;

public final class GameMenuPanel extends WidgetGroup implements Disposable {
    private final MainMenuConfig.GameMenuConfig config;
    private final Array<GameCardActor> actors = new Array<>();
    private final Array<Texture> textures = new Array<>();

    public GameMenuPanel(
        MainMenuConfig.GameMenuConfig config,
        GameCursor gameCursor,
        Consumer<MainMenuConfig.GameCardConfig> onActivate
    ) {
        this.config = config;
        setFillParent(true);
        setTransform(false);
        setTouchable(Touchable.childrenOnly);
        List<MainMenuConfig.GameCardConfig> cards = config.visibleCards();
        for (MainMenuConfig.GameCardConfig card : cards) {
            Texture texture = MenuTextures.load(card.assetPath, "GameMenuPanel");
            if (texture == null) {
                continue;
            }
            textures.add(texture);
            GameCardActor actor = new GameCardActor(texture, card, config.appearance, gameCursor);
            actor.setScaling(config.layout.keepAspectRatio ? Scaling.fit : Scaling.stretch);
            actor.setAction(() -> {
                if (onActivate != null) {
                    onActivate.accept(card);
                }
            });
            actors.add(actor);
            addActor(actor);
        }
    }

    @Override
    public void layout() {
        GameCardLayout.Placement[] placements = GameCardLayout.place(
            actors.size,
            getWidth(),
            getHeight(),
            config.layout
        );
        for (int i = 0; i < actors.size && i < placements.length; i++) {
            GameCardLayout.Placement placement = placements[i];
            GameCardActor actor = actors.get(i);
            MainMenuConfig.GameCardConfig definition = actor.definition();
            float width = definition.width > 0f ? definition.width : placement.width;
            float height = definition.height > 0f ? definition.height : placement.height;
            actor.setBounds(
                placement.x + definition.offsetX + actor.hoverOffsetX(),
                placement.y + definition.offsetY + actor.hoverOffsetY(),
                Math.max(1f, width),
                Math.max(1f, height)
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
}
