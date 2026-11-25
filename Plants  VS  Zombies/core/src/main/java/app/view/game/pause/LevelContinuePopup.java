package app.view.game.pause;

import app.view.ui.ModalOverlay;
import app.view.ui.SkinPanel;
import app.view.ui.button.ButtonRow;
import app.view.ui.button.ButtonSpec;
import app.view.ui.text.TextSpec;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import game.audio.AudioId;
import game.audio.AudioManager;

import java.util.Arrays;

public class LevelContinuePopup extends ModalOverlay {
    private static final String ID_CONTINUE = "continue";
    private static final String ID_NEW = "new";
    private static final String ID_CANCEL = "cancel";

    public interface Callback {
        void onContinueSaved();
        void onNewGame();
        void onCancel();
    }

    private final PauseLayout layout;
    private final boolean hasSave;
    private final Callback callback;
    private Label errorLabel;
    private ButtonRow actions;

    public LevelContinuePopup(String title, PauseLayout layout, boolean hasSave, Callback callback) {
        this.layout = layout == null ? PauseLayout.defaults() : layout;
        this.hasSave = hasSave;
        this.callback = callback;
        build(title);
    }

    private void build(String title) {
        SkinPanel root = new SkinPanel(layout.backgrounds.popupRoot);
        root.pad(layout.spacing.contentPadding);
        root.top();

        SkinPanel header = new SkinPanel(layout.backgrounds.header);
        header.pad(layout.spacing.tightPadding);
        header.add(TextSpec.of(layout.text.header).build("LEVEL")).grow();
        root.add(header).growX().height(layout.size.headerHeight).row();

        SkinPanel body = new SkinPanel(layout.backgrounds.section);
        body.pad(layout.spacing.innerPadding);
        String heading = (title == null || title.isBlank()) ? "This level" : title;
        body.add(TextSpec.of(layout.text.toggle).build(heading)).growX().row();
        String hint = hasSave
            ? "A saved game is available for this level."
            : "No saved game was found for this level.";
        body.add(TextSpec.of(layout.text.statLabel).build(hint)).growX().padTop(layout.spacing.tightPadding).row();
        errorLabel = TextSpec.of(layout.text.error).build("");
        body.add(errorLabel).growX().padTop(layout.spacing.tightPadding).row();
        root.add(body).grow().padTop(layout.spacing.sectionSpacing).row();

        actions = new ButtonRow(Arrays.asList(
            ButtonSpec.of(ID_CANCEL, "Cancel")
                .style(layout.buttons.restartStyle)
                .action(this::cancel)
                .fixedWidth(),
            ButtonSpec.of(ID_NEW, "New Game")
                .style(layout.buttons.saveExitStyle)
                .action(this::newGame)
                .fixedWidth(),
            ButtonSpec.of(ID_CONTINUE, "Continue Saved Game")
                .style(layout.buttons.continueStyle)
                .action(this::continueSaved)
                .fixedWidth()
        ), layout.buttons.actions);
        actions.setEnabled(ID_CONTINUE, hasSave);
        root.add(actions).growX().height(layout.size.footerHeight).padTop(layout.spacing.sectionSpacing);

        add(root).size(layout.size.width * 0.78f, 280f);
    }

    public void showError(String message) {
        if (errorLabel != null) {
            errorLabel.setText(message == null ? "" : message);
        }
        AudioManager.play(AudioId.UI_ERROR);
    }

    private void continueSaved() {
        if (!hasSave) {
            showError("No saved game is available.");
            return;
        }
        callback.onContinueSaved();
    }

    private void newGame() {
        callback.onNewGame();
    }

    private void cancel() {
        callback.onCancel();
        close();
    }
}
