package app.view.screen;

import app.controller.menu.ProfileMenuController;
import app.model.App;
import app.model.user.User;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import common.Result;
import game.audio.AudioId;
import game.audio.AudioManager;
import plants_vs_zombies.PlantsVSZombies;

public class ProfileScreen extends BaseScreen {

    private Label usernameLabel;
    private Label nicknameLabel;
    private Label emailLabel;
    private Label gamesPlayedLabel;
    private Label coinsLabel;
    private Label gemsLabel;
    private Label chaptersPassedLabel;
    private Label mioPointLabel;

    private TextButton changeUsernameButton;
    private TextButton changeNicknameButton;
    private TextButton changeEmailButton;
    private TextButton changePasswordButton;
    private TextButton backButton;

    private final ProfileMenuController controller =
        new ProfileMenuController();

    public ProfileScreen(PlantsVSZombies game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();

        Stack stack = new Stack();
        stack.setFillParent(true);

        createUIElements();

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.center();

        addFeatures(mainTable);

        stack.add(mainTable);
        stage.addActor(stack);

        activateListeners();
    }

    private void createUIElements() {

        User user = App.getCurrentUser();

        usernameLabel = new Label(user.getUsername(), skin);
        nicknameLabel = new Label(user.getNickname(), skin);
        emailLabel = new Label(user.getEmail(), skin);

        gamesPlayedLabel =
            new Label(String.valueOf(user.getGamesPlayed()), skin);

        coinsLabel =
            new Label(String.valueOf(user.getCoins()), skin);

        gemsLabel =
            new Label(String.valueOf(user.getGems()), skin);

        int chaptersPassed = 0;

        if (user.getGameProgress() != null) {
            chaptersPassed =
                user.getGameProgress().getNumberOfLevelsCompleted();
        }

        chaptersPassedLabel =
            new Label(String.valueOf(chaptersPassed), skin);

        mioPointLabel =
            new Label(String.valueOf(user.getMioPoint()), skin);

        changeUsernameButton =
            new TextButton("Change Username", skin);

        changeNicknameButton =
            new TextButton("Change Nickname", skin);

        changeEmailButton =
            new TextButton("Change Email", skin);

        changePasswordButton =
            new TextButton("Change Password", skin);

        backButton =
            new TextButton("Back", skin);
    }

    private void addFeatures(Table table) {

        table.defaults()
            .width(350)
            .pad(5);

        table.add(new Label("PROFILE", skin))
            .colspan(2)
            .padBottom(15);

        table.row();

        addInfoRow(table, "Username", usernameLabel);
        addInfoRow(table, "Nickname", nicknameLabel);
        addInfoRow(table, "Email", emailLabel);
        addInfoRow(table, "Games Played", gamesPlayedLabel);
        addInfoRow(table, "Coins", coinsLabel);
        addInfoRow(table, "Gems", gemsLabel);
        addInfoRow(table, "Chapters Passed", chaptersPassedLabel);
        addInfoRow(table, "Meow Point", mioPointLabel);

        table.row();

        table.add(changeUsernameButton)
            .colspan(2)
            .width(300)
            .pad(8);

        table.row();

        table.add(changeNicknameButton)
            .colspan(2)
            .width(300)
            .pad(8);

        table.row();

        table.add(changeEmailButton)
            .colspan(2)
            .width(300)
            .pad(8);

        table.row();

        table.add(changePasswordButton)
            .colspan(2)
            .width(300)
            .pad(8);

        table.row();

        table.add(backButton)
            .colspan(2)
            .width(300)
            .padTop(15);
    }

    private void addInfoRow(
        Table table,
        String title,
        Label value
    ) {
        table.add(new Label(title, skin))
            .left()
            .width(160);

        table.add(value)
            .left()
            .width(190);

        table.row();
    }

