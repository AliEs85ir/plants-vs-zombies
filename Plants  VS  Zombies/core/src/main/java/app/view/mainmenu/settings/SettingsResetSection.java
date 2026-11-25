package app.view.mainmenu.settings;

import app.view.ui.UiTheme;
import app.view.ui.button.ActionButton;
import app.view.ui.button.ButtonTheme;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

public final class SettingsResetSection extends Table {
    public SettingsResetSection(SettingsMenuConfig.ResetAudioConfig config, Runnable onReset) {
        left();
        ButtonTheme theme = new ButtonTheme()
            .size(config.buttonWidth, config.buttonHeight)
            .spacing(config.buttonMinWidth, 0f)
            .padding(config.buttonPadX, config.buttonPadY);
        theme.labelScale = config.buttonLabelScale;
        theme.hoverScale = config.buttonHoverScale;
        theme.hoverDuration = config.buttonHoverDuration;
        ActionButton button = new ActionButton(
            config.buttonLabel,
            UiTheme.buttonStyleOrDefault(config.buttonStyle),
            theme
        );
        button.onClick(onReset);
        int align = SettingsMenuConfig.align(config.alignment, Align.left | Align.top);
        var cell = add(button).width(config.buttonWidth).height(config.buttonHeight)
            .padLeft(config.offsetX)
            .padBottom(config.offsetY);
        if ((align & Align.right) != 0) {
            cell.right();
        } else if ((align & Align.center) == Align.center && (align & Align.left) == 0) {
            cell.center();
        } else {
            cell.left();
        }
    }
}
