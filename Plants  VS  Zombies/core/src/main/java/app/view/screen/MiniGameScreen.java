package app.view.screen;

import app.enums.Menu;
import app.model.App;
import app.view.minigame.MiniGameMenuPanel;
import game.component.AssetManager;
import plants_vs_zombies.PlantsVSZombies;

public class MiniGameScreen extends BaseScreen {
    private MiniGameMenuPanel panel;

    public MiniGameScreen(PlantsVSZombies game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();
        App.setCurrentMenu(Menu.MiniGameMenu);
        panel = new MiniGameMenuPanel(game);
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
