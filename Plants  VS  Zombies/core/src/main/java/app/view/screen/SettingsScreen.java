package app.view.screen;

import app.view.mainmenu.settings.SettingsMenuPanel;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import plants_vs_zombies.PlantsVSZombies;

public class SettingsScreen extends BaseScreen {
    public SettingsScreen(PlantsVSZombies game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();
        Table root = new Table();
        root.setFillParent(true);
        root.top();
        root.add(new SettingsMenuPanel(skin)).grow().pad(12f);
        root.row();
        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ScreenManager.showMainMenu(game);
            }
        });
        root.add(backButton).width(300f).height(50f).padBottom(18f);
        stage.addActor(root);
    }
}
