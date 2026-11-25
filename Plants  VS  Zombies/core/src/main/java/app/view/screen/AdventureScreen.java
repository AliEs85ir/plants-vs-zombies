package app.view.screen;

import app.enums.Menu;
import app.model.App;
import app.view.adventure.AdventureMenuPanel;
import game.audio.AudioId;
import game.component.AssetManager;
import plants_vs_zombies.PlantsVSZombies;

public class AdventureScreen extends BaseScreen {
    private AdventureMenuPanel panel;

    public AdventureScreen(PlantsVSZombies game) {
        super(game);
    }

    @Override
    protected AudioId menuMusicId() {
        return AudioId.WORLD_MAP;
    }

    @Override
    public void show() {
        super.show();
        App.setCurrentMenu(Menu.GameMenu);
        panel = new AdventureMenuPanel(game);
        stage.addActor(panel);
    }

    @Override
    public void render(float delta) {
        AssetManager.pump();
        super.render(delta);
    }

    @Override
    public void dispose() {
        if (panel != null) {
            panel.dispose();
            panel = null;
        }
        super.dispose();
    }
}
