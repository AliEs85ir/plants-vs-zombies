package app.view.screen.shop;

import app.enums.Menu;
import app.model.App;
import app.view.screen.BaseScreen;
import app.view.shop.ShopPanel;
import plants_vs_zombies.PlantsVSZombies;

public class ShopScreen extends BaseScreen {

    private ShopPanel panel;

    public ShopScreen(PlantsVSZombies game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();
        App.setCurrentMenu(Menu.Shop);
        panel = new ShopPanel(game);
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
