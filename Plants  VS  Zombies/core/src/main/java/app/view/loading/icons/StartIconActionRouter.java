package app.view.loading.icons;

import app.view.loading.icons.config.StartIconsConfig;
import app.view.loading.icons.popup.StartPopupHost;
import com.badlogic.gdx.Gdx;

public final class StartIconActionRouter {
    private final StartPopupHost popups;

    public StartIconActionRouter(StartPopupHost popups) {
        this.popups = popups;
    }

    public void activate(StartIconsConfig.IconConfig icon) {
        if (icon == null || !icon.clickable()) {
            return;
        }
        String action = icon.action.trim().toLowerCase();
        switch (action) {
            case "exit" -> Gdx.app.exit();
            case "open_url" -> openUrl(icon.url);
            case "open_popup" -> {
                if (popups != null) {
                    popups.open(icon.popup);
                }
            }
            default -> Gdx.app.log("StartIconActionRouter", "Unknown action: " + icon.action);
        }
    }

    public static boolean openUrl(String url) {
        if (url == null || url.isBlank()) {
            return false;
        }
        boolean opened = Gdx.net.openURI(url);
        if (!opened) {
            Gdx.app.error("StartIconActionRouter", "Could not open URL: " + url);
        }
        return opened;
    }
}