    private void activateListeners() {

        changeUsernameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showUsernameDialog();
            }
        });

        changeNicknameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showNicknameDialog();
            }
        });

        changeEmailButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showEmailDialog();
            }
        });

        changePasswordButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showPasswordDialog();
            }
        });

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });
    }

    private void showUsernameDialog() {

        final TextField field = new TextField(
            App.getCurrentUser().getUsername(),
            skin
        );

        Dialog dialog = new Dialog("Change Username", skin);

        dialog.getContentTable().add(field)
            .width(300)
            .pad(10);

        dialog.getButtonTable().add(
            new TextButton("Change", skin)
        ).pad(5);

        dialog.getButtonTable().add(
            new TextButton("Cancel", skin)
        ).pad(5);

        TextButton changeButton =
            (TextButton) dialog.getButtonTable().getCells()
                .get(0).getActor();

        TextButton cancelButton =
            (TextButton) dialog.getButtonTable().getCells()
                .get(1).getActor();

        changeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.manageChangeUsername(field.getText(), result -> Gdx.app.postRunnable(() -> {
                    showMessage(result.message());
                    if (result.success()) {
                        usernameLabel.setText(
                            App.getCurrentUser().getUsername()
                        );
                        dialog.hide();
                    } else {
                        AudioManager.play(AudioId.UI_ERROR);
                    }
                }));
            }
        });

        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dialog.hide();
            }
        });

        dialog.show(stage);
    }

    private void showNicknameDialog() {

        final TextField field = new TextField(
            App.getCurrentUser().getNickname(),
            skin
        );

        Dialog dialog = new Dialog("Change Nickname", skin);

        dialog.getContentTable().add(field)
            .width(300)
            .pad(10);

        dialog.getButtonTable().add(
            new TextButton("Change", skin)
        ).pad(5);

        dialog.getButtonTable().add(
            new TextButton("Cancel", skin)
        ).pad(5);

        TextButton changeButton =
            (TextButton) dialog.getButtonTable().getCells()
                .get(0).getActor();

        TextButton cancelButton =
            (TextButton) dialog.getButtonTable().getCells()
                .get(1).getActor();

        changeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.manageChangeNickname(field.getText(), result -> Gdx.app.postRunnable(() -> {
                    showMessage(result.message());
                    if (result.success()) {
                        nicknameLabel.setText(
                            App.getCurrentUser().getNickname()
                        );
                        dialog.hide();
                    } else {
                        AudioManager.play(AudioId.UI_ERROR);
                    }
                }));
            }
        });

        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dialog.hide();
            }
        });

        dialog.show(stage);
    }

    private void showEmailDialog() {

        final TextField field = new TextField(
            App.getCurrentUser().getEmail(),
            skin
        );

        Dialog dialog = new Dialog("Change Email", skin);

        dialog.getContentTable().add(field)
            .width(300)
            .pad(10);

        dialog.getButtonTable().add(
            new TextButton("Change", skin)
        ).pad(5);

        dialog.getButtonTable().add(
            new TextButton("Cancel", skin)
        ).pad(5);

        TextButton changeButton =
            (TextButton) dialog.getButtonTable().getCells()
                .get(0).getActor();

        TextButton cancelButton =
            (TextButton) dialog.getButtonTable().getCells()
                .get(1).getActor();

        changeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.manageChangeEmail(field.getText(), result -> Gdx.app.postRunnable(() -> {
                    showMessage(result.message());
                    if (result.success()) {
                        emailLabel.setText(
                            App.getCurrentUser().getEmail()
                        );
                        dialog.hide();
                    } else {
                        AudioManager.play(AudioId.UI_ERROR);
                    }
                }));
            }
        });

        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dialog.hide();
            }
        });

        dialog.show(stage);
    }
    private void showPasswordDialog() {

        final TextField newPassword =
            new TextField("", skin);

        final TextField confirmPassword =
            new TextField("", skin);

        newPassword.setPasswordMode(true);
        newPassword.setPasswordCharacter('*');

        confirmPassword.setPasswordMode(true);
        confirmPassword.setPasswordCharacter('*');

        Dialog dialog = new Dialog("Change Password", skin);

        Table content = dialog.getContentTable();

        content.add(new Label("New Password", skin))
            .pad(5);

        content.add(newPassword)
            .width(250)
            .pad(5);

        content.row();

        content.add(new Label("Confirm Password", skin))
            .pad(5);

        content.add(confirmPassword)
            .width(250)
            .pad(5);

        dialog.getButtonTable().add(
            new TextButton("Change", skin)
        ).pad(5);

        dialog.getButtonTable().add(
            new TextButton("Cancel", skin)
        ).pad(5);

        TextButton changeButton =
            (TextButton) dialog.getButtonTable().getCells()
                .get(0).getActor();

        TextButton cancelButton =
            (TextButton) dialog.getButtonTable().getCells()
                .get(1).getActor();

        changeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.manageConfirmNewPassword(
                    newPassword.getText(),
                    confirmPassword.getText(),
                    result -> Gdx.app.postRunnable(() -> {
                        showMessage(result.message());
                        if (result.success()) {
                            dialog.hide();
                        } else {
                            AudioManager.play(AudioId.UI_ERROR);
                        }
                    })
                );
            }
        });

        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dialog.hide();
            }
        });

        dialog.show(stage);
    }

    private void showMessage(String message) {
        System.out.println(message);
    }
}
