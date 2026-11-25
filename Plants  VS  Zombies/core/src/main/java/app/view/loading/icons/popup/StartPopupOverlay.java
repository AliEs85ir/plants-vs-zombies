package app.view.loading.icons.popup;

import app.view.loading.icons.config.StartIconsConfig;
import app.view.ui.ModalOverlay;
import com.badlogic.gdx.graphics.Color;

public final class StartPopupOverlay extends ModalOverlay {
    private final Color overlay;

    public StartPopupOverlay(StartIconsConfig.PopupConfig popup) {
        float opacity = popup != null ? popup.overlayOpacity : 0.72f;
        overlay = new Color(0f, 0f, 0f, opacity);
        getColor().a = 0f;
        clearActions();
    }

    @Override
    protected Color overlayColor() {
        return overlay;
    }
}
