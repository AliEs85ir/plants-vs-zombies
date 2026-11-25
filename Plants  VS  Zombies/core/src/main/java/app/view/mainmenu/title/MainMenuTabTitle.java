package app.view.mainmenu.title;

import app.view.mainmenu.MenuTextures;
import app.view.mainmenu.config.MainMenuConfig;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Scaling;

public final class MainMenuTabTitle extends WidgetGroup implements Disposable {
    private final MainMenuConfig.TabTitleConfig config;
    private final Image image = new Image();
    private final ObjectMap<String, Texture> textures = new ObjectMap<>();
    private String tabId = "game";
    private boolean disposed;

    public MainMenuTabTitle(MainMenuConfig.TabTitleConfig config) {
        this.config = config == null ? new MainMenuConfig.TabTitleConfig() : config;
        this.config.sanitize();
        setFillParent(true);
        setTransform(false);
        setTouchable(Touchable.disabled);
        image.setScaling(this.config.layout.keepAspectRatio ? Scaling.fit : Scaling.stretch);
        image.setAlign(Align.top | Align.center);
        addActor(image);
        showTab(tabId);
    }

    public void showTab(String id) {
        tabId = id == null || id.isBlank() ? "game" : id.trim().toLowerCase();
        MainMenuConfig.TabTitleTabConfig tab = config.tab(tabId);
        Texture texture = texture(tab);
        if (texture == null) {
            image.setDrawable(null);
            image.setVisible(false);
        } else {
            image.setDrawable(new TextureRegionDrawable(new TextureRegion(texture)));
            image.setVisible(true);
        }
        invalidate();
    }

    @Override
    public void layout() {
        TabTitleLayout.Placement placement = TabTitleLayout.place(
            getWidth(),
            getHeight(),
            config.layout,
            config.tab(tabId)
        );
        image.setBounds(placement.x, placement.y, placement.width, placement.height);
    }

    private Texture texture(MainMenuConfig.TabTitleTabConfig tab) {
        if (tab == null || tab.assetPath == null || tab.assetPath.isBlank()) {
            return null;
        }
        Texture cached = textures.get(tab.assetPath);
        if (cached != null) {
            return cached;
        }
        Texture loaded = MenuTextures.load(tab.assetPath, "MainMenuTabTitle");
        if (loaded != null) {
            textures.put(tab.assetPath, loaded);
        }
        return loaded;
    }

    @Override
    public void dispose() {
        if (disposed) {
            return;
        }
        disposed = true;
        for (Texture texture : textures.values()) {
            texture.dispose();
        }
        textures.clear();
        image.setDrawable(null);
    }
}
