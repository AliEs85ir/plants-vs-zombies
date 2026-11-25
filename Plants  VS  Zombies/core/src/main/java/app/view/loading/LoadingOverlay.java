package app.view.loading;

import app.view.hud.elements.HudPamActor;
import app.view.loading.config.LoadingScreenConfig;
import app.view.loading.config.LoadingScreenConfigLoader;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import game.component.AssetManager;

public final class LoadingOverlay extends Table {
    private final LoadingScreenConfig.InGameOverlayConfig config;
    private HudPamActor icon;
    private float progress;

    public LoadingOverlay() {
        this(LoadingScreenConfigLoader.load().inGameOverlay);
    }

    public LoadingOverlay(LoadingScreenConfig.InGameOverlayConfig config) {
        this.config = config != null ? config : new LoadingScreenConfig.InGameOverlayConfig();
        this.config.sanitize();
        setFillParent(true);
        setVisible(false);
        setTouchable(Touchable.disabled);
        center();
    }

    public void show() {
        ensureIcon();
        if (icon != null) {
            icon.restart();
        }
        setVisible(true);
    }

    public void hide() {
        setVisible(false);
    }

    public void setProgress(float progress) {
        this.progress = Math.max(0f, Math.min(1f, progress));
    }

    public float progress() {
        return progress;
    }

    private void ensureIcon() {
        if (icon != null || !AssetManager.isCoreReady()) {
            return;
        }
        try {
            AssetManager.player.loadSync(config.pamPath);
            icon = new HudPamActor(
                config.pamPath,
                config.clip,
                config.loop,
                config.scale,
                config.offsetX,
                config.offsetY
            );
            clearChildren();
            add(icon).size(config.width, config.height);
        } catch (RuntimeException exception) {
            Gdx.app.error("LoadingOverlay", "Failed to create overlay icon", exception);
        }
    }
}
