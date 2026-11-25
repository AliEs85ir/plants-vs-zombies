package app.view.screen;

import app.controller.menu.NewsMenuController;
import common.Result;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import plants_vs_zombies.PlantsVSZombies;

public class NewsScreen extends BaseScreen {

    private TextButton unreadButton;
    private TextButton allNewsButton;
    private TextButton backButton;

    private Label newsLabel;

    private final NewsMenuController controller =
        new NewsMenuController();

    public NewsScreen(PlantsVSZombies game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();

        Stack stack = new Stack();
        stack.setFillParent(true);

        createUIElements();

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        addFeatures(table);

        stack.add(table);
        stage.addActor(stack);

        activateListeners();
    }

    private void createUIElements() {

        unreadButton = new TextButton("Unread News", skin);
        allNewsButton = new TextButton("All News", skin);
        backButton = new TextButton("Back", skin);

        newsLabel = new Label(
            "Choose an option to view news.",
            skin
        );

        newsLabel.setWrap(true);
    }

    private void addFeatures(Table table) {

        table.defaults()
            .width(400)
            .pad(8);

        table.add(new Label("NEWS", skin))
            .padBottom(20);

        table.row();

        table.add(newsLabel)
            .width(500)
            .height(200)
            .padBottom(20);

        table.row();

        table.add(unreadButton)
            .width(300)
            .height(50);

        table.row();

        table.add(allNewsButton)
            .width(300)
            .height(50);

        table.row();

        table.add(backButton)
            .width(300)
            .height(50)
            .padTop(15);
    }

    private void activateListeners() {

        unreadButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                Result result = controller.showUnreadNews();

                newsLabel.setText(result.message());
            }
        });

        allNewsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                Result result = controller.showAllNews();

                newsLabel.setText(result.message());
            }
        });

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ScreenManager.showMainMenu(game);
            }
        });
    }
}
