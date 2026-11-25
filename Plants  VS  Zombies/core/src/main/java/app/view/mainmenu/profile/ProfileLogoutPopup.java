package app.view.mainmenu.profile;

import app.view.ui.Solid;
import app.view.ui.UiTheme;
import app.view.ui.button.ActionButton;
import app.view.ui.button.ButtonTheme;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

public final class ProfileLogoutPopup extends Table {
    public ProfileLogoutPopup(ProfileMenuConfig config, Runnable onConfirm, Runnable onCancel) {
        setFillParent(true);
        ProfileMenuConfig.LogoutPopupConfig popup = config.logoutPopup;
        setBackground(Solid.drawable(
            ProfileMenuConfig.color(popup.overlayColor, new Color(0f, 0f, 0f, 0.6f)),
            1f,
            1f
        ));
        Table panel = new Table();
        panel.setBackground(Solid.drawable(
            ProfileMenuConfig.color(popup.panelColor, Color.valueOf("1A140E")),
            popup.panelWidth,
            popup.panelHeight
        ));
        panel.pad(popup.panelPadY, popup.panelPadX, popup.panelPadY, popup.panelPadX);
        Label message = new Label(config.messages.logoutConfirm, new Label.LabelStyle(
            ProfileUiFonts.resolve(popup.messageFont, popup.messageFontFallback),
            ProfileMenuConfig.color(popup.messageColor, Color.valueOf("F2E7C4"))
        ));
        message.setFontScale(popup.messageFontSize);
        message.setAlignment(ProfileMenuConfig.textAlign(popup.messageAlignment));
        message.setWrap(true);
        panel.add(message).width(popup.panelWidth - popup.panelPadX * 2f).center();
        panel.row();
        Table buttons = new Table();
        buttons.add(button(popup, popup.confirmLabel, onConfirm)).width(popup.buttonWidth).height(popup.buttonHeight);
        buttons.add(button(popup, popup.cancelLabel, onCancel))
            .width(popup.buttonWidth)
            .height(popup.buttonHeight)
            .padLeft(popup.buttonGap);
        panel.add(buttons).padTop(popup.buttonTopPadding).center();
        add(panel).size(popup.panelWidth, popup.panelHeight).align(Align.center);
    }

    private static ActionButton button(
        ProfileMenuConfig.LogoutPopupConfig popup,
        String label,
        Runnable action
    ) {
        ButtonTheme theme = new ButtonTheme().size(popup.buttonWidth, popup.buttonHeight);
        theme.labelScale = popup.buttonLabelScale;
        theme.hoverScale = popup.buttonHoverScale;
        theme.hoverDuration = popup.buttonHoverDuration;
        ActionButton button = new ActionButton(label, UiTheme.buttonStyleOrDefault(popup.buttonStyle), theme);
        button.onClick(action);
        return button;
    }
}
