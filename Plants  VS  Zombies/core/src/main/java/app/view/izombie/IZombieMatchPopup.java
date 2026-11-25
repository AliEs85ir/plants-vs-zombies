package app.view.izombie;

import app.view.game.pause.PauseLayout;
import app.view.ui.ModalOverlay;
import app.view.ui.ScrollPanes;
import app.view.ui.SkinPanel;
import app.view.ui.button.ButtonRow;
import app.view.ui.button.ButtonSpec;
import app.view.ui.text.TextSpec;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import game.audio.AudioId;
import game.audio.AudioManager;
import game.component.AssetManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class IZombieMatchPopup extends ModalOverlay {
    public enum View {
        CHOICE,
        FRIEND,
        WAITING
    }

    public interface Callback {
        void onPlayWithFriend();

        void onRandomQueue();

        void onInvite(String username);

        void onRefreshPlayers();

        void onCancel();
    }

    private static final String ID_FRIEND = "friend";
    private static final String ID_QUEUE = "queue";
    private static final String ID_INVITE = "invite";
    private static final String ID_REFRESH = "refresh";
    private static final String ID_BACK = "back";
    private static final String ID_CANCEL = "cancel";

    private final PauseLayout layout;
    private final Callback callback;
    private View view = View.CHOICE;
    private Label statusLabel;
    private TextField usernameField;
    private Table playerList;
    private final List<String> onlinePlayers = new ArrayList<>();

    public IZombieMatchPopup(Callback callback) {
        this.layout = PauseLayout.defaults();
        this.callback = callback;
        rebuild();
    }

    public void showFriendList(List<String> usernames) {
        onlinePlayers.clear();
        if (usernames != null) {
            onlinePlayers.addAll(usernames);
        }
        view = View.FRIEND;
        rebuild();
    }

    public void showWaiting(String message) {
        view = View.WAITING;
        rebuild();
        setStatus(message);
    }

    public void showChoice() {
        view = View.CHOICE;
        rebuild();
    }

    public void setStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message == null ? "" : message);
        }
    }

    public void showError(String message) {
        setStatus(message);
        AudioManager.play(AudioId.UI_ERROR);
    }

    private void rebuild() {
        clearChildren();
        SkinPanel root = new SkinPanel(layout.backgrounds.popupRoot);
        root.pad(layout.spacing.contentPadding);
        root.top();

        SkinPanel header = new SkinPanel(layout.backgrounds.header);
        header.pad(layout.spacing.tightPadding);
        header.add(TextSpec.of(layout.text.header).build("I, ZOMBIE")).grow();
        root.add(header).growX().height(layout.size.headerHeight).row();

        SkinPanel body = new SkinPanel(layout.backgrounds.section);
        body.pad(layout.spacing.innerPadding);
        body.top();
        if (view == View.CHOICE) {
            buildChoiceBody(body);
        } else if (view == View.FRIEND) {
            buildFriendBody(body);
        } else {
            buildWaitingBody(body);
        }
        root.add(body).grow().padTop(layout.spacing.sectionSpacing).row();
        root.add(buildFooter()).growX().height(layout.size.footerHeight).padTop(layout.spacing.sectionSpacing);

        float height = view == View.FRIEND ? 420f : 280f;
        add(root).size(layout.size.width * 0.72f, height);
    }

    private void buildChoiceBody(SkinPanel body) {
        body.add(TextSpec.of(layout.text.toggle).build("This I, Zombie stage is online.")).growX().row();
        body.add(TextSpec.of(layout.text.statLabel).build(
            "Play with a specific online player, or wait in a queue for a random opponent."
        )).growX().padTop(layout.spacing.tightPadding).row();
        statusLabel = TextSpec.of(layout.text.error).build("");
        body.add(statusLabel).growX().padTop(layout.spacing.tightPadding).row();
    }

    private void buildFriendBody(SkinPanel body) {
        body.add(TextSpec.of(layout.text.toggle).build("Invite an online player")).growX().row();
        usernameField = new TextField("", AssetManager.skin);
        usernameField.setMessageText("Username");
        body.add(usernameField).growX().height(36f).padTop(layout.spacing.tightPadding).row();

        playerList = new Table();
        playerList.top().left();
        if (onlinePlayers.isEmpty()) {
            playerList.add(TextSpec.of(layout.text.statLabel).build("No other players are online.")).left();
        } else {
            for (String username : onlinePlayers) {
                Label row = TextSpec.of(layout.text.channel).build(username);
                row.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        usernameField.setText(username);
                    }
                });
                playerList.add(row).left().growX().padBottom(4f).row();
            }
        }
        ScrollPane scroll = ScrollPanes.vertical(playerList, 8f);
        body.add(scroll).grow().padTop(layout.spacing.tightPadding).row();
        statusLabel = TextSpec.of(layout.text.error).build("");
        body.add(statusLabel).growX().padTop(layout.spacing.tightPadding).row();
    }

    private void buildWaitingBody(SkinPanel body) {
        body.add(TextSpec.of(layout.text.toggle).build("Waiting for opponent...")).growX().row();
        statusLabel = TextSpec.of(layout.text.statLabel).build("Stay on this screen until a match is found.");
        body.add(statusLabel).growX().padTop(layout.spacing.tightPadding).row();
    }

    private Table buildFooter() {
        if (view == View.CHOICE) {
            return new ButtonRow(Arrays.asList(
                ButtonSpec.of(ID_CANCEL, "Cancel")
                    .style(layout.buttons.restartStyle)
                    .action(this::cancel)
                    .fixedWidth(),
                ButtonSpec.of(ID_FRIEND, "Play with a friend")
                    .style(layout.buttons.saveExitStyle)
                    .action(callback::onPlayWithFriend)
                    .fixedWidth(),
                ButtonSpec.of(ID_QUEUE, "Random match")
                    .style(layout.buttons.continueStyle)
                    .action(callback::onRandomQueue)
                    .fixedWidth()
            ), layout.buttons.actions);
        }
        if (view == View.FRIEND) {
            return new ButtonRow(Arrays.asList(
                ButtonSpec.of(ID_BACK, "Back")
                    .style(layout.buttons.restartStyle)
                    .action(this::showChoice)
                    .fixedWidth(),
                ButtonSpec.of(ID_REFRESH, "Refresh")
                    .style(layout.buttons.saveExitStyle)
                    .action(callback::onRefreshPlayers)
                    .fixedWidth(),
                ButtonSpec.of(ID_INVITE, "Invite")
                    .style(layout.buttons.continueStyle)
                    .action(this::invite)
                    .fixedWidth()
            ), layout.buttons.actions);
        }
        return new ButtonRow(Arrays.asList(
            ButtonSpec.of(ID_CANCEL, "Cancel")
                .style(layout.buttons.restartStyle)
                .action(this::cancel)
                .fixedWidth()
        ), layout.buttons.actions);
    }

    private void invite() {
        String username = usernameField == null ? "" : usernameField.getText();
        if (username == null || username.isBlank()) {
            showError("Enter a username.");
            return;
        }
        callback.onInvite(username.trim());
    }

    private void cancel() {
        callback.onCancel();
        close();
    }
}
