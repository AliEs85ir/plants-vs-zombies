package app.view.screen;

import app.enums.Menu;
import app.enums.cursor.CursorType;
import app.model.App;
import app.view.greenhouse.GreenhousePanel;
import game.audio.AudioId;
import plants_vs_zombies.PlantsVSZombies;

public class GreenhouseScreen extends BaseScreen {
    private GreenhousePanel panel;

    public GreenhouseScreen(PlantsVSZombies game) {
        super(game);
    }

    @Override
    protected AudioId menuMusicId() {
        return AudioId.ZEN_GARDEN;
    }

    @Override
    public void show() {
        super.show();
        App.setCurrentMenu(Menu.GreenhousMenu);
        panel = new GreenhousePanel(game);
        stage.addActor(panel);
    }

    @Override
    protected CursorType resolveCursorType(boolean interactive, boolean pressed) {
        if (panel != null && panel.isShovelSelected()) {
            return CursorType.SHOVEL;
        }
        return super.resolveCursorType(interactive, pressed);
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
