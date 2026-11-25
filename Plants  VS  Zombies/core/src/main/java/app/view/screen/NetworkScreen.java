package app.view.screen;

import app.enums.Menu;
import app.model.App;
import app.view.network.NetworkMenuPanel;
import game.audio.AudioId;
import game.component.AssetManager;
import plants_vs_zombies.PlantsVSZombies;

public class NetworkScreen extends BaseScreen {
    private NetworkMenuPanel panel;

    public NetworkScreen(PlantsVSZombies game) {
        super(game);
    }

    @Override
    protected AudioId menuMusicId() {
        return AudioId.PVP_MENU;
    }

    @Override
    public void show() {
        super.show();
        App.setCurrentMenu(Menu.NetworkMenu);
        panel = new NetworkMenuPanel(game);
        stage.addActor(panel);
    }

    @Override
    public void render(float delta) {
        AssetManager.pump();
        super.render(delta);
    }

    @Override
    public void hide() {
        disposePanel();
        super.hide();
    }

    @Override
    public void dispose() {
        disposePanel();
        super.dispose();
    }

    private void disposePanel() {
        if (panel != null) {
            panel.dispose();
            panel = null;
        }
    }
}
