package app.view.screen;

import app.enums.Menu;
import app.model.App;
import app.view.auth.AuthMenuPanel;
import app.view.auth.AuthViewState;
import plants_vs_zombies.PlantsVSZombies;

public class LoginScreen extends BaseScreen {
    private AuthMenuPanel panel;

    public LoginScreen(PlantsVSZombies game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();
        App.setCurrentMenu(Menu.LoginMenu);
        panel = new AuthMenuPanel(skin, game, AuthViewState.LOGIN);
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
