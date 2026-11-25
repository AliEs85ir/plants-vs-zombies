package app.view.game.pause.section;

import app.view.game.pause.PauseLayout;
import app.view.ui.button.ButtonRow;
import app.view.ui.button.ButtonSpec;
import app.view.ui.text.TextSpec;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.util.Arrays;

public class ActionButtonsSection extends Table {
    public static final String ID_CONTINUE = "continue";
    public static final String ID_SAVE_EXIT = "save_exit";
    public static final String ID_RESTART = "restart";

    private final ButtonRow actions;
    private final Label errorLabel;

    public ActionButtonsSection(PauseLayout layout, Runnable onContinue, Runnable onSaveExit, Runnable onRestart) {
        bottom();
        actions = new ButtonRow(Arrays.asList(
            ButtonSpec.of(ID_CONTINUE, "Continue")
                .style(layout.buttons.continueStyle)
                .action(onContinue)
                .fixedWidth(),
            ButtonSpec.of(ID_SAVE_EXIT, "Save & Exit")
                .style(layout.buttons.saveExitStyle)
                .action(onSaveExit)
                .fixedWidth(),
            ButtonSpec.of(ID_RESTART, "Restart")
                .style(layout.buttons.restartStyle)
                .action(onRestart)
                .fixedWidth()
        ), layout.buttons.actions);
        errorLabel = TextSpec.of(layout.text.error).build("");
        errorLabel.setVisible(false);
        add(errorLabel).growX().padBottom(layout.spacing.tightPadding).row();
        add(actions).growX().height(layout.size.footerHeight);
    }

    public void showError(String message) {
        boolean hasMessage = message != null && !message.isBlank();
        errorLabel.setText(hasMessage ? message : "");
        errorLabel.setVisible(hasMessage);
    }

    public void clearError() {
        errorLabel.setText("");
        errorLabel.setVisible(false);
    }

    public ButtonRow actions() {
        return actions;
    }
}
