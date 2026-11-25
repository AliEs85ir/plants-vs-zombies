package app.view.adventure;

import app.view.loading.CoverImage;
import app.view.mainmenu.MenuTextures;
import app.view.screen.ScreenManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import plants_vs_zombies.PlantsVSZombies;

public final class AdventureMenuPanel extends WidgetGroup implements Disposable {
    private final AdventureMenuConfig config;
    private final CoverImage background = new CoverImage();
    private final Array<Texture> textures = new Array<>();
    private final AdventureCarousel carousel;
    private final AdventureHud hud;

    public AdventureMenuPanel(PlantsVSZombies game) {
        this.config = AdventureMenuConfigLoader.load();
        setFillParent(true);
        setTransform(false);
        setTouchable(Touchable.childrenOnly);
        Texture backgroundTexture = load(config.background.path);
        if (backgroundTexture != null) {
            background.setTexture(
                backgroundTexture,
                config.background.sourceWidth > 0f ? config.background.sourceWidth : backgroundTexture.getWidth(),
                config.background.sourceHeight > 0f ? config.background.sourceHeight : backgroundTexture.getHeight()
            );
            background.setOffsets(config.background.offsetX, config.background.offsetY);
        }
        background.setFillParent(true);
        addActor(background);
        AdventureChapterRouter router = new AdventureChapterRouter(game);
        carousel = new AdventureCarousel(config, router::activate);
        addActor(carousel);
        hud = new AdventureHud(game, () -> ScreenManager.showMainMenu(game));
        addActor(hud);
    }

    @Override
    public void layout() {
        background.setBounds(0f, 0f, getWidth(), getHeight());
        carousel.setBounds(0f, 0f, getWidth(), getHeight());
        hud.setBounds(0f, 0f, getWidth(), getHeight());
        hud.setZIndex(getChildren().size);
    }

    private Texture load(String path) {
        Texture texture = MenuTextures.load(path, "AdventureMenuPanel");
        if (texture != null) {
            textures.add(texture);
        }
        return texture;
    }

    @Override
    public void dispose() {
        carousel.dispose();
        hud.dispose();
        for (Texture texture : textures) {
            texture.dispose();
        }
        textures.clear();
    }
}
