package app.view.screen;

import app.enums.Menu;
import app.model.App;
import app.view.collection.CollectionPanel;
import plants_vs_zombies.PlantsVSZombies;

public class CollectionScreen extends BaseScreen {

    private CollectionPanel panel;

    public CollectionScreen(PlantsVSZombies game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();
        App.setCurrentMenu(Menu.CollectionMenu);
        panel = new CollectionPanel(game);
        stage.addActor(panel);
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
