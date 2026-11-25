package app.view.screen;

import app.enums.Menu;
import app.model.App;
import app.view.auth.AuthMenuPanel;
import app.view.auth.AuthViewState;
import plants_vs_zombies.PlantsVSZombies;

public class SignupScreen extends BaseScreen {
    private AuthMenuPanel panel;

    public SignupScreen(PlantsVSZombies game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();
        App.setCurrentMenu(Menu.SignupMenu);
        panel = new AuthMenuPanel(skin, game, AuthViewState.SIGN_UP_STEP_1);
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
